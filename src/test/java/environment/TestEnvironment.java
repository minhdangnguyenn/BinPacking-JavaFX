package environment;

import algorithm.AlgorithmType;
import algorithm.Controller;
import algorithm.core.greedy.strategy.raw.GreedyStrategyType;
import algorithm.core.localsearch.neighborhood.raw.NeighborhoodType;
import algorithm.model.Rectangle;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestEnvironment {
    private final Controller.Config config;
    private final List<Rectangle> testRectangles = new ArrayList<>();
    private final Random random = new Random(42);

    public TestEnvironment() {
        this.config = new Controller.Config();
    }

    public Controller.Config getConfig() {
        return config;
    }

    public List<Rectangle> getRectangles() {
        return testRectangles;
    }

    public List<Instance> easyInstances(int numInstances) {
        List<Instance> instances = new ArrayList<>();

        Controller.Config config = new Controller.Config();
        config.rectangleCount = 1000;
        config.minWidth = 1;
        config.maxWidth = 100;
        config.minHeight = 1;
        config.maxHeight = 100;
        config.boxLength = 100;
        config.algorithm = AlgorithmType.GREEDY.name();
        config.neighborhood = NeighborhoodType.GEOMETRY.name();
        config.selectionStrategy = GreedyStrategyType.FIRST_FIT.name();
        config.maxIteration = 100;

        Utils.validConfig(config);

        for (int i = 0; i < numInstances; i++) {
            List<Rectangle> rectangles = new ArrayList<>();
            for (int r = 0; r < config.rectangleCount; r++) {
                int w = random.nextInt(config.maxWidth - config.minWidth + 1) + config.minWidth;
                int h = random.nextInt(config.maxHeight - config.minHeight + 1) + config.minHeight;
                rectangles.add(new Rectangle(r, w, h));
            }
            instances.add(new Instance(config.boxLength, rectangles));
        }

        return instances;
    }

    public List<Instance> getMediumInstances(int numInstances) {
        List<Instance> instances = new ArrayList<>();

        Controller.Config config = new Controller.Config();
        config.rectangleCount = 2000;
        config.minWidth = 1;
        config.maxWidth = 50;
        config.minHeight = 1;
        config.maxHeight = 100;
        config.boxLength = 150;
        config.algorithm = AlgorithmType.GREEDY.name();
        config.neighborhood = NeighborhoodType.GEOMETRY.name();
        config.selectionStrategy = GreedyStrategyType.FIRST_FIT.name();
        config.maxIteration = 100;

        Utils.validConfig(config);

        for (int i = 0; i < numInstances; i++) {
            List<Rectangle> rectangles = new ArrayList<>();
            for (int r = 0; r < config.rectangleCount; r++) {
                int w = random.nextInt(config.maxWidth - config.minWidth + 1) + config.minWidth;
                int h = random.nextInt(config.maxHeight - config.minHeight + 1) + config.minHeight;
                rectangles.add(new Rectangle(r, w, h));
            }
            instances.add(new Instance(config.boxLength, rectangles));
        }

        return instances;
    }

    public List<Instance> getHardInstances(int numInstances) {
        List<Instance> instances = new ArrayList<>();

        Controller.Config config = new Controller.Config();
        config.rectangleCount = 10_000;
        config.minWidth = 50;
        config.maxWidth = 100;
        config.minHeight = 1;
        config.maxHeight = 100;
        config.boxLength = 200;
        config.algorithm = AlgorithmType.GREEDY.name();
        config.neighborhood = NeighborhoodType.GEOMETRY.name();
        config.selectionStrategy = GreedyStrategyType.FIRST_FIT.name();
        config.maxIteration = 100;

        Utils.validConfig(config);

        for (int i = 0; i < numInstances; i++) {
            List<Rectangle> rectangles = new ArrayList<>();
            for (int r = 0; r < config.rectangleCount; r++) {
                int w = random.nextInt(config.maxWidth - config.minWidth + 1) + config.minWidth;
                int h = random.nextInt(config.maxHeight - config.minHeight + 1) + config.minHeight;
                rectangles.add(new Rectangle(r, w, h));
            }
            instances.add(new Instance(config.boxLength, rectangles));
        }

        return instances;
    }
}
