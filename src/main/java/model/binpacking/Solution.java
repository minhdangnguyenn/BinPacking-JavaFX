package model.binpacking;

import java.util.ArrayList;
import java.util.List;
import model.algorithm.AbstractSolution;
import model.algorithm.ToPlacePosition;
import model.binpacking.greedy.BottomLeftPlacer;
import model.binpacking.instances.BinRectangle;
import model.binpacking.instances.Box;
import model.binpacking.localsearch.neighborhood.GeometryBasedSolution;

public class Solution extends AbstractSolution<Box, Solution> {

    List<Box> items;
    private int numbRect;

    public Solution(int numbRect) {
        super();
        this.items = new ArrayList<>();
        this.numbRect = numbRect;
    }

    public int getNumbRect() {
        return numbRect;
    }

    public double getRuntime() {
        return this.runtime;
    }

    public List<Box> getItems() {
        return items;
    }

    @Override
    public int getNumberOfBins() {
        return this.items.size();
    }

    @Override
    public double getTotalUnusedArea() {
        double totalUnusedArea = 0;
        for (Box box : this.items) {
            int usedArea = 0;
            for (BinRectangle rectangle: box.getRectangles()) {
                usedArea += rectangle.getArea();
            }
            totalUnusedArea += box.getArea() - usedArea;
        }
        return totalUnusedArea;
    }

    public void setItems(List<Box> items) {
        this.items = items;
    }

    public void printStats() {
        System.out.println(
            "Number of items (boxes): " +
                items.size() +
                " for " +
                numbRect +
                " rectangles"
        );

        System.out.println("Run time: " + getRuntime() + "ms");
    }


    @Override
    public ArrayList<Solution> generateNeighbors() {
        GeometryBasedSolution geometryBased = new GeometryBasedSolution();
        return geometryBased.generateNeighborsFor(this);
    }

    private Solution generateLastBoxRepackNeighbor() {
        // 1. Get the last box
        Box lastBox = items.getLast();

        // 2. Extract all rectangles from last box
        ArrayList<BinRectangle> rectanglesToRepack =
                new ArrayList<>(lastBox.getRectangles());

        if (rectanglesToRepack.isEmpty()) {
            return null;  // Empty box, no neighbor to generate
        }

        // 3. Create a copy of current solution WITHOUT the last box
        Solution neighbor = this.deepCopyWithoutLastBox();

        // 4. Try to repack rectangles using greedy bottom-left placement
        BottomLeftPlacer placer = new BottomLeftPlacer(lastBox.getLength());

        for (BinRectangle rect : rectanglesToRepack) {
            // Create a copy of the rectangle
            BinRectangle rectCopy = new BinRectangle(
                    rect.getId(),
                    rect.getWidth(),
                    rect.getHeight()
            );

            // Try to place in existing boxes
            boolean placed = false;
            for (Box box : neighbor.getItems()) {
                ToPlacePosition pos = placer.canPlaceInABox(rectCopy, box);
                if (pos != null) {
                    // Place the rectangle
                    if (pos.getShouldRotate()) {
                        rectCopy.rotate();
                    }
                    rectCopy.setPosition(pos.getX(), pos.getY());
                    box.addRectangle(rectCopy);
                    placed = true;
                    break;
                }
            }

            // if couldn't place in existing boxes, create new box
            if (!placed) {
                Box newBox = new Box(
                        neighbor.getItems().size(),
                        lastBox.getLength()
                );

                ToPlacePosition pos = placer.canPlaceInABox(rectCopy, newBox);
                if (pos != null) {
                    if (pos.getShouldRotate()) {
                        rectCopy.rotate();
                    }
                    rectCopy.setPosition(pos.getX(), pos.getY());
                    newBox.addRectangle(rectCopy);
                    neighbor.getItems().add(newBox);
                } else {
                    // Should not happen, but handle gracefully
                    return null;
                }
            }
        }

        return neighbor;
    }

    private Solution deepCopyWithoutLastBox() {
        Solution copy = new Solution(this.numbRect);
        copy.runtime = this.runtime;

        // Copy all boxes EXCEPT the last one
        for (int i = 0; i < this.items.size() - 1; i++) {
            Box box = this.items.get(i);
            Box boxCopy = new Box(box.getId(), box.getLength());

            for (BinRectangle rect : box.getRectangles()) {
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

            copy.items.add(boxCopy);
        }

        return copy;
    }
}
