package algorithm.core.greedy.strategy.raw;

public enum GreedyStrategyType {
    FIRST_FIT("First Fit"),
    BEST_FIT("Best Fit");

    private final String displayName;

    GreedyStrategyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
