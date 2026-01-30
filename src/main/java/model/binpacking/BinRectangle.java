package model.binpacking;

import model.core.Item;

public class BinRectangle extends Item {

    int id;
    int width;
    int height;
    private boolean isRotated;
    int area;

    private InitPosition position;

    public BinRectangle(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.area = width * height;
        this.isRotated = false;
        this.position = new InitPosition(0, 0);
    }

    public int getId() {
        return id;
    }

    public int getArea() {
        return this.area;
    }

    public boolean getIsRotated() {
        return isRotated;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public InitPosition getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        this.position = new InitPosition(x, y);
    }

    public void rotate() {
        int temp = width;
        width = height;
        height = temp;
        isRotated = !isRotated;
    }
}
