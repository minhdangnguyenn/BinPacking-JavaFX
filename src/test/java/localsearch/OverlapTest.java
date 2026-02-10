package localsearch;

import algorithm.core.localsearch.LocalSearch;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.core.localsearch.neighborhood.raw.Overlap;
import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.core.localsearch.objective.raw.OverlapObjective;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;
import algorithm.solution.raw.PackingSolution;
import environment.Instance;
import environment.TestEnvironment;
import environment.utils.Utils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OverlapTest {
    private static List<Instance> easyInstances = new ArrayList<>();
    private static List<Instance> mediumInstances = new ArrayList<>();
    private static List<Instance> hardInstances = new ArrayList<>();

    private static LocalSearch<OverlapPackingSolution> localSearchSolver;
    static TestEnvironment env = new TestEnvironment();

    @BeforeAll
    static void setUp() {
        easyInstances = env.easyInstances(5);
        mediumInstances = env.getMediumInstances(10);
        // hardInstances = env.getHardInstances(5);

        // Initialize the Local Search Solver
        Neighborhood<OverlapPackingSolution> neighborhood = new Overlap();
        Objective<OverlapPackingSolution> objective = new OverlapObjective();
        localSearchSolver = new LocalSearch<>(neighborhood, objective, 100);
    }

    @Test
    void easy() {

        List<PackingSolution> solutions = new ArrayList<>();
        List<Long> durations = new ArrayList<>();

        // Solve easy instances and check solutions
        for (Instance instance : easyInstances) {

            List<Box> testBoxes = Utils.createOverlapBoxes(instance.boxSize(), instance.rectangles());

            OverlapPackingSolution initial = OverlapPackingSolution.init(testBoxes, 1000);
            initial.maxIterations = 100;
            initial.currentIteration = 0;

            Date startTime = new Date();
            OverlapPackingSolution solution = localSearchSolver.solve(initial);
            Date endTime = new Date();
            long duration = endTime.getTime() - startTime.getTime();

            // Store solution and duration
            solutions.add(solution);
            durations.add(duration);

            // Basic assertions
            assertNotNull(solution);
            assertFalse(solution.boxes().isEmpty());
            
            // Check that there are no overlapping rectangles in each box
            for (Box box : solution.boxes()) {
                assertEquals(
                        0.0,
                        box.totalOverlapRate(),
                        "Box " + box.getId() + " should have no overlaps"
                );

                // Check that no rectangle overflows the box
                for (Rectangle rectangle: box.getRectangles()) {
                    assertFalse(
                            box.isOverflow(rectangle),
                            "Rectangle " + rectangle.getId() + " should not overflow"
                    );
                }
            }
        }

        Path path = Paths.get(
                "target",
                "csv",
                "localsearch",
                "Overlap_EasyResults.csv"
        );

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{ "Instance", "NumBoxes", "Duration(ms)" });
        for (int i = 0; i < solutions.size(); i++) {
            PackingSolution sol = solutions.get(i);
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

        List<PackingSolution> solutions = new ArrayList<>();
        List<Long> durations = new ArrayList<>();

        // Solve easy instances and check solutions
        for (Instance instance : mediumInstances) {
            List<Box> testBoxes = Utils.createOverlapBoxes(instance.boxSize(), instance.rectangles());

            OverlapPackingSolution initial = OverlapPackingSolution.init(testBoxes, 1000);
            initial.maxIterations = 100;
            initial.currentIteration = 0;

            Date startTime = new Date();
            OverlapPackingSolution solution = localSearchSolver.solve(initial);
            Date endTime = new Date();
            long duration = endTime.getTime() - startTime.getTime();

            // Store solution and duration
            solutions.add(solution);
            durations.add(duration);

            // Basic assertions
            assertNotNull(solution);
            assertFalse(solution.boxes().isEmpty());

            // Check that there are no overlapping rectangles in each box
            for (Box box : solution.boxes()) {
                assertEquals(
                        0.0,
                        box.totalOverlapRate(),
                        "Box " + box.getId() + " should have no overlaps"
                );

                // Check that no rectangle overflows the box
                for (Rectangle rectangle: box.getRectangles()) {
                    assertFalse(
                            box.isOverflow(rectangle),
                            "Rectangle " + rectangle.getId() + " should not overflow"
                    );
                }
            }
        }

        Path path = Paths.get(
                "target",
                "csv",
                "localsearch",
                "Overlap_MediumResults.csv"
        );

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{ "Instance", "NumBoxes", "Duration(ms)" });
        for (int i = 0; i < solutions.size(); i++) {
            PackingSolution sol = solutions.get(i);
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
