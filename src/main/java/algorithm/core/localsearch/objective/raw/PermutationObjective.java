package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.solution.raw.PackingSolution;
import algorithm.solution.raw.PermutationSolution;

import java.util.List;

public class PermutationObjective implements Objective<PermutationSolution> {
    @Override
    public double evaluate(PermutationSolution solution) {
        return (new MinimizeUsedArea()).evaluate(solution.decode());
    }
}
