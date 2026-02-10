package greedy;

import algorithm.core.greedy.Greedy;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.SideDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.model.Rectangle;
import algorithm.solution.raw.PackingSolution;
import environment.Instance;
import environment.TestEnvironment;
import environment.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FirstFitBottomLeftSideDescTest {
    private List<Instance> easyInstances;
    private static Greedy<PackingSolution, Rectangle> greedy;

    @BeforeEach
    void setup() {
        TestEnvironment env = new TestEnvironment();
        easyInstances = env.easyInstances(5);
        PackingStrategy bottomLeft = new BottomLeft();

        GreedyStrategy<PackingSolution, Rectangle> greedySelection =
                new FirstFitStrategy(bottomLeft);

        GreedyOrdering<Rectangle> sideDescOrder = new SideDescOrder();

        greedy = new Greedy<>(sideDescOrder, greedySelection);
    }

    @Test
    void easy() {

        List<PackingSolution> solutions = new ArrayList<>();
        List<Long> durations = new ArrayList<>();

        for (Instance instance : easyInstances) {

            PackingSolution initial = new PackingSolution(instance.boxSize());

            long start = System.currentTimeMillis();
            PackingSolution greedySolution =
                    greedy.solve(initial, instance.rectangles());

            long duration = System.currentTimeMillis() - start;

            solutions.add(greedySolution);
            durations.add(duration);

            // Assertions
            assertNotNull(greedySolution);
            assertFalse(greedySolution.boxes().isEmpty());

            for (var box : greedySolution.boxes()) {
                assertEquals(0.0, box.totalOverlapRate());

                for (Rectangle rectangle : box.getRectangles()) {
                    assertFalse(box.isOverflow(rectangle));
                }
            }
        }

        Path path = Paths.get(
                "target", "csv", "greedy", "FFBL_SideDESC_EasyResults.csv"
        );

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{"Instance", "NumBoxes", "NumRectangles", "Duration(ms)"});

        for (int i = 0; i < solutions.size(); i++) {
            PackingSolution sol = solutions.get(i);
            csvData.add(new String[]{
                    String.valueOf(i + 1),
                    String.valueOf(sol.boxes().size()),
                    String.valueOf(sol.getRectangles().size()),
                    String.valueOf(durations.get(i))
            });
        }

        Utils.WriteResult(csvData, path);
    }
}
