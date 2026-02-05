package algorithm.solution.raw;

import algorithm.model.Box;
import algorithm.solution.generic.Solution;

import java.util.ArrayList;
import java.util.List;

public class PackingSolution implements Solution {

    private final List<Box> boxes;
    private double allowedOverlapPercent = 100.0;

    public PackingSolution(List<Box> boxes) {
        this.boxes = boxes;
    }

    public PackingSolution(int boxLength) {
        this(new ArrayList<>());
        this.boxes.add(new Box(this.boxes.size(), boxLength));
    }

    public List<Box> boxes() {
        return boxes;
    }

    public void addBox(Box box) {
        boxes.add(box);
    }

    public double allowedOverlapPercent() {
        return allowedOverlapPercent;
    }

    public void setAllowedOverlapPercent(double allowedOverlapPercent) {
        this.allowedOverlapPercent = allowedOverlapPercent;
    }

    public PackingSolution copy() {
        List<Box> newBoxes = new ArrayList<>();
        for (Box box : boxes) {
            newBoxes.add(box.copy());
        }
        PackingSolution copy = new PackingSolution(newBoxes);
        copy.setAllowedOverlapPercent(this.allowedOverlapPercent);
        return copy;
    }
}