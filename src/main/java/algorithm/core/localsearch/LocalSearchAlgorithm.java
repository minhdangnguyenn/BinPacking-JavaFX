package algorithm.core.localsearch;

import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.solution.generic.Solution;

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
        double minScore = objective.evaluate(initialSolution);
        S currentSolution = initialSolution;
        boolean isImproved = false;
        int unimproveIter = 0;
        int earlyStopIter = 10;

        while (i < this.maxIteration) {
            isImproved = false; // reset at start of next iteration
            Iterable<S> neighbors = this.neighborhood.getNeighbors(currentSolution);
            for (S neighbor : neighbors) {
                double currentScore = objective.evaluate(neighbor);
                if (currentScore < minScore) {
                    currentSolution = neighbor;
                    minScore = currentScore;
                    isImproved = true;
                }
            }

            if (isImproved) {
                unimproveIter = 0;
            } else {
                unimproveIter += 1;
            }

            if (unimproveIter > earlyStopIter) break;

            i++;
        }

        return currentSolution;
    }
}
