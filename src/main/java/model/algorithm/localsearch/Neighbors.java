package model.algorithm.localsearch;


import model.binpacking.AlgSolution;

import java.util.ArrayList;

public abstract class Neighbors<S, B> {
    // abstract Iterable<S> getNeighbors(S solution);

    public abstract ArrayList<AlgSolution> getNeighbors();
}

