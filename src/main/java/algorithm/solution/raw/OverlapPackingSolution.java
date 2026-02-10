package algorithm.solution.raw;

import algorithm.model.Box;

import java.util.List;

public class OverlapPackingSolution extends PackingSolution {

    public int currentIteration = 0;
    public int maxIterations;

    public OverlapPackingSolution(List<Box> boxes) {
        super(boxes);
    }

    public OverlapPackingSolution(int boxSize) {
        super(boxSize);
    }

    public static OverlapPackingSolution init(List<Box> boxes, int maxIterations) {
        OverlapPackingSolution solution = new OverlapPackingSolution(boxes.getFirst().getLength());
        solution.currentIteration = 0;
        solution.maxIterations = maxIterations;

        solution.boxes().addAll(boxes);
        solution.boxes().removeIf(box -> box.getRectangles().isEmpty());

        return solution;
    }

    public Box getHighestOverlapBox() {
        Box highestOverlapBox = null;
        double highestOverlapRate = -1.0;

        for (Box box : boxes()) {
            double overlapRate = box.totalOverlapRate();
            if (overlapRate > highestOverlapRate) {
                highestOverlapRate = overlapRate;
                highestOverlapBox = box;
            }
        }
        return highestOverlapBox;
    }

    public Box getLeastUsedBox() {
        Box leastUsedBox = null;
        double lowestUtilization = Double.MAX_VALUE;

        for (Box box : boxes()) {
            double utilization = box.getUtilization();
            if (utilization < lowestUtilization) {
                lowestUtilization = utilization;
                leastUsedBox = box;
            }
        }
        return leastUsedBox;
    }

    public Box getMostUsedBox() {
        Box highestUsedBox = null;
        double highestUtilization = Double.MIN_VALUE;

        for (Box box : boxes()) {
            double utilization = box.getUtilization();
            if (utilization > highestUtilization) {
                highestUtilization = utilization;
                highestUsedBox = box;
            }
        }
        return highestUsedBox;
    }

    @Override
    public OverlapPackingSolution copy() {
        PackingSolution newBase = super.copy();
        OverlapPackingSolution newSolution = new OverlapPackingSolution(newBase.boxes());
        newSolution.currentIteration = this.currentIteration;
        newSolution.maxIterations = this.maxIterations;
        return newSolution;
    }
}