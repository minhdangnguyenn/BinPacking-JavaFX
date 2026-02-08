package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;

import java.util.List;

import static utils.Utils.calculateAllowedOverlap;


public class OverlapObjective implements Objective<OverlapPackingSolution> {

    public double penalty(Box box, int currentIteration, int maxIteration) {

        double progress = (double) currentIteration / maxIteration;

        double sumOverlap = 0.0;
        double maxOverlap = 0.0;
        int overlapCount = 0;

        List<Rectangle> rects = box.getRectangles();
        for (int i = 0; i < rects.size(); i++) {
            for (int j = i + 1; j < rects.size(); j++) {
                double overlap = box.overlapRate(rects.get(i), rects.get(j)); // 0–1 or 0–100
                if (overlap > 0.0) {
                    sumOverlap += overlap;
                    maxOverlap = Math.max(maxOverlap, overlap);
                    overlapCount++;
                }
            }
        }

        double alpha = 1.0;
        double beta  = 10.0 * progress * progress;
        double gamma = 0.1 * progress;

        return alpha * sumOverlap
                + beta  * maxOverlap
                + gamma * overlapCount;
    }

    @Override
    public double evaluate(OverlapPackingSolution solution) {

        int numBoxes = solution.boxes().size();

        double totalPenalty = 0.0;
        for (Box box : solution.boxes()) {
            totalPenalty += penalty(
                    box,
                    solution.currentIteration,
                    solution.maxIterations
            );
        }

        // MAIN OBJECTIVE:
        // fewer boxes is always better
        // overlap becomes expensive over time
        return 10 * numBoxes + totalPenalty;
    }
}