package model.algorithm;

/**
 * Represents a concrete placement position for a rectangle,
 * optionally with rotation information
 */
public class ToPlacePosition {

    private final int cid;
    private final int x;
    private final int y;
    private Boolean shouldRotate; // nullable = optional

    // Constructor without rotation
    public ToPlacePosition(int cid, int x, int y) {
        this.cid = cid;
        this.x = x;
        this.y = y;
        this.shouldRotate = null;
    }

    // Constructor with rotation
    public ToPlacePosition(int cid, int x, int y, Boolean shouldRotate) {
        this.cid = cid;
        this.x = x;
        this.y = y;
        this.shouldRotate = shouldRotate;
    }

    // Getters
    public int getCid() {
        return cid;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Boolean getShouldRotate() {
        return shouldRotate;
    }

    // Setter for rotation if needed
    public void setShouldRotate(Boolean shouldRotate) {
        this.shouldRotate = shouldRotate;
    }
}
