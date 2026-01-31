package model.binpacking.localsearch.neighborhood;

import model.algorithm.ToPlacePosition;
import model.algorithm.localsearch.NeighborsSolution;
import model.binpacking.Solution;
import model.binpacking.greedy.BottomLeftPlacer;
import model.binpacking.instances.BinRectangle;
import model.binpacking.instances.Box;

import java.util.ArrayList;

public class GeometryBasedSolution extends NeighborsSolution<Solution> {
    private ArrayList<Solution> neighbors;
    public GeometryBasedSolution() {
        
    }

    public ArrayList<BinRectangle> unpack(Box box) {
        ArrayList<BinRectangle> unpackedRectangles = new ArrayList<BinRectangle>();
        for (BinRectangle rect : box.getRectangles()) {
            unpackedRectangles.add(rect);
            box.removeRectangle(rect);
        }

        return unpackedRectangles;
    }

    /**
     * Generate neighbors by unpacking the last box and trying to repack
     */
    public ArrayList<Solution> generateNeighborsFor(Solution currentSolution) {
        ArrayList<Solution> neighbors = new ArrayList<>();

        if (currentSolution.getItems().size() <= 1) {
            return neighbors;  // Need at least 2 boxes
        }

        // Try to repack the last box
        Solution neighbor = tryRepackLastBox(currentSolution);

        if (neighbor != null) {
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    private Solution tryRepackLastBox(Solution solution) {
        // 1. Get the last box
        Box lastBox = solution.getItems().get(solution.getItems().size() - 1);

        // 2. Extract rectangles (make a copy of the list to avoid modification issues)
        ArrayList<BinRectangle> rectanglesToRepack = new ArrayList<>(lastBox.getRectangles());

        if (rectanglesToRepack.isEmpty()) {
            return null;  // Empty box, nothing to repack
        }

        // 3. Create a deep copy WITHOUT the last box
        Solution neighbor = deepCopyWithoutLastBox(solution);

        // 4. Try to place each rectangle using greedy placement
        BottomLeftPlacer placer = new BottomLeftPlacer(lastBox.getLength());

        for (BinRectangle rect : rectanglesToRepack) {
            // Create a NEW rectangle (not a reference to the old one)
            BinRectangle newRect = new BinRectangle(
                    rect.getId(),
                    rect.getWidth(),
                    rect.getHeight()
            );

            // Try to place in existing boxes
            boolean placed = false;
            for (Box box : neighbor.getItems()) {
                ToPlacePosition pos = placer.canPlaceInABox(newRect, box);

                if (pos != null) {
                    // Place the rectangle
                    if (pos.getShouldRotate() != null && pos.getShouldRotate()) {
                        newRect.rotate();
                    }
                    newRect.setPosition(pos.getX(), pos.getY());
                    box.addRectangle(newRect);
                    placed = true;
                    break;
                }
            }

            // If it couldn't place in existing boxes, create a new box
            if (!placed) {
                Box newBox = new Box(neighbor.getItems().size(), lastBox.getLength());

                ToPlacePosition pos = placer.canPlaceInABox(newRect, newBox);

                if (pos != null) {
                    if (pos.getShouldRotate() != null && pos.getShouldRotate()) {
                        newRect.rotate();
                    }
                    newRect.setPosition(pos.getX(), pos.getY());
                    newBox.addRectangle(newRect);
                    neighbor.getItems().add(newBox);
                } else {
                    return null;  // Failed to place
                }
            }
        }

        return neighbor;
    }

    /**
     * Create a deep copy of the solution WITHOUT the last box
     */
    private Solution deepCopyWithoutLastBox(Solution original) {
        Solution copy = new Solution(original.getNumbRect());
        copy.setRunTime(original.getRuntime());

        // Copy all boxes EXCEPT the last one
        for (int i = 0; i < original.getItems().size() - 1; i++) {
            Box originalBox = original.getItems().get(i);
            Box boxCopy = new Box(originalBox.getId(), originalBox.getLength());

            // Copy all rectangles in this box
            for (BinRectangle rect : originalBox.getRectangles()) {
                BinRectangle rectCopy = new BinRectangle(
                        rect.getId(),
                        rect.getWidth(),
                        rect.getHeight()
                );
                rectCopy.setPosition(
                        rect.getPosition().getX(),
                        rect.getPosition().getY()
                );
                if (rect.getIsRotated()) {
                    rectCopy.rotate();
                }
                boxCopy.addRectangle(rectCopy);
            }

            copy.getItems().add(boxCopy);
        }

        return copy;
    }

    @Override
    public ArrayList<Solution> generateNeighbors() {
        // This method is not used, but required by parent class
        return new ArrayList<>();
    }
}
