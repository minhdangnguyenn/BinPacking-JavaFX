package algorithm.greedy.ordering;

import algorithm.instances.Rectangle;

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