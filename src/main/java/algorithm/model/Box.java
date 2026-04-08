package algorithm.model;


import java.util.ArrayList;
import java.util.List;

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

    public void setId(int id) {this.id = id;}

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

    public Box copy() {
        Box newBox = new Box(this.id, this.getLength());
        for (Rectangle rectangle : this.rectangles) {
            Rectangle newRectangle = rectangle.copy();
            newBox.addRectangle(newRectangle, newRectangle.getX(), newRectangle.getY());
        }
        return newBox;
    }

    public int getUsedArea() {
        int usedArea = 0;
        for (Rectangle rect : this.rectangles) {
            usedArea += rect.getArea();
        }
        return usedArea;
    }

    public double getUtilization() {
        return ((double) this.getUsedArea() / this.area) * 100;
    }
    
    public void pushApart(Rectangle rect1, Rectangle rect2, int boxLength) {
        int xOverlap = Math.min(rect1.getX() + rect1.getWidth(), rect2.getX() + rect2.getWidth())
                - Math.max(rect1.getX(), rect2.getX());
        int yOverlap = Math.min(rect1.getY() + rect1.getHeight(), rect2.getY() + rect2.getHeight())
                - Math.max(rect1.getY(), rect2.getY());

        // no overlap -> nothing to do
        if (xOverlap <= 0 && yOverlap <= 0) {
            return;
        }

        // clamp helper
        java.util.function.IntUnaryOperator clampX1 = v -> Math.max(0, Math.min(v, boxLength - rect1.getWidth()));
        java.util.function.IntUnaryOperator clampY1 = v -> Math.max(0, Math.min(v, boxLength - rect1.getHeight()));
        java.util.function.IntUnaryOperator clampX2 = v -> Math.max(0, Math.min(v, boxLength - rect2.getWidth()));
        java.util.function.IntUnaryOperator clampY2 = v -> Math.max(0, Math.min(v, boxLength - rect2.getHeight()));

        if (xOverlap >= yOverlap) {
            // push along X
            int rect1Distance = Math.min(rect1.getX(), boxLength - rect1.getX() - rect1.getWidth());
            int rect2Distance = Math.min(rect2.getX(), boxLength - rect2.getX() - rect2.getWidth());

            int rect1CenterX = rect1.getX() + rect1.getWidth() / 2;
            int rect2CenterX = rect2.getX() + rect2.getWidth() / 2;

            int direction1 = (rect1CenterX < rect2CenterX) ? -1 : 1;
            int direction2 = -direction1;

            if (rect1Distance > rect2Distance) {
                int toPush = Math.min(rect1Distance, xOverlap);
                int newX = clampX1.applyAsInt(rect1.getX() + direction1 * toPush);
                rect1.setPosition(newX, rect1.getY());
            } else {
                int toPush = Math.min(rect2Distance, xOverlap);
                int newX = clampX2.applyAsInt(rect2.getX() + direction2 * toPush);
                rect2.setPosition(newX, rect2.getY());
            }
        } else {
            // push along Y
            int rect1Distance = Math.min(rect1.getY(), boxLength - rect1.getY() - rect1.getHeight());
            int rect2Distance = Math.min(rect2.getY(), boxLength - rect2.getY() - rect2.getHeight());

            int rect1CenterY = rect1.getY() + rect1.getHeight() / 2;
            int rect2CenterY = rect2.getY() + rect2.getHeight() / 2;

            int direction1 = (rect1CenterY < rect2CenterY) ? -1 : 1;
            int direction2 = -direction1;

            if (rect1Distance > rect2Distance) {
                int toPush = Math.min(rect1Distance, yOverlap);
                int newY = clampY1.applyAsInt(rect1.getY() + direction1 * toPush);
                rect1.setPosition(rect1.getX(), newY);
            } else {
                int toPush = Math.min(rect2Distance, yOverlap);
                int newY = clampY2.applyAsInt(rect2.getY() + direction2 * toPush);
                rect2.setPosition(rect2.getX(), newY);
            }
        }
    }
}
