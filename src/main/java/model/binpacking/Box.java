package model.binpacking;

import java.util.ArrayList;

public class Box {

    int id;
    int boxL;
    int area;

    ArrayList<BinRectangle> rectangles;

    public Box(int id, int boxL) {
        this.id = id;
        this.boxL = boxL;
        this.area = boxL * boxL;
        this.rectangles = new ArrayList<BinRectangle>();
    }

    public int getLength() {
        return this.boxL;
    }

    public int getId() {
        return id;
    }

    public void addRectangle(BinRectangle rectangle) {
        this.rectangles.add(rectangle);
    }

    public ArrayList<BinRectangle> getRectangles() {
        return rectangles;
    }

    public boolean checkPossible(BinRectangle rectangle, InitPosition pos) {
        int x = pos.getX();
        int y = pos.getY();

        // 1. Boundary check (fast reject)
        if (x < 0 || y < 0) return false;
        if (x + rectangle.getWidth() > this.boxL) return false;
        if (y + rectangle.getHeight() > this.boxL) return false;

        // 2. Overlap check
        for (BinRectangle r : this.rectangles) {
            if (
                x < r.getPosition().getX() + r.getWidth() &&
                x + rectangle.getWidth() > r.getPosition().getX() &&
                y < r.getPosition().getY() + r.getHeight() &&
                y + rectangle.getHeight() > r.getPosition().getY()
            ) {
                return false;
            }
        }

        return true;
    }
}
