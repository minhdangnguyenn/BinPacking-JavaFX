package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import java.util.Comparator;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
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
    private final GreedyAlgorithm<PackingSolution, Rectangle> greedyAlgorithm;

    public Geometry() {
        PackingStrategy packingStrategy = new BottomLeft();
        GreedyOrdering<Rectangle> greedyOrdering = new AreaDescOrder();
        GreedyStrategy<PackingSolution, Rectangle> greedyStrategy = new FirstFitStrategy(packingStrategy);
        this.greedyAlgorithm = new GreedyAlgorithm<>(greedyOrdering, greedyStrategy);
    }

    @Override
    public Iterable<PackingSolution> getNeighbors(PackingSolution solution) {
        List<PackingSolution> neighbors = new ArrayList<>();

        PackingSolution cloneSolution = solution.copy();

        cloneSolution.boxes().sort(Comparator.comparingInt(Box::getUsedArea));

        int minUnpack;
        int boxesSize = cloneSolution.boxes().size();

        if (boxesSize < 15 ) {
            minUnpack = 1;
        } else {
            minUnpack = 15;
        }
        int numUnpackBox = Math.min(minUnpack, boxesSize);

        for  (int i = 0; i < numUnpackBox; i++) {
            PackingSolution temp = cloneSolution.copy();
            Box box = temp.boxes().get(i);
            List<Rectangle> copiedRectangles = box.getRectangles()
                    .stream()
                    .map(Rectangle::copy)
                    .toList();

            temp.boxes().remove(box);
            PackingSolution baseSolution = temp.copy();
            PackingSolution improvedSolution = greedyAlgorithm.solve(baseSolution, copiedRectangles);
            neighbors.add(improvedSolution);
        }

        return neighbors;
    }
}
