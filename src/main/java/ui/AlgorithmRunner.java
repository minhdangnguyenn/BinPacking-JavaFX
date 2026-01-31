package ui;

import app.binpacking.TestFramework;
import javafx.application.Platform;
import model.binpacking.instances.BinRectangle;
import model.binpacking.instances.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AlgorithmRunner {
    public TestFramework tf;
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

    public ArrayList<BinRectangle> generateTestInstances(AlgorithmConfig config) {
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
            if ("Greedy".equals(config.algorithm)) {
                String strategy = config.selectionStrategy != null 
                        ? config.selectionStrategy 
                        : "Area-based";
                this.tf.runGreedy(strategy);
            } else if ("Local Search".equals(config.algorithm)) {
                String strategy = config.selectionStrategy != null
                        ? config.selectionStrategy
                        : "Area-based";
                this.tf.runGreedy(strategy);
                String neigh = config.neighborhood != null 
                        ? config.neighborhood 
                        : "Geometry-based";
                this.tf.runLocalSearch(neigh);
            }

            AlgorithmResult result = new AlgorithmResult();
            result.boxes = BoxVisualizer.selectBoxesToDisplay(this.tf.getSolution().getItems());
            result.runtime = this.tf.getSolution().getFormattedRunTime();
            result.totalBoxes = this.tf.getSolution().getItems().size();
            result.totalRectangles = this.tf.getSolution().getItems().stream()
                    .mapToInt(b -> b.getRectangles().size())
                    .sum();

            Platform.runLater(() -> onComplete.accept(result));
        }).start();
    }
}
