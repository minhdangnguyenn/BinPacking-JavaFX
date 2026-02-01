package algorithm.core.greedy.packing.generic;

import algorithm.core.greedy.packing.raw.TryPackResult;
import algorithm.model.Rectangle;
import algorithm.model.Box;

public interface PackingStrategy {
    TryPackResult tryPack(Rectangle rectangle, Box box);
}
