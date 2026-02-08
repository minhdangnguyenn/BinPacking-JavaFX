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

        System.out.println("[DEBUG] Evaluating solution with iteration: " +
                solution.currentIteration + "/" + solution.maxIterations);

        double thresholdRatio = calculateAllowedOverlap(
                solution.currentIteration, solution.maxIterations);
        double thresholdPercent = thresholdRatio * 100.0;

        System.out.printf("[DEBUG] thresholdRatio=%.4f, thresholdPercent=%.1f%%%n",
                thresholdRatio, thresholdPercent);

        // Calculate excess overlap (overlap above threshold)
        double totalExcessOverlap = 0;
        for (Box box : solution.boxes()) {
            List<Rectangle> rects = box.getRectangles();
            for (int i = 0; i < rects.size(); i++) {
                for (int j = i + 1; j < rects.size(); j++) {
                    double overlap = box.overlapRate(rects.get(i), rects.get(j)); // 0-100
                    if (overlap > thresholdPercent) {
                        totalExcessOverlap += (overlap - thresholdPercent);
                    }
                }
            }
        }

        // Determine stage based on progress
        double progress = (double) solution.currentIteration / solution.maxIterations;

        double boxWeight, overlapWeight;

        if (progress < 0.3) {
            // Early stage: focus on reducing boxes
            boxWeight = 100.0;
            overlapWeight = 10.0;
        }
        else if (progress < 0.8) {
            // Middle stage: balance
            boxWeight = 50.0;
            overlapWeight = 50.0;
        }
        else {
            // Final stage: eliminate overlaps
            boxWeight = 20.0;
            overlapWeight = 200.0;
        }

        // Calculate final score
        double score = boxWeight * numBoxes + overlapWeight * totalExcessOverlap;

        // Debug output (remove in production)
        System.out.printf("[Obj] Iter %3d: threshold=%5.1f%%, boxes=%2d, " +
                        "excess=%-6.1f, score=%-8.1f (box:%-6.1f, overlap:%-6.1f)%n",
                solution.currentIteration, thresholdPercent, numBoxes,
                totalExcessOverlap, score,
                boxWeight * numBoxes, overlapWeight * totalExcessOverlap);

        return score;
    }
}