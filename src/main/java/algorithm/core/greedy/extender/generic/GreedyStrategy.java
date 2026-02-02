package algorithm.core.greedy.extender.generic;

import algorithm.solution.Solution;
import algorithm.model.Item;

public interface GreedyStrategy<S extends Solution, I extends Item> {
    S select(S initial, I item);
}
