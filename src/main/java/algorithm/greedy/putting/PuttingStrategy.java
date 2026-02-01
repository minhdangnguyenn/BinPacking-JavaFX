package algorithm.greedy.putting;

import algorithm.instances.Rectangle;
import algorithm.instances.Box;

public interface PuttingStrategy {
    TryPutResult tryPut(Rectangle rectangle, Box box);
}
