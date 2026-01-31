package app.binpacking;

import java.util.ArrayList;
import model.algorithm.greedy.GreedyAlgorithm;
import model.algorithm.greedy.GreedySelection;
import model.algorithm.localsearch.LocalSearchAlgorithm;
import model.binpacking.greedy.GreedySolution;
import model.binpacking.instances.BinRectangle;
import model.binpacking.instances.Box;
import model.binpacking.greedy.BottomLeftPlacer;
import model.binpacking.greedy.selectionStrategy.AreaGreedyStrategy;
import model.binpacking.greedy.selectionStrategy.HeightGreedyStrategy;

public class TestFramework {

    private int numberInstances;
    private int minW;
    private int maxW;
    private int minH;
    private int maxH;
    private ArrayList<BinRectangle> rectangles;
    private int boxL;
    private GreedySolution solution;

    public TestFramework(
        int numberInstances,
        int minW,
        int maxW,
        int minH,
        int maxH,
        int boxL
    ) {
        if (
            numberInstances <= 0 ||
            minW <= 0 ||
            maxW <= 0 ||
            minH <= 0 ||
            maxH <= 0 ||
            boxL <= 0
        ) {
            throw new IllegalArgumentException(
                "All inputs must be positive integers."
            );
        }

        if (minW > maxW) throw new IllegalArgumentException(
            "minW cannot be greater than maxW."
        );
        if (minH > maxH) throw new IllegalArgumentException(
            "minH cannot be greater than maxH."
        );

        // Ensure boxL can fit the min/max rectangle sizes
        if (boxL < minW || boxL < minH) {
            throw new IllegalArgumentException(
                "Box length must be at least as big as minW and minH."
            );
        }
        if (boxL < maxW || boxL < maxH) {
            throw new IllegalArgumentException(
                "Box length cannot be smaller than maxW or maxH."
            );
        }

        this.numberInstances = numberInstances;
        this.minW = minW;
        this.maxW = maxW;
        this.minH = minH;
        this.maxH = maxH;
        this.rectangles = new ArrayList<BinRectangle>();
        this.boxL = boxL;
        this.solution = null;
    }

    public GreedySolution getSolution() {
        return this.solution;
    }

    public void generateInstances() {
        for (int i = 0; i < numberInstances; i++) {
            int width = (int) (Math.random() * (maxW - minW + 1)) + minW;
            int height = (int) (Math.random() * (maxH - minH + 1)) + minH;

            BinRectangle rect = new BinRectangle(i, width, height);

            this.rectangles.add(rect);
        }

        System.out.println(
            "Generated " + this.rectangles.size() + " in test-framework"
        );
    }

    public ArrayList<BinRectangle> getRectangles() {
        return this.rectangles;
    }

    public void runGreedy(String greedyStrategy) {
        GreedySelection<BinRectangle> strategy;

        if ("Area-based".equalsIgnoreCase(greedyStrategy)) {
            strategy = new AreaGreedyStrategy(this.rectangles);
        } else if ("Height-based".equalsIgnoreCase(greedyStrategy)) {
            strategy = new HeightGreedyStrategy(this.rectangles);
        } else {
            throw new IllegalArgumentException(
                    "Unknown greedy strategy: " + greedyStrategy
            );
        }

        BottomLeftPlacer placer = new BottomLeftPlacer(this.boxL);
        this.solution = new GreedySolution(this.numberInstances);

        GreedyAlgorithm<BinRectangle, Box, GreedySolution> alg =
            new GreedyAlgorithm<>(solution, strategy, placer);

        // set the solution attribute of this instance
        // solve it
        this.solution = alg.solve();

        System.out.println("Solution boxes: " + this.solution.getItems().size());
        System.out.println("Runtime:" + this.solution.getFormattedRunTime());
        System.out.println("\nBoxes with rectangles:");

        for (int i = 0; i < this.solution.getItems().size(); i++) {
            Box box = this.solution.getItems().get(i);

            System.out.println(
                "\nBox " + i + ": " + box.getRectangles().size() + " rectangles"
            );

            for (BinRectangle rect : box.getRectangles()) {
                System.out.println(
                    "  Rectangle " +
                        rect.getId() +
                        ": " +
                        "Size: " +
                        "(" +
                        rect.getWidth() +
                        "x" +
                        rect.getHeight() +
                        ")" +
                        " at (" +
                        rect.getPosition().getX() +
                        ", " +
                        rect.getPosition().getY() +
                        ")"
                );
            }
        }

        this.solution.printStats();
    }

    public void runLocalSearch(String neighborhood) {
        // currently I am testing Geometry-based only -- neighborhood is always Geometry based
        // Check if greedy solution exists
        if (this.solution == null) {
            throw new IllegalStateException(
                    "No greedy solution found. Run runGreedy() first."
            );
        }

        // use the solution of greedy as initial solution
        System.out.println("\n=== Starting Local Search ===");
        int initialBoxes = this.solution.getNumberOfBins();
        System.out.println("Initial boxes (from greedy): " + initialBoxes);

        // Run local search
        LocalSearchAlgorithm<Box, GreedySolution> localSearch =
                new LocalSearchAlgorithm<>(this.solution);

        GreedySolution improvedSolution = localSearch.solve();

        // Update the solution with improvedSolution result
        this.solution = improvedSolution;

        // Print results
        System.out.println("After local search: " + improvedSolution.getNumberOfBins() + " boxes");
        System.out.println("Improvement: " +
                (initialBoxes - improvedSolution.getNumberOfBins()) + " boxes saved");

        // Print detailed results
        System.out.println("\nFinal boxes with rectangles:");
        for (int i = 0; i < improvedSolution.getItems().size(); i++) {
            Box box = improvedSolution.getItems().get(i);

            System.out.println(
                    "\nBox " + i + ": " + box.getRectangles().size() + " rectangles"
            );

            for (BinRectangle rect : box.getRectangles()) {
                System.out.println(
                        "  Rectangle " +
                                rect.getId() +
                                ": Size (" +
                                rect.getWidth() +
                                "x" +
                                rect.getHeight() +
                                ") at (" +
                                rect.getPosition().getX() +
                                ", " +
                                rect.getPosition().getY() +
                                ")"
                );
            }
        }

        this.solution.printStats();
    }

}
