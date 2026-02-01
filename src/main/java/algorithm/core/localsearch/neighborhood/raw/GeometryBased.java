package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.extender.raw.BestFitExtender;
import algorithm.core.greedy.extender.raw.FirstFitExtender;
import algorithm.core.greedy.extender.generic.GreedyExtender;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.LargestAreaFirst;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.PackingSolution;

import java.util.ArrayList;
import java.util.List;

public class GeometryBased implements Neighborhood<PackingSolution> {
    private final GreedyAlgorithm<PackingSolution, Rectangle> greedyAlgorithm;

    public GeometryBased() {
        PackingStrategy packingStrategy = new BottomLeft();
        GreedyOrdering<Rectangle> greedyOrdering = new LargestAreaFirst();
        GreedyExtender<PackingSolution, Rectangle> greedyExtender = new BestFitExtender(packingStrategy);
        this.greedyAlgorithm = new GreedyAlgorithm<>(greedyOrdering, greedyExtender);
    }

    @Override
    public Iterable<PackingSolution> getNeighbors(PackingSolution solution) {
            List<PackingSolution> neighborSolutions = new ArrayList<>();

        // create a new solution (copy from current solution) but don't copy the last box
        PackingSolution cloneSolution = solution.copy();
        // unpack the last existing box, try to optimize the solution by repacking it into other boxes
        int numberUnpackedBox = 0;
        int maxUnpackedBox = 5;

        while (numberUnpackedBox < maxUnpackedBox) {
            PackingSolution tempSolution = cloneSolution.copy();
            Box lastBox = tempSolution.boxes().getLast();

            // get a list of placed rectangles in the last box
            List<Rectangle> unpackedRectangle = lastBox.getRectangles()
                    .stream()
                    .map(Rectangle::copy)
                    .toList();

            // try to make box num -= 1
            tempSolution.boxes().remove(lastBox); // remove the last box
            PackingSolution baseSolution = tempSolution.copy();

            PackingSolution improvedSolution = greedyAlgorithm.solve(baseSolution, unpackedRectangle);
            neighborSolutions.add(improvedSolution);

            numberUnpackedBox ++;
        }

        return neighborSolutions;
    }
}
