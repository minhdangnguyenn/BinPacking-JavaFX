package algorithm.solution.raw;

import algorithm.core.greedy.Greedy;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.model.Rectangle;
import algorithm.solution.generic.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * this class should represent
 * only ordered list of rectangles
 * no boxes, no coordinates
 * this works with different permutation of rectangles instead of a list of solution
 */
public class PermutationSolution implements Solution {
    List<Rectangle> rectangles;
    static Greedy<PackingSolution, Rectangle> greedy;
    private final int boxLength;

    public PermutationSolution(List<Rectangle> initRectangles, int boxLength) {
        if (greedy == null) {
            GreedyOrdering<Rectangle> ordering = new AreaDescOrder();
            PackingStrategy putting = new BottomLeft();
            GreedyStrategy<PackingSolution, Rectangle> packer = new FirstFitStrategy(putting);
            greedy = new Greedy<>(ordering, packer);
        }
        this.rectangles = initRectangles;
        this.boxLength = boxLength;
    }

    public List<Rectangle> getRectangles() {
        return this.rectangles;
    }

    @Override
    public PermutationSolution copy() {
        List<Rectangle> permutations = new ArrayList<>();
        for (Rectangle rect : this.rectangles) {
            permutations.add(rect.copy());
        }
        return new PermutationSolution(permutations, boxLength);
    }

    public PackingSolution decode() {
        // take the permutation solution (contains a permutation of rectangles)
        // convert it into PackingSolution (with boxes and coordinates)
        PackingSolution packingSolution = new PackingSolution(this.boxLength);
        return greedy.solve(packingSolution, this.getRectangles());
    }
}
