package algorithm;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.raw.GreedyOrderingType;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.packing.raw.RandomPacking;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.ordering.raw.SideDescOrder;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.LocalSearchAlgorithm;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.neighborhood.raw.GeometryBased;
import algorithm.core.localsearch.neighborhood.raw.NeighborhoodType;
import algorithm.core.localsearch.neighborhood.raw.Overlap;
import algorithm.core.localsearch.neighborhood.raw.Permutation;
import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.core.localsearch.objective.raw.MinimizeUsedArea;
import algorithm.core.localsearch.objective.raw.OverlapObjective;
import algorithm.core.localsearch.objective.raw.PermutationObjective;
import algorithm.solution.raw.OverlapPackingSolution;
import algorithm.solution.raw.PackingSolution;
import algorithm.solution.raw.PermutationSolution;
import javafx.application.Platform;
import algorithm.model.Rectangle;
import algorithm.model.Box;
import ui.BoxVisualizer;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AlgorithmRunner {
    private ArrayList<Rectangle> instances;
    private int boxLength;
    private PackingSolution greedySolution;
    private PackingSolution localSearchSolution;
    private PackingSolution badSolution;
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
        Utils.validConfig(config);

        this.boxLength = config.boxLength;

        this.instances = new ArrayList<>();
        for (int i = 0; i < config.rectangleCount; i++) {
            int width = (int) (Math.random() * (config.maxWidth - config.minWidth + 1)) + config.minWidth;
            int height = (int) (Math.random() * (config.maxHeight - config.minHeight + 1)) + config.minHeight;
            
            Rectangle rect = new Rectangle(i, width, height);
            this.instances.add(rect);
        }

        System.out.println("Generated " + this.instances.size() + " rectangles");

        this.greedySolution = null;
        this.localSearchSolution = null;

        return this.instances;
    }

    public void runAlgorithm(
            AlgorithmConfig config,
            Consumer<AlgorithmResult> onComplete,
            int maxIteration
    ) {
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
                this.greedySolution = runGreedy(strategy, bottomLeft);
                System.out.println("FFDA greedy: " + this.greedySolution.boxes().size() + " boxes");
            }
            else if (AlgorithmType.LOCALSEARCH.name().equals(config.algorithm)) {
                initBadGreedySolution(result);

                String neighborType = config.neighborhood != null
                        ? config.neighborhood
                        : NeighborhoodType.GEOMETRY.name();

                runLocalSearch(neighborType, maxIteration);
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

    private PackingSolution runGreedy(
            String orderStrategy,
            PackingStrategy packingStrategy
    ) {
        GreedyOrdering<Rectangle> ordering;

        if (GreedyOrderingType.LARGEST_AREA_FIRST.name().equalsIgnoreCase(orderStrategy)) {
            ordering = new AreaDescOrder();
        } else if (GreedyOrderingType.LARGEST_SIDE_FIRST.name().equalsIgnoreCase(orderStrategy)) {
            ordering = new SideDescOrder();
        } else {
            throw new IllegalArgumentException("Unknown greedy strategy: " + orderStrategy);
        }

        PackingSolution initialSolution = new PackingSolution(this.boxLength);
        GreedyStrategy<PackingSolution, Rectangle> greedySelection =
                new FirstFitStrategy(packingStrategy);

        GreedyAlgorithm<PackingSolution, Rectangle> greedyAlgorithm = 
                new GreedyAlgorithm<>(ordering, greedySelection);

        this.greedySolution = greedyAlgorithm.solve(initialSolution, instances);
        return this.greedySolution;
    }

    private void runLocalSearch(
            String neighborType,
            int maxIteration
    ) {
        if (this.badSolution == null) {
            throw new IllegalStateException("No greedy solution found. Check again initial solution !");
        }

        System.out.println("\n=== Starting Local Search ===");
        int numInitialBoxes = this.badSolution.boxes().size();
        System.out.println("Initial boxes (from random place greedy): " + numInitialBoxes);

        // Create neighborhood
        if (NeighborhoodType.GEOMETRY.name().equalsIgnoreCase(neighborType)) {
            runGeometry(maxIteration);
        } else if (NeighborhoodType.RULEBASED.name().equalsIgnoreCase(neighborType)) {
            runPermutation(maxIteration);
        }
        else if (NeighborhoodType.OVERLAP.name().equalsIgnoreCase(neighborType)) {
            runOverlap(maxIteration);
        } else {
            throw new IllegalArgumentException("Unknown neighborhood type: " + neighborType);
        }
    }

    public void runGeometry(int maxIteration) {
        int numInitialBoxes = this.badSolution.boxes().size();
        Neighborhood<PackingSolution> neighborhood = new GeometryBased();
        Objective<PackingSolution> objective = new MinimizeUsedArea();

        LocalSearchAlgorithm<PackingSolution> localSearch =
                new LocalSearchAlgorithm<>(
                        neighborhood,
                        objective,
                        maxIteration
                );

        PackingSolution geometrySolution = localSearch.solve(this.badSolution);
        
        // Store the result in localSearchSolution
        this.localSearchSolution = geometrySolution;

        System.out.println("Local search solution: " + geometrySolution.boxes().size() + " boxes");
        System.out.println("Improvement: " + (numInitialBoxes - geometrySolution.boxes().size()) + " boxes saved compared to initial solution");
    }

    private void runPermutation(int maxIteration) {
        System.out.println("\n=== Starting Permutation-based Local Search ===");
        int initialBoxes = this.badSolution.boxes().size();
        System.out.println("Initial boxes (from random place greedy): " + initialBoxes);

        // Create neighborhood and objective
        Neighborhood<PermutationSolution> neighborhood = new Permutation(this.boxLength);
        Objective<PermutationSolution> objective = new PermutationObjective();

        LocalSearchAlgorithm<PermutationSolution> localSearch =
                new LocalSearchAlgorithm<>(
                        neighborhood,
                        objective,
                        maxIteration
                );

        PermutationSolution initPermutationSolution = new PermutationSolution(this.instances, this.boxLength);

        long start = System.nanoTime();
        PermutationSolution permutationSolution = localSearch.solve(initPermutationSolution);
        long end = System.nanoTime();
        long runtime = (end - start) / 1_000_000; // Convert to milliseconds

        PackingSolution decodedSolution = permutationSolution.decode();
        
        // Store the result in localSearchSolution
        this.localSearchSolution = decodedSolution;

        System.out.println("Local search Permutation solution: " + decodedSolution.boxes().size() + " boxes, runtime: " + runtime + " ms");
        System.out.println("Improvement: " + (initialBoxes - decodedSolution.boxes().size()) + " boxes saved compared to initial solution");
    }

    private void runOverlap(int maxIteration) {
        OverlapPackingSolution initial = OverlapPackingSolution.getFromPackingSolution(this.badSolution, 100);
        int numInitialBoxes = initial.boxes().size();
        Neighborhood<OverlapPackingSolution> neighborhood = new Overlap();
        Objective<OverlapPackingSolution> objective = new OverlapObjective();

        LocalSearchAlgorithm<OverlapPackingSolution> localSearch =
                new LocalSearchAlgorithm<>(
                        neighborhood,
                        objective,
                        maxIteration
                );

        PackingSolution geometrySolution = localSearch.solve(initial);
        if (hasOverlaps(geometrySolution)) {
            geometrySolution = repackAllRectangles(geometrySolution);
        }

        // Store the result in localSearchSolution
        this.localSearchSolution = geometrySolution;

        System.out.println("Local search solution: " + geometrySolution.boxes().size() + " boxes");
        System.out.println("Improvement: " + (numInitialBoxes - geometrySolution.boxes().size()) + " boxes saved compared to initial solution");
    }

    public void initBadGreedySolution(AlgorithmResult result) {
        PackingStrategy randomPacker = new RandomPacking();

        long startInit = System.nanoTime();
        String defaultStrategy = GreedyOrderingType.LARGEST_AREA_FIRST.name();
        this.badSolution = runGreedy(defaultStrategy, randomPacker); // create bad init solution
        long initTime = (System.nanoTime() - startInit) / 1_000_000;
        System.out.println("initial bad greedy: " + this.greedySolution.boxes().size() + " boxes");
        result.initRuntime = String.format("%.2f ms", (double) initTime);
    }

    private boolean hasOverlaps(PackingSolution solution) {
        for (Box box : solution.boxes()) {
            List<Rectangle> rectangles = box.getRectangles();
            for (int i = 0; i < rectangles.size(); i++) {
                for (int j = i + 1; j < rectangles.size(); j++) {
                    if (box.isOverlapping(rectangles.get(i), rectangles.get(j))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private PackingSolution repackAllRectangles(PackingSolution solution) {
        List<Rectangle> rectanglesToRepack = new ArrayList<>();
        for (Box box : solution.boxes()) {
            for (Rectangle rectangle : box.getRectangles()) {
                rectanglesToRepack.add(rectangle.copy());
            }
        }

        rectanglesToRepack.sort((a, b) -> Integer.compare(b.getArea(), a.getArea()));
        PackingSolution baseSolution = new PackingSolution(this.boxLength);

        GreedyOrdering<Rectangle> ordering = new AreaDescOrder();
        GreedyStrategy<PackingSolution, Rectangle> greedyStrategy =
                new FirstFitStrategy(new BottomLeft());
        GreedyAlgorithm<PackingSolution, Rectangle> greedyAlgorithm =
                new GreedyAlgorithm<>(ordering, greedyStrategy);

        return greedyAlgorithm.solve(baseSolution, rectanglesToRepack);
    }
}
