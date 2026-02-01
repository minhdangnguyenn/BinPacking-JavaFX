package algorithm.greedy.packing;

import algorithm.instances.Rectangle;
import algorithm.instances.Box;

public interface PackingStrategy {
    TryPackResult tryPut(Rectangle rectangle, Box box);
}
