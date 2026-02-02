package algorithm.core.greedy.strategy.raw;

import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.TryPackResult;
import algorithm.solution.raw.PackingSolution;
import algorithm.model.Rectangle;
import algorithm.model.Box;


public class FirstFitStrategy implements GreedyStrategy<PackingSolution, Rectangle> {
    private final PackingStrategy packingStrategy;

    public FirstFitStrategy(PackingStrategy packingStrategy) {
        this.packingStrategy = packingStrategy;
    }

    @Override
    public PackingSolution select(
            PackingSolution solution,
            Rectangle rectangle
    ) {
        // Try existing boxes
        for (Box box : solution.boxes()) {
            TryPackResult result = this.packingStrategy.tryPack(rectangle, box);
            if (result != null) {
                box.addRectangle(
                        result.rotated() ? rectangle.rotate() : rectangle,
                        result.x(),
                        result.y()
                );
                return solution;
            }
        }

        // Create new box if no existing box can fit the rectangle
        int newBoxId = solution.boxes().size();
        Box newBox = new Box(newBoxId, solution.boxes().getFirst().getLength());
        newBox.addRectangle(rectangle, 0, 0);

        // Add new box to solution
        solution.addBox(newBox);
        return solution;
    }
}
