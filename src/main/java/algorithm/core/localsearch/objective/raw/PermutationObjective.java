package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.solution.raw.PermutationSolution;

public class PermutationObjective implements Objective<PermutationSolution> {
    @Override
    public double evaluate(PermutationSolution solution) {
        return (new MaximizeUsedArea()).evaluate(solution.decode());
    }
}
