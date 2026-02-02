package algorithm.core.localsearch.neighborhood;

import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.solution.Solution;

/**
 * This is a generic class as exercise requirements
 * @param <S> (initial solution in non-generic)
 */
public class LocalSearchAlgorithm<S extends Solution>{
    private Neighborhood<S> neighborhood;
    private final int maxIteration;
    private Objective<S> objective;

    public LocalSearchAlgorithm(
            Neighborhood<S> neighborhood,
            Objective<S> objective,
            int maxInteration
    ) {
        this.neighborhood = neighborhood;
        this.maxIteration = maxInteration;
        this.objective = objective;
    }

    public S solve(S initialSolution) {
        int i = 0;
        double maxScore = objective.evaluate(initialSolution);
        S currentSolution = initialSolution;
        while (i < this.maxIteration) {
            Iterable<S> neighbors = this.neighborhood.getNeighbors(currentSolution);

            for (S neighbor : neighbors) {
                double curretScore = objective.evaluate(neighbor);
                if (maxScore < curretScore) {
                    currentSolution = neighbor;
                    maxScore =  curretScore;
                }
            }

            i++;

        }

        return currentSolution;
    }
}
