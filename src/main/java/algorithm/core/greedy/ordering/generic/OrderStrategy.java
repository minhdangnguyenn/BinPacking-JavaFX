package algorithm.core.greedy.ordering.generic;

import java.util.List;

public interface OrderStrategy<I> {
    List<I> order(List<I> elements);
}
