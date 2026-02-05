package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;
import algorithm.solution.raw.PackingSolution;

import java.util.ArrayList;
import java.util.List;

public class Overlap implements Neighborhood<OverlapPackingSolution> {
    private final GreedyAlgorithm<PackingSolution, Rectangle> greedyAlgorithm;
    private int currentIteration = 0;

    public Overlap() {
        PackingStrategy packingStrategy = new BottomLeft();
        GreedyOrdering<Rectangle> greedyOrdering = new AreaDescOrder();
        GreedyStrategy<PackingSolution, Rectangle> greedyStrategy = new FirstFitStrategy(packingStrategy);
        greedyAlgorithm = new GreedyAlgorithm<>(greedyOrdering, greedyStrategy);
    }



    @Override
    public Iterable<OverlapPackingSolution> getNeighbors(OverlapPackingSolution solution) {
        currentIteration++;

        List<OverlapPackingSolution> neighbors = new ArrayList<>();



        return neighbors;
    }
}
