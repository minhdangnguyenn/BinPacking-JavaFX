package algorithm.core.greedy.ordering.generic;

import java.util.List;

public interface GreedyOrdering<I> {
    List<I> order(List<I> elements);
}
