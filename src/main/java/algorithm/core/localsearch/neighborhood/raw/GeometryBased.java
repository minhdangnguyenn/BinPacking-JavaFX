package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.extender.raw.FirstFitExtender;
import algorithm.core.greedy.extender.generic.GreedyExtender;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.LargestAreaFirst;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.model.Rectangle;
import algorithm.solution.PackingSolution;

import java.util.ArrayList;

public class GeometryBased implements Neighborhood<PackingSolution> {
    private Iterable<PackingSolution> neighborSolutions = new ArrayList<>();
    public GeometryBased() {
        PackingStrategy packingStrategy = new BottomLeft();
        GreedyOrdering<Rectangle> greedyOrdering = new LargestAreaFirst();
        GreedyExtender<PackingSolution, Rectangle> greedyExtender = new FirstFitExtender(packingStrategy);
    }

    @Override
    public Iterable<PackingSolution> getNeighbors(PackingSolution solution) {
        boolean isImproved = false;
        // create a new solution (copy from current solution) but don't copy the last box

        // unpack the last existing box, try to optimize the solution by repacking it into other boxes
        // try to make box num -= 1

        // keep do that, stop when 3 boxes repack but unoptimized


        return this.neighborSolutions;
    }
}
