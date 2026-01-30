package model.algorithm.greedy;

import java.util.ArrayList;
import model.algorithm.AbstractSolution;
import model.core.Item;

public class GreedyAlgorithm<I extends Item, B, S extends AbstractSolution<B, S>> {

    GreedySelection<I> selectionStrategy; // e.g, first fit descending
    GreedyPlacement<I, B, S> placementStrategy; //e.g. bottom left placement
    S solution;

    public GreedyAlgorithm(
        S initSolution,
        GreedySelection<I> selectionStrategy,
        GreedyPlacement<I, B, S> placementStrategy
    ) {
        this.solution = initSolution;
        this.selectionStrategy = selectionStrategy;
        this.placementStrategy = placementStrategy;
    }

    public S solve() {
        long start = System.nanoTime();

        // 2. Order items based on the selection strategy (e.g., Area, Longest Side)
        ArrayList<I> items = this.selectionStrategy.items;
        ArrayList<I> orderedItems = this.selectionStrategy.orderItems(items);

        // 3. Start with the (initial) solution state
        // items are already ordered, so try to place them immediately
        for (I item : orderedItems) {
            this.placementStrategy.checkThenAdd(item, this.solution);
        }

        // set runtime for the solution (convert to milliseconds)
        long runtimeMs = (System.nanoTime() - start) / 1_000_000;
        this.solution.setRunTime(runtimeMs);

        return this.solution;
    }
}
