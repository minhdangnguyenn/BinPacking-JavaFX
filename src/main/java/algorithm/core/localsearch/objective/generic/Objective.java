package algorithm.core.localsearch.objective.generic;

import algorithm.solution.generic.Solution;

public interface Objective<S extends Solution> {
    double evaluate(S solution);
}
