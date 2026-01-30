package controller;

import java.util.ArrayList;
import model.algorithm.greedy.GreedyAlgorithm;
import model.algorithm.greedy.GreedySelection;
import model.binpacking.AlgSolution;
import model.binpacking.BinRectangle;
import model.binpacking.Box;
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
    private AlgSolution solution;

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

    public AlgSolution getSolution() {
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
        this.solution = new AlgSolution(this.numberInstances);

        GreedyAlgorithm<BinRectangle, Box, AlgSolution> alg =
            new GreedyAlgorithm<>(solution, strategy, placer);

        AlgSolution sol = alg.solve();

        System.out.println("Solution boxes: " + sol.getItems().size());
        System.out.println("Runtime:" + sol.getFormattedRunTime());
        System.out.println("\nBoxes with rectangles:");

        for (int i = 0; i < sol.getItems().size(); i++) {
            Box box = sol.getItems().get(i);

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

        sol.printStats();
    }
}
