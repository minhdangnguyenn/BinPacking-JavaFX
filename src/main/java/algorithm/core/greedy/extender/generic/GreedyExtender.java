package algorithm.core.greedy.extender.generic;

import algorithm.solution.Solution;
import algorithm.model.Item;

public interface GreedyExtender<S extends Solution, I extends Item> {
    S extend(S initial, I item);
}
