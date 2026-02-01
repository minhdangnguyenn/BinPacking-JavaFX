package algorithm.core.localsearch.neighborhood;

import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.objective.Objective;
import algorithm.solution.Solution;

/**
 * This is a generic class as exercise requirements
 * @param <S> (initial solution in non-generic)
 */
public class LocalSearchAlgorithm<S extends Solution>{
    private Neighborhood<S> neighborhood;
    private final int maxIteration;
    private S currentSolution;
    private Objective<S> objective;

    public LocalSearchAlgorithm(
            S initialSolution,
            Neighborhood<S> neighborhood,
            Objective objective,
            int maxInteration
    ) {
        this.currentSolution = initialSolution;
        this.neighborhood = neighborhood;
        this.maxIteration = maxInteration;
        this.objective = objective;
    }

    public S solve() {
        int i = 0;
        int unimprovedIter = 0;
        while (i < this.maxIteration && unimprovedIter < 5) { // stop after 5 unimproved iteration
            // each iteration has different neighbors
            // because after each iteration, currentSolution is updated
            Iterable<S> neighbors = this.neighborhood.getNeighbors(this.currentSolution);

            S betterSolution = selectBest(neighbors);

            // cannot find a better solution for this iteration
            if (betterSolution == null) {
                unimprovedIter ++;
                continue;
            };

            currentSolution = betterSolution;
            unimprovedIter = 0; // reset if found a better solution
        }

        return currentSolution;
    }

    public S selectBest(Iterable<S> neighbors) {
        double currentValue = this.objective.evaluate(this.currentSolution);

        // in non-generic use case: number of boxes of new solution is smaller than current solution
        for (S s : neighbors) {
            if (this.objective.evaluate(s) < currentValue) {
                return s;
            }
        }
        return null;
    }
}
