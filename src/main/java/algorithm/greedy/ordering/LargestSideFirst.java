package algorithm.greedy.ordering;

import algorithm.instances.Rectangle;

import java.util.List;
import java.util.stream.Collectors;

public class LargestSideFirst implements GreedyOrdering<Rectangle> {

    @Override
    public List<Rectangle> order(List<Rectangle> elements) {
        return elements
                .stream()
                .sorted((r1, r2) -> {
                    int side1 = Math.max(r1.getWidth(), r1.getHeight());
                    int side2 = Math.max(r2.getWidth(), r2.getHeight());
                    if (side1 != side2) return side2 - side1;
                    return r2.getWidth() - r1.getWidth();
                })
                .collect(Collectors.toList());
    }
}