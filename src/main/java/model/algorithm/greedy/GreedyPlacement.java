package model.algorithm.greedy;

/**
 * check if it is possible to add an item into a solution
 * e.g. Bottom Left
 */
// If return true then add else skip

import model.algorithm.AbstractSolution;
import model.algorithm.ToPlacePosition;
import model.core.Item;
import model.core.PlacementStrategy;

/**
 * check if it is possible to add an item into a solution
 * e.g. Bottom Left
 */
public abstract class GreedyPlacement<
    I extends Item, B,
    S extends AbstractSolution<B,S>
> extends PlacementStrategy<I, S> {

    public boolean checkThenAdd(I item, S solution) {
        ToPlacePosition toPlacePos = this.canPlace(item, solution);
        if (toPlacePos != null) {
            this.place(item, solution, toPlacePos);
            return true;
        }
        return false;
    }

    /**
     * check if a rectangle can be placed in a solution
     * @return Placement containing container id and position,
     *         or null if it is unplaceable
     */
    protected abstract ToPlacePosition canPlace(I item, S solution);

    /**
     * insert an item into a solution
     * @param pos container id in the solution and coordinates to place the item
     */
    protected abstract void place(I item, S solution, ToPlacePosition pos);
}
