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
import algorithm.solution.generic.Solution;
import algorithm.solution.raw.PackingSolution;

import java.util.List;

public class Overlap implements Neighborhood {
    private final GreedyAlgorithm<PackingSolution, Rectangle> greedyAlgorithm;
    private final double removalFraction; // e.g. 0.2 (number of boxes to unpack: 20%)

    public Overlap(double removalFraction) {
        PackingStrategy packingStrategy = new BottomLeft();
        GreedyOrdering<Rectangle> greedyOrdering = new AreaDescOrder();
        GreedyStrategy<PackingSolution, Rectangle> greedyStrategy = new FirstFitStrategy(packingStrategy);
        this.greedyAlgorithm = new GreedyAlgorithm<>(greedyOrdering, greedyStrategy);
        this.removalFraction = removalFraction;
    }

    @Override
    public Iterable getNeighbors(Solution initialSolution) {
        return null;
    }
}
