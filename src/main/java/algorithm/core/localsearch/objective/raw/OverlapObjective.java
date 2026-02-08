package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;

import java.util.List;

import static utils.Utils.calculateAllowedOverlap;


public class OverlapObjective implements Objective<OverlapPackingSolution> {

    public double evaluate(OverlapPackingSolution solution) {
        int numBoxes = solution.boxes().size();

        double thresholdRatio = calculateAllowedOverlap(
                solution.currentIteration, solution.maxIterations);
        double thresholdPercent = thresholdRatio * 100.0;

        // Calculate excess overlap (overlap above threshold)
        double totalOverlapRate = 0;
        for (Box box : solution.boxes()) {
            List<Rectangle> rects = box.getRectangles();
            for (int i = 0; i < rects.size(); i++) {
                for (int j = i + 1; j < rects.size(); j++) {
                    double overlapRate = box.overlapRate(rects.get(i), rects.get(j));
                    if (overlapRate > 0) {
                        totalOverlapRate += (overlapRate);
                    }
                }
            }
        }

        double score = totalOverlapRate;

        // Debug output (remove in production)
        System.out.printf("[Obj] Iter %3d: threshold=%5.1f%%, boxes=%2d, " + "score=%-8.1f",
                solution.currentIteration, thresholdPercent, numBoxes, score);

        return score;
    }
}