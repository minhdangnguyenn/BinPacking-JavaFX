package algorithm;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.raw.GreedyOrderingType;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.ordering.raw.SideDescOrder;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.LocalSearchAlgorithm;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.neighborhood.raw.Geometry;
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

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class AlgorithmRunner {
    private ArrayList<Rectangle> rectangles;
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
        public int numBadBoxes;
    }

    public ArrayList<Rectangle> generateTestInstances(AlgorithmConfig config) {
        Utils.validConfig(config);

        this.boxLength = config.boxLength;

        this.rectangles = new ArrayList<>();
        for (int i = 0; i < config.rectangleCount; i++) {
            int width = (int) (Math.random() * (config.maxWidth - config.minWidth + 1)) + config.minWidth;
            int height = (int) (Math.random() * (config.maxHeight - config.minHeight + 1)) + config.minHeight;
            
            Rectangle rect = new Rectangle(i, width, height);
            this.rectangles.add(rect);
        }

        System.out.println("Generated " + this.rectangles.size() + " rectangles");

        this.greedySolution = null;
        this.localSearchSolution = null;

        return this.rectangles;
    }

    public void runAlgorithm(
            AlgorithmConfig config,
            Consumer<AlgorithmResult> onComplete,
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
                result.initRuntime = "N/A"; // No init for pure greedy
                result.totalGreedyBoxes = this.greedySolution.boxes().size();
                result.totalLocalSearchBoxes = 0;
            }
            else if (AlgorithmType.LOCALSEARCH.name().equals(config.algorithm)) {
                PackingSolution badGreedy = initBadGreedySolution();

                String neighborType = config.neighborhood != null
                        ? config.neighborhood
                        : NeighborhoodType.GEOMETRY.name();

                this.localSearchSolution = runLocalSearch(neighborType, badGreedy, maxIteration);
                
                // For local search, totalGreedyBoxes shows the bad initial solution count
                result.totalGreedyBoxes = badGreedy.boxes().size();
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

        return greedyAlgorithm.solve(initialSolution, rectangles);
    }

    private PackingSolution runLocalSearch(
            String neighborType,
            PackingSolution badGreedy,
            int maxIteration
    ) {
        // Create neighborhood
        if (NeighborhoodType.GEOMETRY.name().equalsIgnoreCase(neighborType)) {
            return runGeometry(badGreedy, maxIteration);
        } else if (NeighborhoodType.RULEBASED.name().equalsIgnoreCase(neighborType)) {
            return runPermutation(maxIteration);
        }
        else if (NeighborhoodType.OVERLAP.name().equalsIgnoreCase(neighborType)) {
            return runOverlap(maxIteration);
        } else {
            throw new IllegalArgumentException("Unknown neighborhood type: " + neighborType);
        }
    }

    public PackingSolution runGeometry(PackingSolution badSolution, int maxIteration) {
        PackingStrategy putting = new BottomLeft();
        GreedyStrategy<PackingSolution, Rectangle> extender = new FirstFitStrategy(putting);
        GreedyOrdering<Rectangle> ordering = new AreaDescOrder();
        GreedyAlgorithm<PackingSolution, Rectangle> greedySolver = new GreedyAlgorithm<>(ordering, extender);

        PackingSolution initialSolution = new PackingSolution(badSolution.boxes().getFirst().getLength());
        PackingSolution greedySolution = greedySolver.solve(initialSolution, this.rectangles);

        Neighborhood<PackingSolution> neighborhood = new Geometry();
        Objective<PackingSolution> objective = new MinimizeUsedArea();

        LocalSearchAlgorithm<PackingSolution> localSearch =
                new LocalSearchAlgorithm<>(
                        neighborhood,
                        objective,
                        maxIteration
                );

        PackingSolution solution = localSearch.solve(greedySolution);

        System.out.println("Local search solution: " + solution.boxes().size() + " boxes");
        System.out.println("Improvement: " + (greedySolution.boxes().size() - solution.boxes().size()) + " boxes saved compared to initial solution");

        return solution;
    }

    private PackingSolution runPermutation(int maxIteration) {
        // Create neighborhood and objective
        Neighborhood<PermutationSolution> neighborhood = new Permutation(this.boxLength);
        Objective<PermutationSolution> objective = new PermutationObjective();

        LocalSearchAlgorithm<PermutationSolution> localSearch =
                new LocalSearchAlgorithm<>(
                        neighborhood,
                        objective,
                        maxIteration
                );

        PermutationSolution initPermutationSolution = new PermutationSolution(this.rectangles, this.boxLength);

        long start = System.nanoTime();
        PermutationSolution permutationSolution = localSearch.solve(initPermutationSolution);
        long end = System.nanoTime();
        long runtime = (end - start) / 1_000_000; // Convert to milliseconds

        PackingSolution solution = permutationSolution.decode();

        return solution;
    }

    private PackingSolution runOverlap(int maxIteration) {
        List<Rectangle> copyRects = new ArrayList<>();
        for (Rectangle rect : this.rectangles) {
            copyRects.add(rect.copy());
        }
        // OverlapPackingSolution badOverlap  = initOverlapSolution(this.rectangles);
        OverlapPackingSolution badOverlap = this.initOverlapSolution(copyRects);
        OverlapPackingSolution initial = OverlapPackingSolution.init(badOverlap.boxes(), maxIteration);
        System.out.println("Initial Box Used: " + initial.boxes().size());
        Neighborhood<OverlapPackingSolution> neighborhood = new Overlap();
        Objective<OverlapPackingSolution> objective = new OverlapObjective();
        LocalSearchAlgorithm<OverlapPackingSolution> localSearchSolver = new LocalSearchAlgorithm<>(neighborhood, objective, maxIteration);
        Date startTime = new Date();
        OverlapPackingSolution solution = localSearchSolver.solve(initial);
        Date endTime = new Date();

        return solution;
    }

    public PackingSolution initBadGreedySolution() {

        // Create a simple bad solution by placing each rectangle in a new box
        // This is fast but very inefficient
        PackingSolution solution = new PackingSolution(this.boxLength);

        for (Rectangle rect : this.rectangles) {
            Box newBox = new Box(rect.getId(), this.boxLength);
            rect.setPosition(0, 0); // Place at origin
            newBox.addRectangle(rect, 0, 0);
            solution.addBox(newBox);
        }

        return solution;
    }

    public OverlapPackingSolution initOverlapSolution(List<Rectangle> rects) {
        long startInit = System.nanoTime();

        OverlapPackingSolution solution = new OverlapPackingSolution(this.boxLength);
        solution.boxes().clear();

        Random rand = new Random();

        int sumArea = 0;
        for (Rectangle rect : rects) {
            sumArea += rect.getArea();
        }
        int boxArea = this.boxLength * this.boxLength;
        int minNumBox = (int) Math.ceil((double) sumArea / boxArea);

        List<Box> boxes = new ArrayList<>();
        for (int i = 0; i < Math.max(1, minNumBox); i++) {
            Box box = new Box(i, this.boxLength);
            boxes.add(box);
        }

        for (Rectangle rect : rects) {
            Rectangle rectCopy = rect.copy();

            int boxId = rand.nextInt(boxes.size());
            Box selectedBox = boxes.get(boxId);

            int maxX = this.boxLength - rectCopy.getWidth();
            int maxY = this.boxLength - rectCopy.getHeight();

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
            Box emptyBox = new Box(0, this.boxLength);
            solution.addBox(emptyBox);
        }

        long initTimeNanos = System.nanoTime() - startInit;
        double initTimeMs = initTimeNanos / 1_000_000.0;

        System.out.println("Initial overlap solution created:");
        System.out.println("- Total boxes: " + solution.boxes().size());
        System.out.println("- Total rectangles: " + rects.size());
        System.out.println("- Box size: " + this.boxLength);
        System.out.println("- Initialization time: " + String.format("%.2f ms", initTimeMs));

        return solution;
    }
}
