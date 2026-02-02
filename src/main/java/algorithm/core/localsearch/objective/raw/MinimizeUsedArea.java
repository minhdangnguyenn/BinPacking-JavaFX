package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.solution.raw.PackingSolution;

import java.util.List;

public class MinimizeUsedArea implements Objective<PackingSolution> {

    @Override
    public double evaluate(PackingSolution solution) {
        List<Box> boxes = solution.boxes();
        long totalUsedArea = 0;
        for (Box box : boxes) {
            totalUsedArea += (int) Math.pow(box.getUtilization(), 2);
        }
        return (double) -totalUsedArea / boxes.size();
    }
}
