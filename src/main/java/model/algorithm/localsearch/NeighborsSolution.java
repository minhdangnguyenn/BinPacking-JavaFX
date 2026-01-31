package model.algorithm.localsearch;


import model.algorithm.AbstractSolution;

import java.util.ArrayList;

public abstract class NeighborsSolution<S extends AbstractSolution> {
    public abstract ArrayList<S> generateNeighbors();
}

