package algorithm.core.localsearch;

import algorithm.solution.generic.Solution;
import algorithm.solution.raw.OverlapPackingSolution;

public class Termination {

    private double currentScore = 0.0;
    private int noImprovementCount = 0;
    private static final int NO_IMPROVEMENT_LIMIT = 10;

    public boolean shouldStop(int currentIteration, int maxIterations, Solution solution, double score) {
        if (currentIteration >= maxIterations) {
            return true;
        }

        if (solution instanceof OverlapPackingSolution) {
            // For overlap packing we only rely on iteration limit to stop
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