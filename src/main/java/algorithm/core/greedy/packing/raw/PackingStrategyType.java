package algorithm.core.greedy.packing.raw;

public enum PackingStrategyType {
    BOTTOM_LEFT("Bottom Left");
    // SHELF("Shelf");

    private final String displayName;

    PackingStrategyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PackingStrategyType fromDisplayName(String displayName) {
        for (PackingStrategyType type : PackingStrategyType.values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return null;
    }
}
