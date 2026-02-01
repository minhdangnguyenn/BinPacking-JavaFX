package algorithm.core.localsearch.neighborhood.raw;

public enum NeighborhoodType {
    GEOMETRY("Geometry-based");

    private final String name;

    NeighborhoodType(String name) {
        this.name = name;
    }
}
