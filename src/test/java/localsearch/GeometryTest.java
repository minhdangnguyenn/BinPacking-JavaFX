package localsearch;

import algorithm.core.greedy.Greedy;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.LocalSearch;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.neighborhood.raw.Geometry;
import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.core.localsearch.objective.raw.MinimizeUsedArea;
import algorithm.model.Rectangle;
import algorithm.solution.generic.Solution;
import algorithm.solution.raw.PackingSolution;
import environment.Instance;
import environment.TestEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import environment.utils.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeometryTest {
    private List<Instance> easyInstances = new ArrayList<>();
    private List<Instance> mediumInstances = new ArrayList<>();
    private List<Instance> hardInstances = new ArrayList<>();

    private Greedy<PackingSolution, Rectangle> greedySolver;
    private static LocalSearch<PackingSolution> localSearch;
    private TestEnvironment env = new TestEnvironment();

    @BeforeEach
    void setUp() {
        easyInstances = env.easyInstances(5);
        mediumInstances = env.getMediumInstances(10);

        // Initialize the Greedy Solver with Shelf Putting Strategy
        PackingStrategy bottomLeft = new BottomLeft();
        GreedyOrdering<Rectangle> ordering = new AreaDescOrder();
        GreedyStrategy<PackingSolution, Rectangle> extender =
                new FirstFitStrategy(bottomLeft);

        greedySolver = new Greedy<>(ordering, extender);

        // Initialize the Local Search Solver
        Neighborhood<PackingSolution> neighborhood = new Geometry();
        Objective<PackingSolution> objective = new MinimizeUsedArea();
        localSearch = new LocalSearch<>(neighborhood, objective, 1000);
    }

    @Test
    void easy() {

        List<Solution> solutions = new ArrayList<>();
        List<Long> durations = new ArrayList<>();

        // Solve easy instances and check solutions
        for (Instance instance : easyInstances) {

            PackingSolution initial = new PackingSolution(instance.boxSize());
            Date startTime = new Date();
            PackingSolution greedySolution = greedySolver.solve(initial, instance.rectangles());
            PackingSolution solution = localSearch.solve(greedySolution);
            Date endTime = new Date();
            long duration = endTime.getTime() - startTime.getTime();

            // Store solution and duration
            solutions.add(solution);
            durations.add(duration);

            // Basic assertions
            assertNotNull(solution);
            assertFalse(solution.boxes().isEmpty());
            // Check that there are no overlapping rectangles in each box
            for (var box : solution.boxes()) {
                assertEquals(0.0, box.totalOverlapRate());

                // Check that no rectangle overflows the box
                for (Rectangle rectangle: box.getRectangles()) {
                    assertFalse(box.isOverflow(rectangle));
                }
            }
        }

        Path path = Paths.get(
                "target",
                "csv",
                "localsearch",
                "Geometry_EasyResults.csv"
        );

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{ "Instance", "NumBoxes", "Duration(ms)" });
        for (int i = 0; i < solutions.size(); i++) {
            PackingSolution sol = (PackingSolution) solutions.get(i);
            String[] data = {
                    String.valueOf(i + 1),
                    String.valueOf(sol.boxes().size()),
                    String.valueOf(durations.get(i))
            };
            csvData.add(data);
        }
        Utils.writeResult(csvData, path);
    }

    @Test
    void medium() {

        List<Solution> solutions = new ArrayList<>();
        List<Long> durations = new ArrayList<>();

        // Solve medium instances and check solutions
        for (Instance instance : mediumInstances) {

            PackingSolution initial = new PackingSolution(instance.boxSize());
            Date startTime = new Date();
            PackingSolution greedySolution = greedySolver.solve(initial, instance.rectangles());
            PackingSolution solution = localSearch.solve(greedySolution);
            Date endTime = new Date();
            long duration = endTime.getTime() - startTime.getTime();

            // Store solution and duration
            solutions.add(solution);
            durations.add(duration);

            // Basic assertions
            assertNotNull(solution);
            assertFalse(solution.boxes().isEmpty());
            // Check that there are no overlapping rectangles in each box
            for (var box : solution.boxes()) {
                assertEquals(0.0, box.totalOverlapRate());

                // Check that no rectangle overflows the box
                for (Rectangle rectangle: box.getRectangles()) {
                    assertFalse(box.isOverflow(rectangle));
                }
            }
        }

        Path path = Paths.get(
                "target",
                "csv",
                "localsearch",
                "Geometry_MediumResults.csv"
        );

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{ "Instance", "NumBoxes", "Duration(ms)" });
        for (int i = 0; i < solutions.size(); i++) {
            PackingSolution sol = (PackingSolution) solutions.get(i);
            String[] data = {
                    String.valueOf(i + 1),
                    String.valueOf(sol.boxes().size()),
                    String.valueOf(durations.get(i))
            };
            csvData.add(data);
        }
        Utils.writeResult(csvData, path);
    }

    @Test
    void hard() {
        List<Solution> solutions = new ArrayList<>();
        List<Long> durations = new ArrayList<>();

        // Solve hard instances and check solutions
        for (Instance instance : hardInstances) {

            PackingSolution initial = new PackingSolution(instance.boxSize());
            Date startTime = new Date();
            PackingSolution greedySolution = greedySolver.solve(initial, instance.rectangles());
            PackingSolution solution = localSearch.solve(greedySolution);
            Date endTime = new Date();
            long duration = endTime.getTime() - startTime.getTime();

            // Store solution and duration
            solutions.add(solution);
            durations.add(duration);

            // Basic assertions
            assertNotNull(solution);
            assertFalse(solution.boxes().isEmpty());
            // Check that there are no overlapping rectangles in each box
            for (var box : solution.boxes()) {
                assertEquals(0.0, box.totalOverlapRate());

                // Check that no rectangle overflows the box
                for (Rectangle rectangle: box.getRectangles()) {
                    assertFalse(box.isOverflow(rectangle));
                }
            }
        }

        Path path = Paths.get(
                "target",
                "csv",
                "localsearch",
                "Geometry_HardResults.csv"
        );

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{ "Instance", "NumBoxes", "Duration(ms)" });
        for (int i = 0; i < solutions.size(); i++) {
            PackingSolution sol = (PackingSolution) solutions.get(i);
            String[] data = {
                    String.valueOf(i + 1),
                    String.valueOf(sol.boxes().size()),
                    String.valueOf(durations.get(i))
            };
            csvData.add(data);
        }
        Utils.writeResult(csvData, path);
    }
}
