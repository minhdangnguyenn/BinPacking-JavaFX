package algorithm;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.raw.GreedyOrderingType;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.packing.raw.RandomPacking;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.ordering.raw.SideDescOrder;
import algorithm.core.greedy.extender.generic.GreedyStrategy;
import algorithm.core.greedy.extender.raw.FirstFitStrategy;
import algorithm.core.localsearch.LocalSearchAlgorithm;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.neighborhood.raw.GeometryBased;
import algorithm.core.localsearch.neighborhood.raw.NeighborhoodType;
import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.core.localsearch.objective.raw.MinimizeBoxesNumber;
import algorithm.solution.PackingSolution;
import javafx.application.Platform;
import algorithm.model.Rectangle;
import algorithm.model.Box;
import ui.BoxVisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AlgorithmRunner {
    // Instance data
    private ArrayList<Rectangle> instances;
    private int boxLength;
    private PackingSolution greedySolution;
    private PackingSolution localSearchSolution;
    private long runtimeMs;
    
    public static class AlgorithmConfig {
        public int rectangleCount;
        public int minWidth;
        public int maxWidth;
        public int minHeight;
        public int maxHeight;
        public int boxLength;
        public String algorithm;
        public String neighborhood;
        public String selectionStrategy;
    }

    public static class AlgorithmResult {
        public List<Box> boxes;
        public String runtime;
        public int totalGreedyBoxes;
        public int totalLocalSearchBoxes;
        public int totalRectangles;
        public String initRuntime;
    }

    public ArrayList<Rectangle> generateTestInstances(AlgorithmConfig config) {
        // Validate inputs
        if (config.rectangleCount <= 0 || config.minWidth <= 0 || config.maxWidth <= 0 ||
            config.minHeight <= 0 || config.maxHeight <= 0 || config.boxLength <= 0) {
            throw new IllegalArgumentException("All inputs must be positive integers.");
        }

        if (config.minWidth > config.maxWidth) {
            throw new IllegalArgumentException("minWidth cannot be greater than maxWidth.");
        }
        if (config.minHeight > config.maxHeight) {
            throw new IllegalArgumentException("minHeight cannot be greater than maxHeight.");
        }

        // Ensure boxLength can fit the min/max rectangle sizes
        if (config.boxLength < config.minWidth || config.boxLength < config.minHeight) {
            throw new IllegalArgumentException(
                "Box length must be at least as big as minWidth and minHeight."
            );
        }
        if (config.boxLength < config.maxWidth || config.boxLength < config.maxHeight) {
            throw new IllegalArgumentException(
                "Box length cannot be smaller than maxWidth or maxHeight."
            );
        }

        // Store box length
        this.boxLength = config.boxLength;
        
        // Generate rectangles
        this.instances = new ArrayList<>();
        for (int i = 0; i < config.rectangleCount; i++) {
            int width = (int) (Math.random() * (config.maxWidth - config.minWidth + 1)) + config.minWidth;
            int height = (int) (Math.random() * (config.maxHeight - config.minHeight + 1)) + config.minHeight;
            
            Rectangle rect = new Rectangle(i, width, height);
            this.instances.add(rect);
        }

        System.out.println("Generated " + this.instances.size() + " rectangles");
        
        // Reset solutions
        this.greedySolution = null;
        this.localSearchSolution = null;

        return this.instances;
    }

    public void runAlgorithm(AlgorithmConfig config, Consumer<AlgorithmResult> onComplete) {
        new Thread(() -> {
            if (this.instances == null || this.instances.isEmpty()) {
                System.out.println("You need to generate instances first");
                return;
            }

            AlgorithmResult result = new AlgorithmResult();
            long start = System.nanoTime();
            
            if (AlgorithmType.GREEDY.name().equals(config.algorithm)) {
                PackingStrategy bottomLeft = new BottomLeft();
                String strategy = config.selectionStrategy != null 
                        ? config.selectionStrategy 
                        : GreedyOrderingType.LARGEST_AREA_FIRST.name();
                runGreedy(strategy, bottomLeft);
            }
            else if (AlgorithmType.LOCALSEARCH.name().equals(config.algorithm)) {
                String strategy = config.selectionStrategy != null
                        ? config.selectionStrategy
                        : GreedyOrderingType.LARGEST_AREA_FIRST.name();

                PackingStrategy randomPacker = new RandomPacking();

                long startInit = System.nanoTime();
                runGreedy(strategy, randomPacker); // create init solution
                long initTime = (System.nanoTime() - startInit) / 1_000_000;
                result.initRuntime = String.format("%.2f ms", (double) initTime);
                
                String neigh = config.neighborhood != null
                        ? config.neighborhood
                        : NeighborhoodType.GEOMETRY.name();
                runLocalSearch(neigh);
            }

            this.runtimeMs = (System.nanoTime() - start) / 1_000_000;


            
            // Show local search solution if available, otherwise show greedy
            if (this.localSearchSolution != null) {
                result.boxes = BoxVisualizer.selectBoxesToDisplay(
                        this.localSearchSolution.boxes()
                );
            } else {
                result.boxes = BoxVisualizer.selectBoxesToDisplay(
                        this.greedySolution.boxes()
                );
            }
            
            result.runtime = String.format("%.2f ms", (double) this.runtimeMs);

            result.totalGreedyBoxes = this.greedySolution.boxes().size();
            result.totalLocalSearchBoxes =
                    this.localSearchSolution == null
                            ? 0
                            : this.localSearchSolution.boxes().size();
            result.totalRectangles = this.greedySolution.boxes().stream()
                    .mapToInt(b -> b.getRectangles().size())
                    .sum();

            Platform.runLater(() -> onComplete.accept(result));
        }).start();
    }
    
    /**
     * Run greedy algorithm
     */
    private void runGreedy(String greedyStrategy, PackingStrategy packingStrategy) {
        GreedyOrdering<Rectangle> ordering;

        if (GreedyOrderingType.LARGEST_AREA_FIRST.name().equalsIgnoreCase(greedyStrategy)) {
            ordering = new AreaDescOrder();
        } else if (GreedyOrderingType.LARGEST_SIDE_FIRST.name().equalsIgnoreCase(greedyStrategy)) {
            ordering = new SideDescOrder();
        } else {
            throw new IllegalArgumentException("Unknown greedy strategy: " + greedyStrategy);
        }

        PackingSolution initialSolution = new PackingSolution(this.boxLength);
        GreedyStrategy<PackingSolution, Rectangle> greedySelection =
                new FirstFitStrategy(packingStrategy);

        GreedyAlgorithm<PackingSolution, Rectangle> greedyAlgorithm = 
                new GreedyAlgorithm<>(ordering, greedySelection);

        this.greedySolution = greedyAlgorithm.solve(initialSolution, instances);
        
        System.out.println("Greedy solution: " + this.greedySolution.boxes().size() + " boxes");
    }
    
    /**
     * Run local search algorithm
     */
    private void runLocalSearch(String neighborType) {
        if (this.greedySolution == null) {
            throw new IllegalStateException("No greedy solution found. Check again initial solution !");
        }

        System.out.println("\n=== Starting Local Search ===");
        int initialBoxes = this.greedySolution.boxes().size();
        System.out.println("Initial boxes (from random place greedy): " + initialBoxes);

        // Create neighborhood
        Neighborhood<PackingSolution> neighborhood = new GeometryBased();
        Objective<PackingSolution> objective = new MinimizeBoxesNumber();
        int maxIteration = 1000;
        
        LocalSearchAlgorithm<PackingSolution> localSearch =
                new LocalSearchAlgorithm<>(
                        neighborhood,
                        objective,
                        maxIteration
                );

        this.localSearchSolution = localSearch.solve(this.greedySolution);

        System.out.println("Local search solution: " + this.localSearchSolution.boxes().size() + " boxes");
        System.out.println("Improvement: " + (initialBoxes - this.localSearchSolution.boxes().size()) + " boxes saved-compared to initial solution");
    }
}
