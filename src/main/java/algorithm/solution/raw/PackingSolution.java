package algorithm.solution.raw;

import algorithm.model.Box;
import algorithm.solution.generic.Solution;

import java.util.ArrayList;
import java.util.List;

public record PackingSolution(List<Box> boxes) implements Solution {

    public PackingSolution(int boxSize) {
        this(new ArrayList<>());
        this.boxes.add(new Box(0, boxSize));
    }

    public void addBox(Box box) {
        boxes.add(box);
    }

    public PackingSolution copy() {
        List<Box> newBoxes = new ArrayList<>();
        for (Box box : boxes) {
            newBoxes.add(box.copy());
        }
        return new PackingSolution(newBoxes);
    }
}