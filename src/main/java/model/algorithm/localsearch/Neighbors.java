package model.algorithm.localsearch;


import model.algorithm.AbstractSolution;
import model.binpacking.AlgSolution;

import java.util.ArrayList;

public abstract class Neighbors<S extends AbstractSolution> {
    // abstract Iterable<S> getNeighbors(S solution);

    public abstract ArrayList<S> getNeighbors();
}

