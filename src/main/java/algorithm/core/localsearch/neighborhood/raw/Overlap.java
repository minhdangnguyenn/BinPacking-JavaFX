package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.packing.raw.TryPackResult;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;
import algorithm.solution.raw.PackingSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Overlap implements Neighborhood<OverlapPackingSolution> {
    private final GreedyAlgorithm<PackingSolution, Rectangle> greedyAlgorithm;
    private int currentIteration = 0;

    public Overlap() {
        PackingStrategy packingStrategy = new BottomLeft();
        GreedyOrdering<Rectangle> greedyOrdering = new AreaDescOrder();
        GreedyStrategy<PackingSolution, Rectangle> greedyStrategy = new FirstFitStrategy(packingStrategy);
        greedyAlgorithm = new GreedyAlgorithm<>(greedyOrdering, greedyStrategy);
    }

    public static OverlapPackingSolution collapsePackingSolution(PackingSolution packingSolution) {
        List<Rectangle> rectangles = new ArrayList<>();
        OverlapPackingSolution temp = new OverlapPackingSolution(packingSolution.boxes().getFirst().getLength());

        // first I try to put all rectangles to the corner
        for (Box box: packingSolution.boxes()) {
            for (Rectangle rect: box.getRectangles()) {
                Rectangle copy = rect.copy();
                copy.setPosition(0,0);
                rectangles.add(copy);
                temp.boxes().getFirst().addRectangle(copy, 0, 0);
            }
        }

        System.out.println("temp boxes: " + temp.boxes().size());
        return temp;
    }

    private void PushApart(Rectangle rect1, Rectangle rect2, int boxSize) {

        int xOverlap = Math.min(rect1.getX() + rect1.getWidth(), rect2.getX() + rect2.getWidth())
                - Math.max(rect1.getX(), rect2.getX());
        int yOverlap = Math.min(rect1.getY() + rect1.getHeight(), rect2.getY() + rect2.getHeight())
                - Math.max(rect1.getY(), rect2.getY());

        // no overlap -> nothing to do
        if (xOverlap <= 0 && yOverlap <= 0) {
            return;
        }

        // clamp helper
        java.util.function.IntUnaryOperator clampX1 = v -> Math.max(0, Math.min(v, boxSize - rect1.getWidth()));
        java.util.function.IntUnaryOperator clampY1 = v -> Math.max(0, Math.min(v, boxSize - rect1.getHeight()));
        java.util.function.IntUnaryOperator clampX2 = v -> Math.max(0, Math.min(v, boxSize - rect2.getWidth()));
        java.util.function.IntUnaryOperator clampY2 = v -> Math.max(0, Math.min(v, boxSize - rect2.getHeight()));

        if (xOverlap >= yOverlap) {
            // push along X
            int rect1Distance = Math.min(rect1.getX(), boxSize - rect1.getX() - rect1.getWidth());
            int rect2Distance = Math.min(rect2.getX(), boxSize - rect2.getX() - rect2.getWidth());

            int rect1CenterX = rect1.getX() + rect1.getWidth() / 2;
            int rect2CenterX = rect2.getX() + rect2.getWidth() / 2;

            int direction1 = (rect1CenterX < rect2CenterX) ? -1 : 1;
            int direction2 = -direction1;

            if (rect1Distance > rect2Distance) {
                int toPush = Math.min(rect1Distance, xOverlap);
                int newX = clampX1.applyAsInt(rect1.getX() + direction1 * toPush);
                rect1.setPosition(newX, rect1.getY());
            } else {
                int toPush = Math.min(rect2Distance, xOverlap);
                int newX = clampX2.applyAsInt(rect2.getX() + direction2 * toPush);
                rect2.setPosition(newX, rect2.getY());
            }
        } else {
            // push along Y
            int rect1Distance = Math.min(rect1.getY(), boxSize - rect1.getY() - rect1.getHeight());
            int rect2Distance = Math.min(rect2.getY(), boxSize - rect2.getY() - rect2.getHeight());

            int rect1CenterY = rect1.getY() + rect1.getHeight() / 2;
            int rect2CenterY = rect2.getY() + rect2.getHeight() / 2;

            int direction1 = (rect1CenterY < rect2CenterY) ? -1 : 1;
            int direction2 = -direction1;

            if (rect1Distance > rect2Distance) {
                int toPush = Math.min(rect1Distance, yOverlap);
                int newY = clampY1.applyAsInt(rect1.getY() + direction1 * toPush);
                rect1.setPosition(rect1.getX(), newY);
            } else {
                int toPush = Math.min(rect2Distance, yOverlap);
                int newY = clampY2.applyAsInt(rect2.getY() + direction2 * toPush);
                rect2.setPosition(rect2.getX(), newY);
            }
        }
    }

    private void ResolveOverlapsInBox(Box box, int maxIterations) {
        List<Rectangle> rectangles = box.getRectangles();
        boolean overlapsExist;

        do {
            overlapsExist = false;

            for (int i = 0; i < rectangles.size(); i++) {
                for (int j = i + 1; j < rectangles.size(); j++) {
                    Rectangle rect1 = rectangles.get(i);
                    Rectangle rect2 = rectangles.get(j);

                    if (box.isOverlapping(rect1, rect2)) {
                        overlapsExist = true;
                        PushApart(rect1, rect2, box.getLength());
                    }
                }
            }
        } while (overlapsExist && --maxIterations > 0);
    }

    private boolean ContainsOverlap(Box box) {
        List<Rectangle> rectangles = box.getRectangles();

        for (int i = 0; i < rectangles.size(); i++) {
            for (int j = i + 1; j < rectangles.size(); j++) {
                Rectangle rect1 = rectangles.get(i);
                Rectangle rect2 = rectangles.get(j);

                if (box.isOverlapping(rect1, rect2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void ApplyGravity(Box box) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (Rectangle rectangle : box.getRectangles()) {
            if (rectangle.getX() < minX) {
                minX = rectangle.getX();
            }
            if (rectangle.getY() < minY) {
                minY = rectangle.getY();
            }
        }

        // Translate the whole system to bottom-left corner
        for (Rectangle rectangle : box.getRectangles()) {
            rectangle.setPosition(rectangle.getX() - minX, rectangle.getY() - minY);
        }
    }

    @Override
    public Iterable<OverlapPackingSolution> getNeighbors(OverlapPackingSolution solution) {
        currentIteration += 1;

        List<OverlapPackingSolution> neighbors = new ArrayList<>();

        OverlapPackingSolution temp = solution.copy();
        temp.currentIteration = this.currentIteration;

        // NEIGHBOR 1: Resolve overlaps
        OverlapPackingSolution neighbor1 = solution.copy();
        neighbor1.currentIteration = currentIteration;
        neighbor1.maxIterations = solution.maxIterations;

        for (Box box : neighbor1.boxes()) {
            if (box.containsOverlap()) {
                ResolveOverlapsInBox(box, 100);
            }
        }

        neighbors.add(neighbor1);

        // NEIGHBOR 2: Repack most overlapping box
        OverlapPackingSolution neighbor2 = solution.copy();
        neighbor2.currentIteration = currentIteration;
        neighbor2.maxIterations = solution.maxIterations;

        Box mostOverlappingBox = neighbor2.highestOverlapBox();
        if (mostOverlappingBox != null && mostOverlappingBox.totalOverlapRate() > 0) {
            List<Rectangle> rectanglesToRepack = new ArrayList<>();
            for (Rectangle rect : mostOverlappingBox.getRectangles()) {
                rectanglesToRepack.add(rect.copy());
            }
            neighbor2.boxes().remove(mostOverlappingBox);

            PackingSolution baseSolution = neighbor2.copy();
            if (baseSolution.boxes().isEmpty()) {
                baseSolution.addBox(new Box(0, mostOverlappingBox.getLength()));
            }
            PackingSolution improvedSolution = greedyAlgorithm.solve(baseSolution, rectanglesToRepack);

            neighbor2 = new OverlapPackingSolution(improvedSolution.boxes());
            neighbor2.currentIteration = currentIteration;
            neighbor2.maxIterations = solution.maxIterations;
        }
        neighbors.add(neighbor2);

        System.out.println("Generated " + neighbors.size() + " neighbors");
        return neighbors;
    }
}
