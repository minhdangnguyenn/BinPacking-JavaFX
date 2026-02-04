package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.PackingSolution;

import java.util.List;

public class OverlapObjective implements Objective<PackingSolution> {
    private static final double PENALTY_WEIGHT = 1000.0;
    private double allowedOverlapPercent = 100.0;

    public void setAllowedOverlap(double allowedOverlapPercent) {
        this.allowedOverlapPercent = allowedOverlapPercent;
    }

    @Override
    public double evaluate(PackingSolution solution) {
        List<Box> boxes = solution.boxes();
        long totalUsedArea = 0;
        double overlapPenalty = 0.0;

        for (Box box : boxes) {
            totalUsedArea += (int) Math.pow(box.getUtilization(), 2);

            List<Rectangle> rectangles = box.getRectangles();
            for (int i = 0; i < rectangles.size(); i++) {
                for (int j = i + 1; j < rectangles.size(); j++) {
                    double overlapRate = box.overlapRate(rectangles.get(i), rectangles.get(j));
                    if (overlapRate > allowedOverlapPercent) {
                        overlapPenalty += (overlapRate - allowedOverlapPercent);
                    }
                }
            }
        }

        double baseScore = (double) -totalUsedArea / boxes.size();
        return baseScore + (PENALTY_WEIGHT * overlapPenalty);
    }
}
