package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.Greedy;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.SideDescOrder;
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

    private final Greedy<PackingSolution, Rectangle> greedy;
    private int currentIteration = 0;

    public Overlap() {
        GreedyOrdering<Rectangle> ordering = new SideDescOrder();
        PackingStrategy puttingStrategy = new BottomLeft();
        GreedyStrategy<PackingSolution, Rectangle> extender = new FirstFitStrategy(puttingStrategy);
        this.greedy = new Greedy<>(ordering, extender);
    }

    private void pushOut(Rectangle rect1, Rectangle rect2, int boxSize) {

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

    private void resolveOverlapsInBox(Box box, int maxIterations) {
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
                        pushOut(rect1, rect2, box.getLength());
                    }
                }
            }
        } while (overlapsExist && --maxIterations > 0);
    }

    private boolean containsOverlap(Box box) {
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

    @Override
    public Iterable<OverlapPackingSolution> getNeighbors(OverlapPackingSolution solution) {
        currentIteration += 1;

        List<OverlapPackingSolution> neighbors = new ArrayList<>();

        OverlapPackingSolution temp = solution.copy();
        temp.currentIteration = this.currentIteration;

        if (currentIteration == temp.maxIterations) {
            System.out.println("Final Iteration, use greedy to resolve all overlap leftover");
            // Final iteration:
            // Bottom Left Placement for boxes with Utilization < 100%
            List<Rectangle> rectanglesToRepack = new ArrayList<>();

            for (Box box : temp.boxes()) {
                if (box.getUtilization() < 100.0 && containsOverlap(box)) {

                    // Try to remove overlap first
                    resolveOverlapsInBox(box, 100);

                    if (!containsOverlap(box)) continue;

                    List<Rectangle> rectanglesToReposition = box.getRectangles()
                            .stream()
                            .map(Rectangle::copy)
                            .sorted(Comparator.comparingInt(Rectangle::getArea).reversed())
                            .toList();

                    box.getRectangles().clear();

                    PackingStrategy puttingStrategy = new BottomLeft();
                    for (Rectangle rectangle : rectanglesToReposition) {
                        TryPackResult result = puttingStrategy.tryPack(rectangle, box);
                        if (result != null) {
                            box.addRectangle(result.rotated() ? rectangle.rotate() : rectangle, result.x(), result.y());
                        } else {
                            // If any rectangle cannot be placed, add to repack list
                            rectanglesToRepack.add(rectangle);
                        }
                    }
                } else if (containsOverlap(box)) {
                    // If still contains overlaps, add all rectangles to repack list
                    for (Rectangle rectangle : box.getRectangles()) {
                        rectanglesToRepack.add(rectangle.copy());
                    }
                    box.getRectangles().clear();
                }
            }

            // finalizing using greedy
            if (!rectanglesToRepack.isEmpty()) {
                PackingSolution baseSolution = temp.copy();
                baseSolution.boxes().removeIf(box -> box.getRectangles().isEmpty());
                PackingSolution improvedSolution = greedy.solve(baseSolution, rectanglesToRepack);

                // Convert back to OverlapPackingSolution
                OverlapPackingSolution overlapImprovedSolution = new OverlapPackingSolution(improvedSolution.boxes());
                overlapImprovedSolution.currentIteration = temp.currentIteration;
                overlapImprovedSolution.maxIterations = temp.maxIterations;

                return List.of(overlapImprovedSolution);
            }
        }

        // Neighbor 1: Resolve overlaps in all boxes
        OverlapPackingSolution neighbor1 = temp.copy();
        if (!neighbor1.boxes().isEmpty()) {
            for (Box box : neighbor1.boxes()) {
                resolveOverlapsInBox(box, 50);
            }
            neighbors.add(neighbor1);
        }

        // Neighbor 2: Repack rectangles from most overlapping box
        OverlapPackingSolution neighbor2 = temp.copy();
        if (!neighbor2.boxes().isEmpty()) {
            Box mostOverlappingBox = neighbor2.getHighestOverlapBox();
            getNeighbor(neighbors, neighbor2, mostOverlappingBox);
        }

        // Neighbor 3: Repack the least used box
        OverlapPackingSolution neighbor3 = temp.copy();
        if (!neighbor3.boxes().isEmpty()) {
            // Find box with the least used area
            Box leastUsedBox = neighbor3.getLeastUsedBox();
            getNeighbor(neighbors, neighbor3, leastUsedBox);
        }

        // Neighbor 4: Repack the most used box
        OverlapPackingSolution neighbor4 = temp.copy();
        if (!neighbor4.boxes().isEmpty()) {
            // Find box with the most used area
            Box mostUsedBox = neighbor4.getMostUsedBox();
            getNeighbor(neighbors, neighbor4, mostUsedBox);
        }

        // Neighbor 5: Bring the largest area rectangle from most overlapping box to new box
        OverlapPackingSolution neighbor5 = temp.copy();
        if (!neighbor5.boxes().isEmpty()) {
            Box mostOverlappingBox = neighbor5.getHighestOverlapBox();
            if (mostOverlappingBox != null && !mostOverlappingBox.getRectangles().isEmpty()) {
                Rectangle largestRectangle = mostOverlappingBox.getRectangles()
                        .stream()
                        .max(Comparator.comparingInt(Rectangle::getArea))
                        .orElse(null);

                if (largestRectangle != null) {
                    mostOverlappingBox.getRectangles().remove(largestRectangle);
                    Box newBox = new Box(neighbor5.boxes().size(), mostOverlappingBox.getLength());
                    newBox.addRectangle(largestRectangle, 0, 0);
                    neighbor5.boxes().add(newBox);
                }
            }
        }

        neighbors.add(neighbor5);

        return neighbors;
    }

    private void getNeighbor(List<OverlapPackingSolution> neighbors, OverlapPackingSolution neighbor2, Box mostOverlappingBox) {
        if (mostOverlappingBox != null) {
            List<Rectangle> rectanglesToRepack = mostOverlappingBox.getRectangles()
                    .stream()
                    .map(Rectangle::copy)
                    .toList();
            neighbor2.boxes().remove(mostOverlappingBox);

            PackingSolution baseSolution = neighbor2.copy();
            if (baseSolution.boxes().isEmpty()) {
                baseSolution.addBox(new Box(0, mostOverlappingBox.getLength()));
            }
            PackingSolution improvedSolution = greedy.solve(baseSolution, rectanglesToRepack);

            // Convert back to OverlapPackingSolution
            OverlapPackingSolution overlapImprovedSolution = new OverlapPackingSolution(improvedSolution.boxes());
            overlapImprovedSolution.currentIteration = neighbor2.currentIteration;
            overlapImprovedSolution.maxIterations = neighbor2.maxIterations;

            neighbors.add(overlapImprovedSolution);
        }
    }
}