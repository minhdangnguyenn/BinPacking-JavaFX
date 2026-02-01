package algorithm.core.localsearch.neighborhood;

import algorithm.core.greedy.extender.raw.FirstFitExtender;
import algorithm.core.greedy.extender.generic.GreedyExtender;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.LargestAreaFirst;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.model.Rectangle;
import algorithm.solution.PackingSolution;

import java.util.ArrayList;

public class GeometryBased implements Neighborhood <PackingSolution>{
    private Iterable<PackingSolution> neighborSolutions = new ArrayList<>();
    public GeometryBased() {
        PackingStrategy packingStrategy = new BottomLeft();
        GreedyOrdering<Rectangle> greedyOrdering = new LargestAreaFirst();
        GreedyExtender<PackingSolution, Rectangle> greedyExtender = new FirstFitExtender(packingStrategy);
    }

    @Override
    public Iterable<PackingSolution> getNeighbors(PackingSolution solution) {
        return this.neighborSolutions;
    }
}
