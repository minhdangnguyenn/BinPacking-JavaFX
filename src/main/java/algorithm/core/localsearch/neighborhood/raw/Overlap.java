package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.PackingSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Overlap implements Neighborhood<PackingSolution> {
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
    public Iterable<PackingSolution> getNeighbors(PackingSolution solution) {
        List<PackingSolution> neighborSolutions = new ArrayList<>();

        // copy the solution
        PackingSolution tempSolution = solution.copy();

        List<Rectangle> unpackedRectangles = new ArrayList<>();

        // sort the boxes by overlap rate (descending)
        tempSolution.boxes().sort(Comparator.comparingDouble(Box::totalOverlapRate).reversed());

        // determine how many boxes to unpack
        int numUnpackBox = Math.max(1, (int) (tempSolution.boxes().size() * this.removalFraction));
        numUnpackBox = Math.min(numUnpackBox, tempSolution.boxes().size());

        // start unpack
        // PackingSolution tempSolution = cloneSolution.copy();
        for (int i = 0; i < numUnpackBox; i++) {
            Box unpackBox = tempSolution.boxes().get(i);

            for (Rectangle rectangle : unpackBox.getRectangles()) {
                unpackedRectangles.add(rectangle.copy());
            }

            tempSolution.boxes().remove(unpackBox);
            i--; // adjust index because we removed an element
            numUnpackBox--; // reduce target since list shrank
        }

        // reorder by area desc
        unpackedRectangles.sort(Comparator.comparingInt(Rectangle::getArea).reversed());

        // repack removed rectangles via greedy algorithm
        PackingSolution baseSolution = tempSolution.copy();
        PackingSolution improvedSolution = greedyAlgorithm.solve(baseSolution, unpackedRectangles);

        neighborSolutions.add(improvedSolution);
        return neighborSolutions;
    }
}
