package algorithm.core.localsearch.objective.generic;

import algorithm.solution.generic.Solution;

public interface IterationAwareObjective<S extends Solution> extends Objective<S> {
    void setIteration(int iteration, int maxIterations);
}