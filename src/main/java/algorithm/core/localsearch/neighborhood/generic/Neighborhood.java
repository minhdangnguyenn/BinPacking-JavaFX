package algorithm.core.localsearch.neighborhood.generic;

import algorithm.solution.generic.Solution;

public interface Neighborhood<S extends Solution> {
    Iterable<S> getNeighbors(S initialSolution);
}
