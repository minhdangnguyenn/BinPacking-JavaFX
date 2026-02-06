package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.solution.raw.OverlapPackingSolution;


public class OverlapObjective implements Objective<OverlapPackingSolution> {


    private double OverlapThreshold(int iteration, int maxIterations) {
        double progress = (double) iteration / maxIterations;
        return 100 * Math.pow((1 - progress), 2);
    }

    private int penalty(double threshold) {
//        if (threshold > 70) {
//            return 10;
//        }
//        else if (threshold > 50) {
//            return 100;
//        }
//        else if (threshold > 20) {
//            return 1000;
//        } else if (threshold > 5) {
//            return 5000;
//        } else return 10000;
        return (int)threshold / 5;
    }

    @Override
    public double evaluate(OverlapPackingSolution solution) {

        if (solution.currentIteration == solution.maxIterations) {
            return Double.POSITIVE_INFINITY;
        }
        int numBoxes = solution.boxes().size();

        double totalOverlapPenalty = 0;
        double threshold = OverlapThreshold(solution.currentIteration, solution.maxIterations);
        double factor = penalty(threshold);

        for (Box box : solution.boxes()) {
            for (int i = 0; i < box.getRectangles().size() - 1; i++) {
                for (int j = i+1; j < box.getRectangles().size(); j++) {
                    double overlap = box.overlapRate(box.getRectangles().get(i), box.getRectangles().get(j));

                    if (overlap > threshold) {
                        double violation = overlap - threshold;
                        totalOverlapPenalty += factor * Math.pow(violation, 2);
                    }

                }
            }
        }

        return (double) - 1000 * numBoxes - totalOverlapPenalty;
    }
}