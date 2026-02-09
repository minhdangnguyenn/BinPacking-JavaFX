package algorithm.core.localsearch.neighborhood.raw;

public enum NeighborhoodType {
    GEOMETRY("Geometry-based"),
    PERMUTATION("Rule-Based"),
    OVERLAP("Partially Overlapped");

    NeighborhoodType(String name) {
    }
}
