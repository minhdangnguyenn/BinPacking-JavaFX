package algorithm.core.greedy.packing.raw;

public record TryPackResult(Integer x, Integer y, boolean rotated) {
    public static TryPackResult fail() {
        return null;
    }
}
