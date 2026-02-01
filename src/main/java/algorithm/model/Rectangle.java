package algorithm.model;

public class Rectangle implements Item {

    private final int width;
    private final int height;
    private final int area;

    // position in Box
    private int x;
    private int y;
    private final boolean rotated; // compared to the original
    private int id;

    public Rectangle(int id, int width, int height) {
        this.width = width;
        this.height = height;
        this.area = width * height;
        this.rotated = false;
        this.id = id;
    }

    public Rectangle(int id, int width, int height, boolean rotated) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.area = width * height;
        this.rotated = rotated;
    }

    public int getId() { return this.id; }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getArea() {
        return area;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isRotated() {
        return rotated;
    }

    public Rectangle rotate() {
        return new Rectangle(this.id, height, width, !rotated);
    }

    public boolean isSideway() {
        return width > height;
    }

    public boolean isUpright() {
        return height >= width;
    }

    public Rectangle copy() {
        Rectangle rect = new Rectangle(this.id, this.width, this.height, this.rotated);
        rect.setPosition(this.x, this.y);
        return rect;
    }

}
