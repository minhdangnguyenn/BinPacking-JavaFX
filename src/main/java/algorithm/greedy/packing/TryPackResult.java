package algorithm.greedy.packing;

public record TryPackResult(Integer x, Integer y, boolean rotated) {
    public static TryPackResult fail() {
        return null;
    }
}
