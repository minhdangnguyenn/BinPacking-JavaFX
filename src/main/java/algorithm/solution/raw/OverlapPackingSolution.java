package algorithm.solution.raw;

import algorithm.model.Box;
import algorithm.model.Rectangle;

import java.util.List;
import java.util.Random;

public class OverlapPackingSolution extends PackingSolution {

    public int currentIteration = 0;
    public int maxIterations;

    public OverlapPackingSolution(List<Box> boxes) {
        super(boxes);
    }

    public OverlapPackingSolution(int boxSize) {
        super(boxSize);
    }

    public static OverlapPackingSolution init(Box box, int maxIterations) {
        OverlapPackingSolution solution = new OverlapPackingSolution(box.getLength());
        solution.currentIteration = 0;
        solution.maxIterations = maxIterations;

        Random random = new Random();
        int numBoxes = random.nextInt(box.getRectangles().size() - box.getRectangles().size() / 4) + (box.getRectangles().size() / 4);

        for (int i = 0; i < numBoxes; i++) {
            solution.addBox(new Box(i, box.getLength()));
        }

        for (Rectangle rectangle : box.getRectangles()) {
            int boxIndex = random.nextInt(numBoxes);

            int X = random.nextInt(box.getLength() - rectangle.getWidth() + 1);
            int Y = random.nextInt(box.getLength() - rectangle.getHeight() + 1);

            solution.boxes().get(boxIndex).addRectangle(rectangle, X, Y);
        }

        // Clear empty boxes
        solution.boxes().removeIf(removeBox -> removeBox.getRectangles().isEmpty());

        return solution;
    }

    public Box highestOverlapBox() {
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

    public Box leastUsedBox() {
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

    @Override
    public OverlapPackingSolution copy() {
        PackingSolution newBase = super.copy();
        OverlapPackingSolution newSolution = new OverlapPackingSolution(newBase.boxes());
        newSolution.currentIteration = this.currentIteration;
        newSolution.maxIterations = this.maxIterations;
        return newSolution;
    }

    public static OverlapPackingSolution getFromPackingSolution(
            PackingSolution base,
            int maxIterations
    ) {
        // Deep copy boxes
        List<Box> copiedBoxes = base.boxes().stream()
                .map(Box::copy)
                .toList();

        OverlapPackingSolution solution =
                new OverlapPackingSolution(copiedBoxes);

        solution.currentIteration = 0;
        solution.maxIterations = maxIterations;

        return solution;
    }
}