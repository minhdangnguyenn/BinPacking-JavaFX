package algorithm.core.greedy;

import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.model.Item;
import algorithm.solution.generic.Solution;

import java.util.List;

/**
 * This is a generic class for greedy as exercise requirements
 * @param <S> (solution -- in non generic case)
 * @param <I> (rectangle -- in non generic case)
 */
public class Greedy<S extends Solution, I extends Item>{
    private final GreedyOrdering<I> ordering;
    private final GreedyStrategy<S, I> extender;


    public Greedy(
            GreedyOrdering<I> ordering,
            GreedyStrategy<S, I> extender
    ) {
        this.ordering = ordering;
        this.extender = extender;
    }

    public S solve(S solution, List<I> items) {
        List<I> orderedItems = ordering.order(items);

        for (I item : orderedItems) {
            solution = extender.select(solution, item);
        }

        return solution;
    }
}
