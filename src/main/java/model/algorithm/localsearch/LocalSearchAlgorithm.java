package model.algorithm.localsearch;

import java.util.ArrayList;
import model.algorithm.AbstractSolution;
import model.core.Item;

public class LocalSearchAlgorithm<I extends Item, B, S extends AbstractSolution<B, S>> {

    private S currentSolution;
    public LocalSearchAlgorithm(S initialSolution) {
        currentSolution = initialSolution;
    }

    public S getCurrentSolution() {
        return this.currentSolution;
    }

    /*
    * Reducing number of boxes is the best objective that we are looking for
    * Small objective points => better solution
    * */
    public double objectiveFunction(S solution) {
        double BIG = 1_000_000;
        return solution.getNumberOfBins() * BIG + solution.getTotalUnusedArea();
    }

    public S solve() {
        boolean improved = true;

        while (improved) {
            improved = false;

            ArrayList<S> neighbors = currentSolution.generateNeighbors();

            S bestNeighbor = currentSolution;
            double bestCost = objectiveFunction(currentSolution);

            for (S neighbor : neighbors) {
                double cost = objectiveFunction(neighbor);

                if (cost < bestCost) {
                    bestCost = cost;
                    bestNeighbor = neighbor;
                    improved = true;
                }
            }

            if (improved) {
                currentSolution = bestNeighbor;
            }
        }

        return currentSolution;
    }
}
