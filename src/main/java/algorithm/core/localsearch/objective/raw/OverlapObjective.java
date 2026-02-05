package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.solution.raw.OverlapPackingSolution;


public class OverlapObjective implements Objective<OverlapPackingSolution> {


    /**
     * This creates a decreasing threshold
     * @param iteration current interation
     * @param maxIterations max allowed iteration
     * @return a threshold value
     */
    private double OverlapThreshold(int iteration, int maxIterations) {
        double progress = (double) iteration / maxIterations;
        return 100 * Math.pow((1 - progress), 2);
    }

    private int penalty(double threshold) {
        return (int) threshold / 5;
    }

    @Override
    public double evaluate(OverlapPackingSolution solution) {

        if (solution.currentIteration == solution.maxIterations) {
            return Double.POSITIVE_INFINITY;
        }
        int numBoxes = solution.boxes().size();

        double total_overlap_penalty = 0;
        double threshold = OverlapThreshold(solution.currentIteration, solution.maxIterations);
        double factor = penalty(threshold);

        for (Box box : solution.boxes()) {
            for (int i = 0; i < box.getRectangles().size() - 1; i++) {
                for (int j = i+1; j < box.getRectangles().size(); j++) {
                    double overlap = box.overlapRate(box.getRectangles().get(i), box.getRectangles().get(j));

                    if (overlap > threshold) {
                        double violation = overlap - threshold;
                        total_overlap_penalty += factor * Math.pow(violation, 2);
                    }
                }
            }
        }

        return (double) - 1000 * numBoxes - total_overlap_penalty;
    }
}
