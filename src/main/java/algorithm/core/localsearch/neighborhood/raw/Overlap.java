package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.SideDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;
import algorithm.solution.raw.PackingSolution;

import java.util.ArrayList;
import java.util.List;

public class Overlap implements Neighborhood<OverlapPackingSolution> {

    private final GreedyAlgorithm<PackingSolution, Rectangle> greedySolver;
    private int currentIteration = 0;

    public Overlap() {
        GreedyOrdering<Rectangle> ordering = new SideDescOrder();
        PackingStrategy puttingStrategy = new BottomLeft();
        GreedyStrategy<PackingSolution, Rectangle> extender = new FirstFitStrategy(puttingStrategy);
        this.greedySolver = new GreedyAlgorithm<>(ordering, extender);
    }

    private void moveRectangleToNewBox(
            OverlapPackingSolution solution,
            Box sourceBox
    ) {
        for (int i = 0; i< sourceBox.getRectangles().size(); i++) {
            for (int j = i+1; j<sourceBox.getRectangles().size(); j++ ) {
                Rectangle rect1 = sourceBox.getRectangles().get(i);
                Rectangle rect2 = sourceBox.getRectangles().get(j);
                if (sourceBox.isOverlapping(rect1, rect2)) {
                    // Remove from old box
                    sourceBox.removeRectangle(rect2);

                    // Create new box
                    int newBoxId = solution.boxes().size();
                    Box newBox = new Box(newBoxId, sourceBox.getLength());

                    // Place rectangle (safe position)
                    newBox.addRectangle(rect2, 0, 0);

                    solution.boxes().add(newBox);
                }
            }
        }
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

    private double calculateOverlapArea(Rectangle a, Rectangle b) {
        // S_intersection/max(S_a, S_b)
        double xLeft = Math.max(a.getX(), b.getX());
        double xRight = Math.min(a.getX() + a.getWidth(), b.getX() + b.getWidth());
        double yBottom = Math.max(a.getY(), b.getY());
        double yTop = Math.min(a.getY() + a.getHeight(), b.getY() + b.getHeight());

        double width = xRight - xLeft;
        double height = yTop - yBottom;

        if (width > 0 && height > 0) {
            return width * height;
        }
        return 0.0;
    }

    @Override
    public Iterable<OverlapPackingSolution> getNeighbors(OverlapPackingSolution solution) {
        solution.currentIteration++;
        List<OverlapPackingSolution> neighbors = new ArrayList<>();

        System.out.println("\n=== OVERLAP ITERATION " + solution.currentIteration + "/" + solution.maxIterations + " ===");
        System.out.println("Current solution: " + solution.boxes().size() + " boxes, " + countTotalOverlapsSolution(solution) + " overlaps");

        // Neighbor 1: Unpack ALL rectangles and repack with greedy
        OverlapPackingSolution fullGreedyNeighbor = this.createFullGreedyNeighbor(solution);
        neighbors.add(fullGreedyNeighbor);

        // Neighbor 2: Unpack only boxes with overlaps and repack
        OverlapPackingSolution smartNeighbor = this.createSmartGreedyNeighbor(solution);
        neighbors.add(smartNeighbor);

        System.out.println("Generated " + neighbors.size() + " neighbor(s)\n");
        return neighbors;
    }

    /**
     * SMART NEIGHBOR: Only unpacks boxes that have overlaps, keeps good boxes unchanged
     * This is more efficient than unpacking everything
     */
    private OverlapPackingSolution createSmartGreedyNeighbor(OverlapPackingSolution solution) {
        System.out.println(">>> Creating SMART neighbor (unpack only overlap boxes)...");
        
        int boxLength = solution.boxes().getFirst().getLength();
        PackingSolution tmp = solution.copy();
        // Step 1: Identify boxes with and without overlaps
        List<Box> goodBoxes = new ArrayList<>();  // No overlaps - keep these
        List<Rectangle> rectanglesToRepack = new ArrayList<>();  // From bad boxes
        
        for (Box box : tmp.boxes()) {
            int overlaps = countTotalOverlapsBox(box);
            
            if (overlaps == 0) {
                // This box is perfect - keep it as is
                goodBoxes.add(box.copy());
            } else {
                // This box has overlaps - unpack all its rectangles
                for (Rectangle rect : box.getRectangles()) {
                    Rectangle copy = rect.copy();
                    copy.setPosition(0, 0);
                    rectanglesToRepack.add(copy);
                }
            }
        }
        
        System.out.println("  Keeping " + goodBoxes.size() + " good boxes (no overlaps)");
        System.out.println("  Repacking " + rectanglesToRepack.size() + " rectangles from bad boxes");
        
        // Step 2: Create base solution with good boxes
        PackingSolution baseSolution;
        if (goodBoxes.isEmpty()) {
            // No good boxes - start fresh
            baseSolution = new PackingSolution(boxLength);
        } else {
            // Start with good boxes
            baseSolution = new PackingSolution(goodBoxes);
        }
        
        // Step 3: Repack the rectangles from bad boxes using greedy
        PackingSolution repackedSolution = this.greedySolver.solve(baseSolution, rectanglesToRepack);
        
        // Step 4: Convert to OverlapPackingSolution
        OverlapPackingSolution result = new OverlapPackingSolution(repackedSolution.boxes());
        result.currentIteration = solution.currentIteration;
        result.maxIterations = solution.maxIterations;
        
        // Step 5: Reindex boxes
        for (int i = 0; i < result.boxes().size(); i++) {
            result.boxes().get(i).setId(i);
        }
        
        // Step 6: Verify
        int totalRects = 0;
        for (Box box : result.boxes()) {
            totalRects += box.getRectangles().size();
        }
        
        int overlaps = countTotalOverlapsSolution(result);
        System.out.println("  Result: " + result.boxes().size() + " boxes, " + totalRects + " rectangles, " + overlaps + " overlaps");
        
        return result;
    }

    private OverlapPackingSolution createFullGreedyNeighbor(OverlapPackingSolution solution) {
        System.out.println(">>> Creating FULL-GREEDY neighbor (unpack ALL and repack)...");

        OverlapPackingSolution tmp = solution.copy();
        int boxLength = tmp.boxes().getFirst().getLength();
        
        // Step 2: Create empty solution with one empty box
        PackingSolution emptySolution = new PackingSolution(boxLength);
        
        // Step 3: Use greedy to repack ALL rectangles
        System.out.println("  Repacking with greedy algorithm...");
        List<Rectangle> allRectangles = new ArrayList<>();
        for (Box box : tmp.boxes()) {
            for (Rectangle rect : box.getRectangles()) {
                Rectangle copy = rect.copy();
                copy.setPosition(0, 0); // Reset position for repacking
                allRectangles.add(copy);
            }
        }

        PackingSolution repackedSolution = this.greedySolver.solve(emptySolution, allRectangles);
        
        // Step 4: Convert to OverlapPackingSolution
        OverlapPackingSolution result = new OverlapPackingSolution(repackedSolution.boxes());
        result.currentIteration = solution.currentIteration;
        result.maxIterations = solution.maxIterations;
        
        int totalRects = 0;
        for (Box box : result.boxes()) {
            totalRects += box.getRectangles().size();
        }
        
        int overlaps = countTotalOverlapsSolution(result);
        System.out.println("  Result: " + result.boxes().size() + " boxes, " + totalRects + " rectangles, " + overlaps + " overlaps");
        
        if (totalRects != allRectangles.size()) {
            System.out.println("  ⚠ WARNING: Rectangle count mismatch! Expected " + allRectangles.size() + " but got " + totalRects);
        }
        
        return result;
    }
    
    private int countTotalOverlapsSolution(OverlapPackingSolution solution) {
        int count = 0;
        for (Box box : solution.boxes()) {
            List<Rectangle> rects = box.getRectangles();
            for (int i = 0; i < rects.size(); i++) {
                for (int j = i + 1; j < rects.size(); j++) {
                    if (calculateOverlapArea(rects.get(i), rects.get(j)) > 0) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private int countTotalOverlapsBox (Box box) {
        int count = 0;
        List<Rectangle> rects = box.getRectangles();
        for (int i = 0; i < rects.size(); i++) {
            for (int j = i + 1; j < rects.size(); j++) {
                if (calculateOverlapArea(rects.get(i), rects.get(j)) > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private void applyGravity(Box box) {
        for (Rectangle r : box.getRectangles()) {
            int y = r.getY();

            while (true) {
                int newY = clampY(r, y - 1, box.getLength());
                if (newY == y) break;
                if (!canMoveClamped(r, r.getX(), newY, box, Double.MAX_VALUE)) break;
                y = newY;
            }

            r.setPosition(r.getX(), y);
        }
    }

    private boolean canMoveClamped(
            Rectangle rect,
            int targetX,
            int targetY,
            Box box,
            double maxAllowedOverlap
    ) {
        int boxLength = box.getLength();

        int newX = clampX(rect, targetX, boxLength);
        int newY = clampY(rect, targetY, boxLength);

        // if nothing changes → useless move
        if (newX == rect.getX() && newY == rect.getY()) {
            return false;
        }

        double newOverlap = overlapAtPosition(rect, newX, newY, box);
        return newOverlap <= maxAllowedOverlap;
    }

    private double overlapAtPosition(
            Rectangle moving,
            int newX,
            int newY,
            Box box
    ) {
        double overlap = 0.0;

        int oldX = moving.getX();
        int oldY = moving.getY();

        // temporarily move
        moving.setPosition(newX, newY);

        for (Rectangle other : box.getRectangles()) {
            if (other == moving) continue;
            overlap += calculateOverlapArea(moving, other);
        }

        // restore
        moving.setPosition(oldX, oldY);

        return overlap;
    }

    private int clampX(Rectangle r, int x, int boxLength) {
        return Math.max(0, Math.min(x, boxLength - r.getWidth()));
    }

    private int clampY(Rectangle r, int y, int boxLength) {
        return Math.max(0, Math.min(y, boxLength - r.getHeight()));
    }
}