package algorithm.core.localsearch;

import algorithm.solution.generic.Solution;
import algorithm.solution.raw.OverlapPackingSolution;

public class EarlyStop {

    private double currentScore = 0.0;
    private int noImprovementCount = 0;
    private static final int NO_IMPROVEMENT_LIMIT = 10;

    public boolean shouldStop(int currentIteration, int maxIterations, Solution solution, double score) {
        if (currentIteration >= maxIterations) {
            return true;
        }

        // Disable for overlap packing
        // rely only on iteration limit to stop
        if (solution instanceof OverlapPackingSolution) {
            return false;
        }

        if (score > currentScore) {
            currentScore = score;
            noImprovementCount = 0;
            return false;
        } else {
            noImprovementCount++;
            return noImprovementCount >= NO_IMPROVEMENT_LIMIT;
        }
    }
}