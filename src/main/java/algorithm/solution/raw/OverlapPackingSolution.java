package algorithm.solution.raw;

import algorithm.model.Box;
import algorithm.model.Rectangle;

import java.util.ArrayList;
import java.util.Comparator;
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
        List<Box> copiedBoxes = new java.util.ArrayList<>(base.boxes().stream()
                .map(Box::copy)
                .toList());

        copiedBoxes.sort(
                Comparator.comparing(
                        (Box box) -> box.getRectangles().getFirst().getArea()
                ).reversed()
        );

        OverlapPackingSolution solution =
                new OverlapPackingSolution(copiedBoxes);

        solution.currentIteration = 0;
        solution.maxIterations = maxIterations;

        return solution;
    }

    public List<Rectangle> getAllRectangles() {
        List<Rectangle> allRectangles = new ArrayList<>();

        for (Box box: this.boxes()) {
            allRectangles.addAll(box.getRectangles());
        }

        return allRectangles;
    }

    public int getBoxLength() {
        return this.boxes().getFirst().getLength();
    }

    public Rectangle findRectangleById(int id) {
        for (Box box: this.boxes()) {
            for (Rectangle rect : box.getRectangles()) {
                if (rect.getId() == id) {
                    return rect;
                }
            }
        }
        throw new IllegalArgumentException("Rectangle with ID " + id + " not found");
    }

    public Box findBoxContaining(Rectangle target) {
        return this.boxes().stream()
                .filter(box -> box.getRectangles().contains(target))
                .findFirst()
                .orElse(null);
    }

    public PackingSolution toPackingSolution() {
        PackingSolution solution = new PackingSolution(this.boxes().getFirst().getLength());
        OverlapPackingSolution tmp = this.copy();

        for (Box box : tmp.boxes()) {
            Box newBox = box.copy();
            solution.boxes().add(newBox);
        }

        return solution;
    }
}