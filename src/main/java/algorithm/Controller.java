package algorithm;

import algorithm.core.greedy.Greedy;
import algorithm.core.greedy.ordering.raw.GreedyOrderingType;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.ordering.raw.SideDescOrder;
import algorithm.core.greedy.packing.raw.RandomPacking;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.LocalSearch;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.neighborhood.raw.Geometry;
import algorithm.core.localsearch.neighborhood.raw.NeighborhoodType;
import algorithm.core.localsearch.neighborhood.raw.Overlap;
import algorithm.core.localsearch.neighborhood.raw.Permutation;
import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.core.localsearch.objective.raw.MinimizeUsedArea;
import algorithm.core.localsearch.objective.raw.OverlapObjective;
import algorithm.core.localsearch.objective.raw.PermutationObjective;
import algorithm.model.Item;
import algorithm.solution.raw.OverlapPackingSolution;
import algorithm.solution.raw.PackingSolution;
import algorithm.solution.raw.PermutationSolution;
import javafx.application.Platform;
import algorithm.model.Rectangle;
import algorithm.model.Box;
import ui.BoxVisualizer;
import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Controller {
    private ArrayList<Rectangle> rectangles;
    private int configBoxLength;
    private PackingSolution greedySolution;
    private PackingSolution localSearchSolution;

    private long runtimeMs;

    public static class Config {
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
        public int numBadBoxes;
    }

    public void generateInstances(Config config) {
        Utils.validConfig(config);

        this.configBoxLength = config.boxLength;

        this.rectangles = new ArrayList<>();
        for (int i = 0; i < config.rectangleCount; i++) {
            int width = (int) (Math.random() * (config.maxWidth - config.minWidth + 1)) + config.minWidth;
            int height = (int) (Math.random() * (config.maxHeight - config.minHeight + 1)) + config.minHeight;

            this.rectangles.add(new Rectangle(i, width, height));
        }

        this.greedySolution = null;
        this.localSearchSolution = null;
    }

    public void runAlgorithm(
            Config config,
            Consumer<AlgorithmResult> onCompleteFn,
            int maxIteration
    ) {
        new Thread(() -> {
            if (this.rectangles == null || this.rectangles.isEmpty()) {
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
                result.initRuntime = "N/A";
                result.totalGreedyBoxes = this.greedySolution.boxes().size();
                result.totalLocalSearchBoxes = 0;
            }
            else if (AlgorithmType.LOCALSEARCH.name().equals(config.algorithm)) {
                PackingSolution badGreedy = initBadGreedySolution();

                String neighborType = config.neighborhood != null
                        ? config.neighborhood
                        : NeighborhoodType.GEOMETRY.name();

                this.localSearchSolution = runLocalSearch(neighborType, badGreedy, maxIteration);

                if (this.greedySolution != null) {
                     result.totalGreedyBoxes = this.greedySolution.boxes().size();
                } else {
                    result.totalGreedyBoxes = 0;
                }

                result.numBadBoxes = badGreedy.boxes().size();
                result.totalLocalSearchBoxes = this.localSearchSolution.boxes().size();
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

            // Calculate total rectangles from the appropriate solution
            PackingSolution solutionForCount = this.localSearchSolution != null 
                    ? this.localSearchSolution 
                    : this.greedySolution;
            
            if (solutionForCount != null) {
                result.totalRectangles = solutionForCount.boxes().stream()
                        .mapToInt(b -> b.getRectangles().size())
                        .sum();
            } else {
                result.totalRectangles = 0;
            }

            Platform.runLater(() -> onCompleteFn.accept(result));
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

        PackingSolution initialSolution = new PackingSolution(this.configBoxLength);
        GreedyStrategy<PackingSolution, Rectangle> greedySelection =
                new FirstFitStrategy(packingStrategy);

        Greedy<PackingSolution, Rectangle> greedy =
                new Greedy<>(ordering, greedySelection);

        List<Rectangle> rects = new ArrayList<>();
        for (Rectangle rect : rectangles) {
            rects.add(rect.copy());
        }

        return greedy.solve(initialSolution, rects);
    }

    private PackingSolution runLocalSearch(
            String neighborType,
            PackingSolution badGreedy,
            int maxIteration
    ) {
        // Create neighborhood
        if (NeighborhoodType.GEOMETRY.name().equalsIgnoreCase(neighborType)) {
            return runGeometry(badGreedy, maxIteration);
        } else if (NeighborhoodType.PERMUTATION.name().equalsIgnoreCase(neighborType)) {
            return runPermutation(maxIteration);
        }
        else if (NeighborhoodType.OVERLAP.name().equalsIgnoreCase(neighborType)) {
            return runOverlap(maxIteration);
        } else {
            throw new IllegalArgumentException("Unknown neighborhood type: " + neighborType);
        }
    }

    private PackingSolution runGeometry(PackingSolution badSolution, int maxIteration) {
        PackingStrategy randomPacking = new RandomPacking();
        GreedyStrategy<PackingSolution, Rectangle> extender = new FirstFitStrategy(randomPacking);
        GreedyOrdering<Rectangle> ordering = new AreaDescOrder();
        Greedy<PackingSolution, Rectangle> greedySolver = new Greedy<>(ordering, extender);

        double start = System.nanoTime();
        // init solution for geometry is rectangles placed randomly in different boxes
        // but they don't overlap or overflow
        List<Rectangle> copyRects = new ArrayList<>();
        for (Rectangle rect : this.rectangles) {
            copyRects.add(rect.copy());
        }
        Controller.randomShuffle(copyRects);
        PackingSolution initialSolution = new PackingSolution(badSolution.boxes().getFirst().getLength());
        PackingSolution greedySolution = greedySolver.solve(initialSolution, copyRects);
        System.out.println("bad solution init time: " + (System.nanoTime() - start)/1_000_000.0  + " ms");
        System.out.println("init solution with " + initialSolution.boxes().size() + " boxes");

        Neighborhood<PackingSolution> neighborhood = new Geometry();
        Objective<PackingSolution> objective = new MinimizeUsedArea();

        LocalSearch<PackingSolution> localSearch =
                new LocalSearch<>(
                        neighborhood,
                        objective,
                        maxIteration
                );

        return localSearch.solve(greedySolution);
    }

    private PackingSolution runPermutation(int maxIteration) {
        Neighborhood<PermutationSolution> neighborhood = new Permutation(this.configBoxLength);
        Objective<PermutationSolution> objective = new PermutationObjective();

        LocalSearch<PermutationSolution> localSearch =
                new LocalSearch<>(
                        neighborhood,
                        objective,
                        maxIteration
                );
        double start = System.nanoTime();
        List<Rectangle> rectangles = new ArrayList<>();
        for (Rectangle rectangle : this.rectangles) {
            rectangles.add(rectangle.copy());
        }
        randomShuffle(rectangles);
        PermutationSolution initPermutationSolution = new PermutationSolution(rectangles, this.configBoxLength);
        System.out.println("bad solution init time: " + (System.nanoTime() - start)/1_000_000.0  + " ms");

        PermutationSolution permutationSolution = localSearch.solve(initPermutationSolution);
        return permutationSolution.decode();
    }

    private PackingSolution runOverlap(int maxIteration) {
        List<Rectangle> copyRects = new ArrayList<>();
        for (Rectangle rect : this.rectangles) {
            Rectangle copyRect = rect.copy();
            copyRects.add(copyRect);
        }
        randomShuffle(copyRects);
        double start = System.nanoTime();
        OverlapPackingSolution badOverlap = this.initOverlapSolution(copyRects);
        OverlapPackingSolution initial = OverlapPackingSolution.init(badOverlap.boxes(), maxIteration);
        System.out.println("bad solution init time: " + (System.nanoTime() - start)/1_000_000.0 + " ms");
        System.out.println("bad solution init boxes: " + initial.boxes().size() + " boxes");

        Neighborhood<OverlapPackingSolution> neighborhood = new Overlap();
        Objective<OverlapPackingSolution> objective = new OverlapObjective();
        LocalSearch<OverlapPackingSolution> localSearchSolver = new LocalSearch<>(neighborhood, objective, maxIteration);

        return localSearchSolver.solve(initial);
    }

    public PackingSolution initBadGreedySolution() {
        // Create a simple bad solution by placing each rectangle in a new box
        // This is fast but very inefficient
        PackingSolution solution = new PackingSolution(this.configBoxLength);

        for (Rectangle rect : this.rectangles) {
            Rectangle copy = rect.copy();

            Box newBox = new Box(copy.getId(), this.configBoxLength);
            copy.setPosition(0, 0); // Place at origin
            newBox.addRectangle(copy, 0, 0);
            solution.addBox(newBox);
        }

        return solution;
    }

    public OverlapPackingSolution initOverlapSolution(List<Rectangle> rects) {
        long startInit = System.nanoTime();

        OverlapPackingSolution solution = new OverlapPackingSolution(this.configBoxLength);
        solution.boxes().clear();

        Random rand = new Random();

        int sumArea = 0;
        for (Rectangle rect : rects) {
            sumArea += rect.getArea();
        }
        int boxArea = this.configBoxLength * this.configBoxLength;
        int minNumBox = (int) Math.ceil((double) sumArea / boxArea);

        List<Box> boxes = new ArrayList<>();
        for (int i = 0; i < Math.max(1, minNumBox); i++) {
            Box box = new Box(i, this.configBoxLength);
            boxes.add(box);
        }

        for (Rectangle rect : rects) {
            Rectangle rectCopy = rect.copy();

            int boxId = rand.nextInt(boxes.size());
            Box selectedBox = boxes.get(boxId);

            int maxX = this.configBoxLength - rectCopy.getWidth();
            int maxY = this.configBoxLength - rectCopy.getHeight();

            int x = (maxX > 0) ? rand.nextInt(maxX + 1) : 0;
            int y = (maxY > 0) ? rand.nextInt(maxY + 1) : 0;

            selectedBox.addRectangle(rectCopy, x, y);
        }

        for (Box box : boxes) {
            if (!box.getRectangles().isEmpty()) {
                solution.addBox(box);
            }
        }

        if (solution.boxes().isEmpty()) {
            Box emptyBox = new Box(0, this.configBoxLength);
            solution.addBox(emptyBox);
        }

        long initTimeNanos = System.nanoTime() - startInit;
        double initTimeMs = initTimeNanos / 1_000_000.0;

        System.out.println("Initial overlap solution created:");
        System.out.println("- Total boxes: " + solution.boxes().size());
        System.out.println("- Total rectangles: " + rects.size());
        System.out.println("- Box size: " + this.configBoxLength);
        System.out.println("- Initialization time: " + String.format("%.2f ms", initTimeMs));

        return solution;
    }

    public static <I> void randomShuffle(List<I> items) {
        if (items == null) {
            throw new IllegalArgumentException("List cannot be null");
        }
        Collections.shuffle(items);
    }
}
