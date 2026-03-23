package algorithm.core.greedy.strategy.generic;

import algorithm.solution.generic.Solution;
import algorithm.model.Item;

public interface SelectStrategy<S extends Solution, I extends Item> {
    S select(S initial, I item);
}
