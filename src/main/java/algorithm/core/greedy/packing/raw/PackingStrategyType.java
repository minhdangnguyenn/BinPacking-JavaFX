package algorithm.core.greedy.packing.raw;

public enum PackingStrategyType {
    BOTTOM_LEFT("Bottom Left");

    @SuppressWarnings("unused")
    private final String displayName;

    PackingStrategyType(String displayName) {
        this.displayName = displayName;
    }
}
