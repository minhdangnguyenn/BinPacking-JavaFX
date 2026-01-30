package model.binpacking.greedy.selectionStrategy;

import java.util.ArrayList;
import java.util.Comparator;
import model.algorithm.greedy.GreedySelection;
import model.binpacking.BinRectangle;

public class HeightGreedyStrategy extends GreedySelection<BinRectangle> {

    public HeightGreedyStrategy(ArrayList<BinRectangle> rectangles) {
        super(rectangles);
    }

    @Override
    public ArrayList<BinRectangle> orderItems(ArrayList<BinRectangle> items) {
        items.sort(Comparator.comparingInt(BinRectangle::getHeight).reversed());
        return items;
    }
}
