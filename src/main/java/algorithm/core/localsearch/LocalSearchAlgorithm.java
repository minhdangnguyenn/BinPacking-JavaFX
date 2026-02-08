package algorithm.core.localsearch;

import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.solution.generic.Solution;

/**
 * This is a generic class as exercise requirements
 * @param <S> (initial solution in non-generic)
 */
public class LocalSearchAlgorithm<S extends Solution>{
    private final Neighborhood<S> neighborhood;
    private final int maxIteration;
    private final Objective<S> objective;
    private final EarlyStop earlyStop = new EarlyStop();

    public LocalSearchAlgorithm(
            Neighborhood<S> neighborhood,
            Objective<S> objective,
            int maxInteration
    ) {
        this.neighborhood = neighborhood;
        this.maxIteration = maxInteration;
        this.objective = objective;
    }

    public S solve(S initial) {
        S current = initial;
        double currentScore = objective.evaluate(current);

        int iteration = 0;
        while (!earlyStop.shouldStop(iteration, maxIteration, current, currentScore)) {
            S bestNeighbor = current;
            double bestScore = currentScore;
            for (S neighbor : neighborhood.getNeighbors(current)) {
                double neighborScore = objective.evaluate(neighbor);
                if (neighborScore > bestScore) {
                    bestScore = neighborScore;
                    bestNeighbor = neighbor;
                }
            }

            current = bestNeighbor;
            currentScore = bestScore;
            iteration++;
        }
        return current;
    }
}
