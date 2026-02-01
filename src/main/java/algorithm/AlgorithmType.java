package algorithm;

public enum AlgorithmType {
    GREEDY("Greedy Algorithm"),
    LOCALSEARCH("Local Search");

    private final String name;

    AlgorithmType(String s) {
        this.name = s;
    }

    public String getName() {
        return this.name;
    }
}
