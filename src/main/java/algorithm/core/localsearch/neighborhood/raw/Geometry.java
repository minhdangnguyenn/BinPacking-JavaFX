package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.Greedy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import java.util.Comparator;
import algorithm.core.greedy.strategy.generic.SelectStrategy;
import algorithm.core.greedy.ordering.generic.OrderStrategy;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.PackingSolution;

import java.util.ArrayList;
import java.util.List;

public class Geometry implements Neighborhood<PackingSolution> {
    private final Greedy<PackingSolution, Rectangle> greedy;

    public Geometry() {
        PackingStrategy packingStrategy = new BottomLeft();
        OrderStrategy<Rectangle> orderStrategy = new AreaDescOrder();
        SelectStrategy<PackingSolution, Rectangle> selectStrategy = new FirstFitStrategy(packingStrategy);
        this.greedy = new Greedy<>(orderStrategy, selectStrategy);
    }

    @Override
    public Iterable<PackingSolution> getNeighbors(PackingSolution solution) {
        List<PackingSolution> neighbors = new ArrayList<>();

        PackingSolution cloneSolution = solution.copy();

        cloneSolution.boxes().sort(Comparator.comparingInt(Box::getUsedArea));

        int minUnpack;
        int numBox = cloneSolution.boxes().size() / 2;

        if (numBox < 15 ) {
            minUnpack = 10;
        } else {
            minUnpack = 15;
        }
        int numUnpackBox = Math.min(minUnpack, numBox);

        for  (int i = 0; i < numUnpackBox; i++) {
            PackingSolution temp = cloneSolution.copy();
            Box box = temp.boxes().get(i);
            List<Rectangle> copiedRectangles = box.getRectangles()
                    .stream()
                    .map(Rectangle::copy)
                    .toList();

            temp.boxes().remove(box);
            PackingSolution baseSolution = temp.copy();
            PackingSolution improvedSolution = this.greedy.solve(baseSolution, copiedRectangles);
            neighbors.add(improvedSolution);
        }

        return neighbors;
    }
}
