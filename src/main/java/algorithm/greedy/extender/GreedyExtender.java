package algorithm.greedy.extender;

import algorithm.solution.Solution;
import algorithm.interfaces.Item;

public interface GreedyExtender<S extends Solution, I extends Item> {
    S extend(S initial, I item);
}
