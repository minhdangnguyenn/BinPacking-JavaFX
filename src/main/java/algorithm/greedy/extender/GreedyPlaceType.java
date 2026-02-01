package algorithm.greedy.extender;

public enum GreedyPlaceType {
    FIRST_FIT("First Fit"),
    BEST_FIT("Best Fit");

    private final String displayName;

    GreedyPlaceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static GreedyPlaceType fromDisplayName(String displayName) {
        for (GreedyPlaceType type : GreedyPlaceType.values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return null;
    }
}
