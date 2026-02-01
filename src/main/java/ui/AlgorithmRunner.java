package ui;

import algorithm.greedy.ordering.GreedyOrderingType;
import app.TestFramework;
import javafx.application.Platform;
import algorithm.instances.Rectangle;
import algorithm.instances.Box;

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
        public int totalBoxes;
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

        // create test instances for this tf instance
        // saved in rectangles attribute of this instance
        tf.generateInstances();

        return tf.getRectangles();
    }

    public void runAlgorithm(AlgorithmConfig config, Consumer<AlgorithmResult> onComplete) {
        new Thread(() -> {
            if (this.tf == null) {
                System.out.println("You need to generate instances first");
                return;
            }
            
            long start = System.nanoTime();
            
            if ("Greedy".equals(config.algorithm)) {
                String strategy = config.selectionStrategy != null 
                        ? config.selectionStrategy 
                        : GreedyOrderingType.LARGEST_AREA_FIRST.name();
                this.tf.runGreedy(strategy);
            }
//            else if ("Local Search".equals(config.algorithm)) {
//                String strategy = config.selectionStrategy != null
//                        ? config.selectionStrategy
//                        : "Area-based";
//                this.tf.runGreedy(strategy);
//                String neigh = config.neighborhood != null
//                        ? config.neighborhood
//                        : "Geometry-based";
//                this.tf.runLocalSearch(neigh);
//            }

            this.runtimeMs = (System.nanoTime() - start) / 1_000_000;

            AlgorithmResult result = new AlgorithmResult();
            result.boxes = BoxVisualizer.selectBoxesToDisplay(this.tf.getPackingSolution().boxes());
            result.runtime = String.format("%.2f ms", (double) this.runtimeMs);
            result.totalBoxes = this.tf.getPackingSolution().boxes().size();
            result.totalRectangles = this.tf.getPackingSolution().boxes().stream()
                    .mapToInt(b -> b.getRectangles().size())
                    .sum();

            Platform.runLater(() -> onComplete.accept(result));
        }).start();
    }
}
