package algorithm.core.greedy;

import algorithm.core.greedy.extender.generic.GreedyExtender;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.model.Item;
import algorithm.solution.Solution;

import java.util.List;

/*
* This is a generic class for Greedy Algorithm
* This class is generic as exercise requirements
* */
public class GreedyAlgorithm<S extends Solution, I extends Item>{
    private final GreedyOrdering<I> ordering;
    private final GreedyExtender<S, I> extender;


    public GreedyAlgorithm(
            GreedyOrdering<I> ordering,
            GreedyExtender<S, I> extender
    ) {
        this.ordering = ordering;
        this.extender = extender;
    }

    public S solve(S solution, List<I> items) {
        List<I> orderedItems = ordering.order(items);

        for (I item : orderedItems) {
            solution = extender.extend(solution, item);
        }

        return solution;
    }
}
