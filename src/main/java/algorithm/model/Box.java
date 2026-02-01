package algorithm.model;


import java.util.ArrayList;

public class Box {

    int id;
    int boxL;
    int area;

    ArrayList<Rectangle> rectangles;

    public Box(int id, int boxL) {
        this.id = id;
        this.boxL = boxL;
        this.area = boxL * boxL;
        this.rectangles = new ArrayList<Rectangle>();
    }

    public int getLength() {
        return this.boxL;
    }

    public int getArea() { return this.area; }

    public int getId() {
        return id;
    }

    public boolean isOverlapping(Rectangle rect1, Rectangle rect2) {
        return (
                rect1.getX() < rect2.getX() + rect2.getWidth() &&
                        rect1.getX() + rect1.getWidth() > rect2.getX() &&
                        rect1.getY() < rect2.getY() + rect2.getHeight() &&
                        rect1.getY() + rect1.getHeight() > rect2.getY()
        );
    }

    public boolean isOverlapping(Rectangle rectangle) {
        for (Rectangle rect : rectangles) {
            if (isOverlapping(rect, rectangle)) {
                return true;
            }
        }
        return false;
    }

    public void addRectangle(Rectangle rectangle, int X, int Y) {
        rectangles.add(rectangle);
        rectangle.setPosition(X, Y);
    }

    public ArrayList<Rectangle> getRectangles() {
        return rectangles;
    }

    public boolean checkPossible(Rectangle rectangle, InitPosition pos) {
        int x = pos.getX();
        int y = pos.getY();

        // 1. Boundary check (fast reject)
        if (x < 0 || y < 0) return false;
        if (x + rectangle.getWidth() > this.boxL) return false;
        if (y + rectangle.getHeight() > this.boxL) return false;

        // 2. Overlap check
        for (Rectangle r : this.rectangles) {
            if (
                x < r.getX() + r.getWidth() &&
                x + rectangle.getWidth() > r.getX() &&
                y < r.getY() + r.getHeight() &&
                y + rectangle.getHeight() > r.getY()
            ) {
                return false;
            }
        }

        return true;
    }

    public double overlapRate(Rectangle rect1, Rectangle rect2) {
        if (!isOverlapping(rect1, rect2)) {
            return 0.0;
        }
        int xOverlap = Math.min(
                rect1.getX() + rect1.getWidth(),
                rect2.getX() + rect2.getWidth()) - Math.max(rect1.getX(), rect2.getX()
        );
        int yOverlap = Math.min(
                rect1.getY() + rect1.getHeight(),
                rect2.getY() + rect2.getHeight()) - Math.max(rect1.getY(), rect2.getY()
        );
        int overlapArea = xOverlap * yOverlap;
        int largerArea = Math.max(rect1.getArea(), rect2.getArea());
        return (double) (overlapArea / largerArea) * 100;
    }


    public double totalOverlapRate() {
        double totalRate = 0.0;
        for (int i = 0; i < rectangles.size(); i++) {
            for (int j = i + 1; j < rectangles.size(); j++) {
                totalRate += overlapRate(rectangles.get(i), rectangles.get(j));
            }
        }
        return totalRate;
    }



    public boolean isOverflow(Rectangle rectangle) {
        return (
                rectangle.getX() + rectangle.getWidth() > boxL ||
                        rectangle.getY() + rectangle.getHeight() > boxL
        );
    }

    public void removeRectangle(Rectangle rect) {
        rect.setPosition(-1, -1); // -1 -1 means remove
        if (this.rectangles.remove(rect)) {
            System.out.println("Rectangle ID " + rect.getId() + " has been removed successfully");
        } else {
            System.out.println("Rectangle ID " + rect.getId() + " cannot be removed");
        }
    }

    public Box copy() {
        Box newBox = new Box(this.id, this.getLength());
        for (Rectangle rectangle : this.rectangles) {
            Rectangle newRectangle = rectangle.copy();
            newBox.addRectangle(newRectangle, newRectangle.getX(), newRectangle.getY());
        }
        return newBox;
    }
}
