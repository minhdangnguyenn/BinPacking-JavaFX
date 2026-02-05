package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.PackingSolution;

import java.util.List;

public class OverlapObjective implements Objective<PackingSolution> {
    private static final int PENALTY_WEIGHT = 1000;
    private double allowedOverlapPercent = 100.0;

    @Override
    public double evaluate(PackingSolution solution) {
        List<Box> boxes = solution.boxes();
        if (boxes.isEmpty()) {
            return 0.0;
        }

        long totalUsedArea = 0;
        double overlapPenalty = 0.0;

        double allowedOverlapPercent = solution.allowedOverlapPercent();

        for (Box box : boxes) {
            totalUsedArea += (int) Math.pow(box.getUtilization(), 2);

            List<Rectangle> rectangles = box.getRectangles();
            for (int i = 0; i < rectangles.size(); i++) {
                for (int j = i + 1; j < rectangles.size(); j++) {
                    // calculate the overlap rate of two nearby rectangle iteration
                    double overlapRate = box.overlapRate(rectangles.get(i), rectangles.get(j));
                    if (overlapRate > allowedOverlapPercent) {
                        double violation = overlapRate - allowedOverlapPercent;
                        overlapPenalty += violation * violation;
                    }
                }
            }
        }

        double baseScore = (double) -totalUsedArea / boxes.size();

        return baseScore + (PENALTY_WEIGHT * overlapPenalty);
    }
}
