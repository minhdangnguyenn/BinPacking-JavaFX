package algorithm.greedy.putting;

public enum PuttingStrategyType {
    BOTTOM_LEFT("Bottom Left"),
    SHELF("Shelf");

    private final String displayName;

    PuttingStrategyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PuttingStrategyType fromDisplayName(String displayName) {
        for (PuttingStrategyType type : PuttingStrategyType.values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return null;
    }
}
