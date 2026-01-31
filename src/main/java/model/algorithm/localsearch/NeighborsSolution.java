package model.algorithm.localsearch;


import model.algorithm.AbstractSolution;

import java.util.ArrayList;

public abstract class NeighborsSolution<S extends AbstractSolution> {
    // abstract Iterable<S> getNeighbors(S solution);

    public abstract ArrayList<S> getNeighbors();
}

