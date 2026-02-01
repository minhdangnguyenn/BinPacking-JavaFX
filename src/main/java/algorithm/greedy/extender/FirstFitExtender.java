package algorithm.greedy.extender;

import algorithm.greedy.putting.PuttingStrategy;
import algorithm.greedy.putting.TryPutResult;
import algorithm.solution.PackingSolution;
import algorithm.instances.Rectangle;
import algorithm.instances.Box;


public class FirstFitExtender implements GreedyExtender<PackingSolution, Rectangle> {
    private final PuttingStrategy putting;

    public FirstFitExtender(PuttingStrategy putting) {
        this.putting = putting;
    }

    @Override
    public PackingSolution extend(
            PackingSolution solution,
            Rectangle rectangle
    ) {
        // Try existing boxes
        for (Box box : solution.boxes()) {
            TryPutResult result = this.putting.tryPut(rectangle, box);
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
        Box newBox = new Box(0, solution.boxes().getFirst().getLength());
        newBox.addRectangle(rectangle, 0, 0);

        // Add new box to solution
        solution.addBox(newBox);
        return solution;
    }
}
