package algorithm.greedy.putting;

public record TryPutResult(Integer x, Integer y, boolean rotated) {
    public static TryPutResult fail() {
        return null;
    }
}
