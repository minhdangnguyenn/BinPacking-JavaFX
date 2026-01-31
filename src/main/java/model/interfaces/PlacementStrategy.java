package model.interfaces;

public abstract class PlacementStrategy<I, S> {
    protected abstract boolean checkThenAdd(I item, S solution);
}
