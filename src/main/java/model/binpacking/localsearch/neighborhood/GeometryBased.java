package model.binpacking.localsearch.neighborhood;

import model.algorithm.localsearch.Neighbors;
import model.binpacking.AlgSolution;
import model.binpacking.BinRectangle;
import model.binpacking.Box;

import java.util.ArrayList;

public class GeometryBased extends Neighbors<AlgSolution> {
    private Box targetBox;
    private Box currentBox;
    private ArrayList<AlgSolution> neighbors;
    public GeometryBased() {
        
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
    public ArrayList<AlgSolution> getNeighbors() {
        // unpack the last box (because it is always the least occupied box)

        // try to pack unpacked rectangles into other box

        // collect the solutions as neighbors

        return this.neighbors;
    }
}
