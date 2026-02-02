package algorithm.core.greedy.extender.raw;

import algorithm.core.greedy.extender.generic.GreedyStrategy;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.TryPackResult;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.PackingSolution;

public class BestFitStrategy
        implements GreedyStrategy<PackingSolution, Rectangle>
{

    private final PackingStrategy putting;

    public BestFitStrategy(PackingStrategy putting) {
        this.putting = putting;
    }

    @Override
    public PackingSolution select(
            PackingSolution solution,
            Rectangle rectangle
    ) {
        // Try existing boxes to find the tightest fit
        int bestArea = Integer.MAX_VALUE;
        Box bestBox = null;
        TryPackResult bestPosition = null;

        for (Box box : solution.boxes()) {
            TryPackResult result = putting.tryPack(rectangle, box);
            if (result != null) {
                int area = box.getArea() - rectangle.getArea() - box.getUsedArea();
                if (area < bestArea) {
                    bestArea = area;
                    bestBox = box;
                    bestPosition = result;
                }
            }
        }

        if (bestBox != null) {
            bestBox.addRectangle(
                    bestPosition.rotated() ? rectangle.rotate() : rectangle,
                    bestPosition.x(),
                    bestPosition.y()
            );
            return solution;
        }

        // Create a new box if no existing box can fit the rectangle
        Box newBox = new Box(
                solution.boxes().size(),
                solution.boxes().getFirst().getLength());
        newBox.addRectangle(rectangle, 0, 0);

        solution.addBox(newBox);
        return solution;
    }
}