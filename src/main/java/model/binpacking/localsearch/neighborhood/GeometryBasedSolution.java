package model.binpacking.localsearch.neighborhood;

import model.algorithm.localsearch.NeighborsSolution;
import model.binpacking.greedy.GreedySolution;
import model.binpacking.instances.BinRectangle;
import model.binpacking.instances.Box;

import java.util.ArrayList;

public class GeometryBasedSolution extends NeighborsSolution<GreedySolution> {
    private Box targetBox;
    private Box currentBox;
    private ArrayList<GreedySolution> neighbors;
    public GeometryBasedSolution() {
        
    }

    public ArrayList<BinRectangle> unpack(Box box) {
        ArrayList<BinRectangle> unpackedRectangles = new ArrayList<BinRectangle>();
        for (BinRectangle rect : box.getRectangles()) {
            unpackedRectangles.add(rect);
            box.removeRectangle(rect);
        }

        return unpackedRectangles;
    }

    @Override
    public ArrayList<GreedySolution> getNeighbors() {
        // unpack the last box (because it is always the least occupied box)

        // try to pack unpacked rectangles into other box

        // collect the solutions as neighbors

        return this.neighbors;
    }
}
