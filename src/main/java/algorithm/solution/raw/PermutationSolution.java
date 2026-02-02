package algorithm.solution.raw;

import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.model.Box;
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

    public PermutationSolution(List<Rectangle> initRectangles) {
        this.rectangles = initRectangles;
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
        return new PermutationSolution(permutations);
    }
}
