package app;

import java.util.ArrayList;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.raw.GreedyOrderingType;
import algorithm.core.localsearch.neighborhood.LocalSearchAlgorithm;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.neighborhood.raw.GeometryBased;
import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.core.localsearch.objective.raw.MinimizeBoxesNumber;
import algorithm.solution.PackingSolution;
import algorithm.model.Rectangle;
import algorithm.model.Box;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.LargestAreaFirst;
import algorithm.core.greedy.ordering.raw.LargestSideFirst;
import algorithm.core.greedy.extender.generic.GreedyExtender;
import algorithm.core.greedy.extender.raw.FirstFitExtender;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;

public class TestFramework {

    private int numberInstances;
    private int minW;
    private int maxW;
    private int minH;
    private int maxH;
    private ArrayList<Rectangle> instances;
    private int boxL;
    private PackingSolution packingSolution;

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
        this.instances = new ArrayList<Rectangle>();
        this.boxL = boxL;
        this.packingSolution = null;
    }

    public PackingSolution getPackingSolution() {
        return this.packingSolution;
    }

    public void generateInstances() {
        for (int i = 0; i < numberInstances; i++) {
            int width = (int) (Math.random() * (maxW - minW + 1)) + minW;
            int height = (int) (Math.random() * (maxH - minH + 1)) + minH;

            Rectangle rect = new Rectangle(i, width, height);

            this.instances.add(rect);
        }

        System.out.println(
            "Generated " + this.instances.size() + " rectangles in test-framework"
        );
    }

    public ArrayList<Rectangle> getInstances() {
        return this.instances;
    }

    public void runGreedy(String greedyStrategy) {
        GreedyOrdering<Rectangle> ordering;

        if (GreedyOrderingType.LARGEST_AREA_FIRST.name().equalsIgnoreCase(greedyStrategy)) {
            ordering = new LargestAreaFirst();
        } else if (GreedyOrderingType.LARGEST_SIDE_FIRST.name().equalsIgnoreCase(greedyStrategy)) {
            ordering = new LargestSideFirst();
        } else {
            throw new IllegalArgumentException(
                    "Unknown greedy strategy: " + greedyStrategy
            );
        }

        PackingSolution initialSolution = new PackingSolution(this.boxL);
        PackingStrategy packingStrategy = new BottomLeft();
        GreedyExtender<PackingSolution, Rectangle> greedyExtender = new FirstFitExtender(packingStrategy);

        GreedyAlgorithm<PackingSolution, Rectangle> greedyAlgorithm = new GreedyAlgorithm<>(ordering, greedyExtender);

        // wrap into timer
        long start = System.nanoTime();
        this.packingSolution = greedyAlgorithm.solve(initialSolution, instances);
        long runtimeMs = (System.nanoTime() - start) / 1_000_000;

        // 7. Print results
        System.out.println("Solution boxes: " + this.packingSolution.boxes().size());
        System.out.println("Runtime: " + runtimeMs + " ms");
        System.out.println("\nBoxes with rectangles:");

        for (int i = 0; i < this.packingSolution.boxes().size(); i++) {
            Box box = this.packingSolution.boxes().get(i);

            System.out.println(
                "\nBox " + i + ": " + box.getRectangles().size() + " rectangles"
            );

            for (Rectangle rect : box.getRectangles()) {
                System.out.println(
                    "  Rectangle " +
                        rect.getId() +
                        ": Size (" +
                        rect.getWidth() +
                        "x" +
                        rect.getHeight() +
                        ") at (" +
                        rect.getX() +
                        ", " +
                        rect.getY() +
                        ")"
                );
            }
        }

        printStats();
    }

    private void printStats() {
        if (this.packingSolution == null) {
            System.out.println("No solution available");
            return;
        }

        int totalRectangles = 0;
        for (Box box : this.packingSolution.boxes()) {
            totalRectangles += box.getRectangles().size();
        }

        System.out.println(
            "\nNumber of boxes: " +
                this.packingSolution.boxes().size() +
                " for " +
                totalRectangles +
                " rectangles"
        );
    }

    public void runLocalSearch(String neighborType) {
        // currently I am testing Geometry-based only -- neighborhood is always Geometry based
        // Check if greedy solution exists
        if (this.packingSolution == null) {
            throw new IllegalStateException(
                    "No greedy solution found. Run runGreedy() first."
            );
        }

        // use the solution of greedy as initial solution
        System.out.println("\n=== Starting Local Search ===");
        int initialBoxes = this.packingSolution.boxes().size();
        System.out.println("Initial boxes (from greedy): " + initialBoxes);

        // Run local search
        Neighborhood<PackingSolution> neighborhood = new GeometryBased();
        Objective mininizeBox = new MinimizeBoxesNumber();
        int maxIteration = 200;
        LocalSearchAlgorithm<PackingSolution> localSearch =
                new LocalSearchAlgorithm<PackingSolution>(
                        this.packingSolution,
                        neighborhood,
                        mininizeBox,
                        maxIteration
                ); //use greedy solution as initial solution

        PackingSolution improvedSolution = localSearch.solve();

        // Update the solution with improvedSolution result
        this.packingSolution = improvedSolution;

        // Print results
        System.out.println("After local search: " + improvedSolution.boxes().size() + " boxes");
        System.out.println("Improvement: " +
                (initialBoxes - improvedSolution.boxes().size()) + " boxes saved");

        // Print detailed results
        System.out.println("\nFinal boxes with rectangles:");
        for (int i = 0; i < improvedSolution.boxes().size(); i++) {
            Box box = improvedSolution.boxes().get(i);

            System.out.println(
                    "\nBox " + i + ": " + box.getRectangles().size() + " rectangles"
            );

            for (Rectangle rect : box.getRectangles()) {
                System.out.println(
                        "  Rectangle " +
                                rect.getId() +
                                ": Size (" +
                                rect.getWidth() +
                                "x" +
                                rect.getHeight() +
                                ") at (" +
                                rect.getX() +
                                ", " +
                                rect.getY() +
                                ")"
                );
            }
        }

        printStats();
    }

}
