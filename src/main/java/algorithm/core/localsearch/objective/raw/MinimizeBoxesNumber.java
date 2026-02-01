package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.solution.PackingSolution;

public class MinimizeBoxesNumber implements Objective<PackingSolution> {

    @Override
    public double evaluate(PackingSolution solution) {
        return solution.boxes().size();
    }
}
