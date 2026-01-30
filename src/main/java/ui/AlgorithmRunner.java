package ui;

import controller.TestFramework;
import javafx.application.Platform;
import model.binpacking.Box;

import java.util.List;
import java.util.function.Consumer;

public class AlgorithmRunner {

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

    public static void runAlgorithm(AlgorithmConfig config, Consumer<AlgorithmResult> onComplete) {
        new Thread(() -> {
            TestFramework tf = new TestFramework(
                    config.rectangleCount,
                    config.minWidth,
                    config.maxWidth,
                    config.minHeight,
                    config.maxHeight,
                    config.boxLength
            );
            tf.generateInstances();

            if ("Greedy".equals(config.algorithm)) {
                String strategy = config.selectionStrategy != null 
                        ? config.selectionStrategy 
                        : "Area-based";
                tf.runGreedy(strategy);
            } else if ("Local Search".equals(config.algorithm)) {
                String neigh = config.neighborhood != null 
                        ? config.neighborhood 
                        : "Geometry-based";
                String select = config.selectionStrategy != null 
                        ? config.selectionStrategy 
                        : "Area-based";
                // tf.runLocalSearch(neigh, select);
            }

            AlgorithmResult result = new AlgorithmResult();
            result.boxes = BoxVisualizer.selectBoxesToDisplay(tf.getSolution().getItems());
            result.runtime = tf.getSolution().getFormattedRunTime();
            result.totalBoxes = tf.getSolution().getItems().size();
            result.totalRectangles = tf.getSolution().getItems().stream()
                    .mapToInt(b -> b.getRectangles().size())
                    .sum();

            Platform.runLater(() -> onComplete.accept(result));
        }).start();
    }
}
