package algorithm.core.localsearch.objective;

import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.PackingSolution;

public class MinimizeBoxesNumber implements Objective<PackingSolution> {

    @Override
    public double evaluate(PackingSolution solution) {
        return solution.boxes().size();
    }
}
