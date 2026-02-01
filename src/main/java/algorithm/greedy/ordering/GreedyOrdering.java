package algorithm.greedy.ordering;

import java.util.List;

public interface GreedyOrdering<I> {
    List<I> order(List<I> elements);
}
