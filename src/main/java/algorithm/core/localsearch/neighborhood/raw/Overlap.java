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

        List<Rectangle> rectangles = new ArrayList<>();
        OverlapPackingSolution temp = new OverlapPackingSolution(solution.boxes().getFirst().getLength());

        // first I try to put all rectangles to the corner
        for (Box box: solution.boxes()) {
            for (Rectangle rect: box.getRectangles()) {
                Rectangle copy = rect.copy();
                copy.setPosition(0,0);
                rectangles.add(copy);
                temp.boxes().getFirst().addRectangle(copy, 0, 0);
            }
        }

        neighbors.add(temp);

        return neighbors;
    }
}
