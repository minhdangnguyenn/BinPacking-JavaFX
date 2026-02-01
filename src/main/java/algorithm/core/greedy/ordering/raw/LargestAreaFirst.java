package algorithm.core.greedy.ordering.raw;

import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.model.Rectangle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LargestAreaFirst implements GreedyOrdering<Rectangle> {

    @Override
    public List<Rectangle> order(List<Rectangle> elements) {
        return elements
                .stream()
                .sorted(Comparator.comparingInt(Rectangle::getArea).reversed())
                .collect(Collectors.toList());
    }
}