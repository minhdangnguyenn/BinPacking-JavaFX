package algorithm.core.greedy;

import algorithm.core.greedy.strategy.generic.SelectStrategy;
import algorithm.core.greedy.ordering.generic.OrderStrategy;
import algorithm.model.Item;
import algorithm.solution.generic.Solution;

import java.util.List;

/**
 * This is a generic class for greedy as exercise requirements
 * @param <S> (solution -- in non generic case)
 * @param <I> (rectangle -- in non generic case)
 */
public class Greedy<S extends Solution, I extends Item>{
    private final OrderStrategy<I> ordering;
    private final SelectStrategy<S, I> selector;


    public Greedy(
            OrderStrategy<I> ordering,
            SelectStrategy<S, I> selector
    ) {
        this.ordering = ordering;
        this.selector = selector;
    }

    public S solve(S solution, List<I> items) {
        List<I> orderedItems = ordering.order(items);

        for (I item : orderedItems) {
            solution = selector.select(solution, item);
        }

        return solution;
    }
}
