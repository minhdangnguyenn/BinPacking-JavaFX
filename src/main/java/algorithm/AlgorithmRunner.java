package algorithm;

import algorithm.core.greedy.ordering.raw.GreedyOrderingType;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.packing.raw.RandomPacking;
import algorithm.core.localsearch.neighborhood.raw.NeighborhoodType;
import app.TestFramework;
import javafx.application.Platform;
import algorithm.model.Rectangle;
import algorithm.model.Box;
import ui.BoxVisualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AlgorithmRunner {
    public TestFramework tf;
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
    }

    public ArrayList<Rectangle> generateTestInstances(AlgorithmConfig config) {
        this.tf = new TestFramework(
                config.rectangleCount,
                config.minWidth,
                config.maxWidth,
                config.minHeight,
                config.maxHeight,
                config.boxLength
        );

        tf.generateInstances();

        return tf.getInstances();
    }

    public void runAlgorithm(AlgorithmConfig config, Consumer<AlgorithmResult> onComplete) {
        new Thread(() -> {
            if (this.tf == null) {
                System.out.println("You need to generate instances first");
                return;
            }
            
            long start = System.nanoTime();
            
            if (AlgorithmType.GREEDY.name().equals(config.algorithm)) {
                PackingStrategy bottomLeft = new BottomLeft();
                String strategy = config.selectionStrategy != null 
                        ? config.selectionStrategy 
                        : GreedyOrderingType.LARGEST_AREA_FIRST.name();
                this.tf.runGreedy(strategy, bottomLeft);
            }
            else if (AlgorithmType.LOCALSEARCH.name().equals(config.algorithm)) {
                String strategy = config.selectionStrategy != null
                        ? config.selectionStrategy
                        : GreedyOrderingType.LARGEST_AREA_FIRST.name();
                PackingStrategy bottomLeft = new BottomLeft();
                PackingStrategy randomPacker = new RandomPacking();
                this.tf.runGreedy(strategy, randomPacker);
                String neigh = config.neighborhood != null
                        ? config.neighborhood
                        : NeighborhoodType.GEOMETRY.name();
                this.tf.runLocalSearch(neigh);
            }

            this.runtimeMs = (System.nanoTime() - start) / 1_000_000;

            AlgorithmResult result = new AlgorithmResult();
            result.boxes = BoxVisualizer.selectBoxesToDisplay(this.tf.getGreedySolution().boxes());
            result.runtime = String.format("%.2f ms", (double) this.runtimeMs);
            result.totalGreedyBoxes = this.tf.getGreedySolution().boxes().size();
            result.totalLocalSearchBoxes =
                    this.tf.getLocalSearchSolution() == null
                            ? 0
                            : this.tf.getLocalSearchSolution().boxes().size();
            result.totalRectangles = this.tf.getGreedySolution().boxes().stream()
                    .mapToInt(b -> b.getRectangles().size())
                    .sum();

            Platform.runLater(() -> onComplete.accept(result));
        }).start();
    }
}
