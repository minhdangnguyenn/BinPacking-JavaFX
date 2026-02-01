package algorithm.core.greedy.packing.generic;

import algorithm.core.greedy.packing.raw.TryPackResult;
import algorithm.instances.Rectangle;
import algorithm.instances.Box;

public interface PackingStrategy {
    TryPackResult tryPack(Rectangle rectangle, Box box);
}
