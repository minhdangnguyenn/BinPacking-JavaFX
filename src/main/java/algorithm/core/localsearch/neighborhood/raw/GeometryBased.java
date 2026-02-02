package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.extender.raw.FirstFitStrategy;
import java.util.Comparator;
import algorithm.core.greedy.extender.generic.GreedyStrategy;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
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
        GreedyOrdering<Rectangle> greedyOrdering = new AreaDescOrder();
        GreedyStrategy<PackingSolution, Rectangle> greedyStrategy = new FirstFitStrategy(packingStrategy);
        this.greedyAlgorithm = new GreedyAlgorithm<>(greedyOrdering, greedyStrategy);
    }

    @Override
    public Iterable<PackingSolution> getNeighbors(PackingSolution solution) {
            List<PackingSolution> neighborSolutions = new ArrayList<>();

        // create a new solution (copy from current solution) but don't copy the last box
        PackingSolution cloneSolution = solution.copy();

        PackingSolution clone = solution.copy();

        List<Rectangle> unpackedRectangles = new ArrayList<>();

        // sort utilization asc
        clone.boxes().sort(Comparator.comparingInt(Box::getUsedArea));

        // get the 20% of numb boxes
        int numUnpackBox = solution.boxes().size() / 5;
        PackingSolution tempSolution = cloneSolution.copy();
        for (int i = 0; i < numUnpackBox; i++) {
            Box unpackBox = tempSolution.boxes().get(i);

            for (Rectangle rectangle : unpackBox.getRectangles()) {
                unpackedRectangles.add(rectangle.copy());
            }

            tempSolution.boxes().remove(unpackBox);
        }

        //reorder by area desc
        unpackedRectangles.sort(Comparator.comparingInt(Rectangle::getArea).reversed());

        PackingSolution baseSolution = tempSolution.copy();

        PackingSolution improvedSolution = greedyAlgorithm.solve(baseSolution, unpackedRectangles);

        neighborSolutions.add(improvedSolution);

       return neighborSolutions;
    }
}
