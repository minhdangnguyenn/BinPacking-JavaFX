package model.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic abstract class for a solution state.
 * This class allows algorithms to interact with a problem
 * without knowing its specific rules or geometry.
 */
public abstract class AbstractSolution<B, S extends AbstractSolution<B, S>> {

    protected double runtime; // ms
    protected List<B> items;

    public AbstractSolution() {
        this.runtime = 0.0;
        this.items = new ArrayList<>();
    }

    public void setRunTime(double runtime) {
        this.runtime = runtime;
    }

    public String getFormattedRunTime() {
        return String.format("%.2f ms", runtime);
    }

    public List<B> getItems() {
        return this.items;
    }

    public abstract ArrayList<S> generateNeighbors();

    public abstract int getNumberOfBins();
    public abstract double getTotalUnusedArea();
}
