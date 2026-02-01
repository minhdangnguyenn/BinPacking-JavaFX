package algorithm.core.localsearch.neighborhood;

public enum NeighborhoodType {
    GEOMETRY("Geometry-based");

    private final String name;

    NeighborhoodType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
