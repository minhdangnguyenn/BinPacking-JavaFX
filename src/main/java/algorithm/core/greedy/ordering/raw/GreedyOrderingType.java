package algorithm.core.greedy.ordering.raw;

public enum GreedyOrderingType {
    LARGEST_AREA_FIRST("Largest Area First"),
    LARGEST_SIDE_FIRST("Largest Side First");

    private final String displayName;

    GreedyOrderingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static GreedyOrderingType fromDisplayName(String displayName) {
        for (GreedyOrderingType type : GreedyOrderingType.values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return null;
    }
}