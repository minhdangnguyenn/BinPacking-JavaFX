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

    /**
     * Spec-required: generates instances from a parameter tuple.
     * Tuple: (numInstances, numRects, minWidth, minHeight, maxWidth, maxHeight, boxLength)
     */
    public List<Instance> generateInstances(
            int numInstances,
            int numRects,
            int minWidth,
            int minHeight,
            int maxWidth,
            int maxHeight,
            int boxLength
    ) {
        Controller.Config cfg = new Controller.Config();
        cfg.rectangleCount  = numRects;
        cfg.minWidth        = minWidth;
        cfg.maxWidth        = maxWidth;
        cfg.minHeight       = minHeight;
        cfg.maxHeight       = maxHeight;
        cfg.boxLength       = boxLength;
        // required by validConfig — keep consistent with existing defaults
        cfg.algorithm          = AlgorithmType.GREEDY.name();
        cfg.neighborhood       = NeighborhoodType.GEOMETRY.name();
        cfg.selectionStrategy  = GreedyStrategyType.FIRST_FIT.name();
        cfg.maxIteration       = 100;

        Utils.validConfig(cfg);

        List<Instance> instances = new ArrayList<>();
        for (int i = 0; i < numInstances; i++) {
            List<Rectangle> rectangles = new ArrayList<>();
            for (int r = 0; r < numRects; r++) {
                int w = random.nextInt(maxWidth  - minWidth  + 1) + minWidth;
                int h = random.nextInt(maxHeight - minHeight + 1) + minHeight;
                rectangles.add(new Rectangle(r, w, h));
            }
            instances.add(new Instance(boxLength, rectangles));
        }
        return instances;
    }

}
