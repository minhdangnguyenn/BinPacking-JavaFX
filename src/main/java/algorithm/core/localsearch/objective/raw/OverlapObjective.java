package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;

import java.util.List;


public class OverlapObjective implements Objective<OverlapPackingSolution> {

    // Trả về threshold dưới dạng % (0-100)
    private double OverlapThreshold(int iteration, int maxIterations) {
        if (maxIterations <= 0) return 0;
        double progress = (double) iteration / maxIterations;
        // Bắt đầu 80%, kết thúc 5%
        return 80.0 * Math.pow(0.1, progress);  // 80% → 8% (không phải 5%, nhưng ổn)
    }

    // Penalty tăng khi threshold giảm
    private double penaltyFactor(double thresholdPercent) {
        // threshold: 80% → 8%
        if (thresholdPercent > 60) return 1;
        else if (thresholdPercent > 40) return 5;
        else if (thresholdPercent > 20) return 20;
        else if (thresholdPercent > 10) return 50;
        else if (thresholdPercent > 5) return 100;
        else return 200;
    }

    public double evaluate(OverlapPackingSolution solution) {
        int numBoxes = solution.boxes().size();

        // Threshold dưới dạng %
        double thresholdPercent = OverlapThreshold(
                solution.currentIteration, solution.maxIterations);
        double penalty = penaltyFactor(thresholdPercent);

        // Tính overlap vượt quá threshold
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

        // Box weight cố định
        double boxWeight = 50.0;

        // Score
        double score = boxWeight * numBoxes + penalty * totalExcessOverlap;

        // Debug
        System.out.printf("Iter %d: boxes=%d, threshold=%.1f%%, " +
                        "excess=%.1f, penalty=%.0f, score=%.1f%n",
                solution.currentIteration, numBoxes, thresholdPercent,
                totalExcessOverlap, penalty, score);

        return score;
    }
}