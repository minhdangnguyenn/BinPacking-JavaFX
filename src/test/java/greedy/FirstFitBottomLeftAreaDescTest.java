package greedy;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import algorithm.core.greedy.Greedy;
import algorithm.core.greedy.ordering.generic.OrderStrategy;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.strategy.generic.SelectStrategy;
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
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class FirstFitBottomLeftAreaDescTest {

    private List<Instance> easyInstances;
    private List<Instance> mediumInstances;
    private List<Instance> hardInstances;
    private static Greedy<PackingSolution, Rectangle> greedy;

    @BeforeEach
    void setup() {
        TestEnvironment env = new TestEnvironment();
        easyInstances = env.easyInstances(5);
        mediumInstances = env.getMediumInstances(10);
        hardInstances = env.getHardInstances(20);
        PackingStrategy bottomLeft = new BottomLeft();

        SelectStrategy<PackingSolution, Rectangle> greedySelection =
                new FirstFitStrategy(bottomLeft);

        OrderStrategy<Rectangle> areaDescOrder = new AreaDescOrder();

        greedy = new Greedy<>(areaDescOrder, greedySelection);
    }

    @Test
    void easy() {
        List<PackingSolution> solutions = new ArrayList<>();
        List<Long> durations = new ArrayList<>();
        List<Double> cpuLoads = new ArrayList<>(); // ← local
        int j = 0;

        for (Instance instance : easyInstances) {
            PackingSolution initial = new PackingSolution(instance.boxSize());

            long start = System.currentTimeMillis();
            PackingSolution greedySolution = greedy.solve(initial, instance.rectangles());
            long end = System.currentTimeMillis() - start;

            double cpuLoad = getCpuLoadPercent();
            System.out.printf("Instance %d → CPU Load: %.2f%%%n", j + 1, cpuLoad);
            j++;

            solutions.add(greedySolution);
            durations.add(end);
            cpuLoads.add(cpuLoad);

            assertNotNull(greedySolution);
            assertFalse(greedySolution.boxes().isEmpty());

            for (var box : greedySolution.boxes()) {
                assertEquals(0.0, box.totalOverlapRate());
                for (Rectangle rectangle : box.getRectangles()) {
                    assertFalse(box.isOverflow(rectangle));
                }
            }
        }

        Path path = Paths.get("target", "csv", "greedy", "FFBL_AreaDESC_EasyResults.csv");

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{"Instance", "NumRectangles", "NumBoxes", "Duration(ms)", "CPU(%)"});

        for (int i = 0; i < solutions.size(); i++) {
            PackingSolution sol = solutions.get(i);
            csvData.add(new String[]{
                    String.valueOf(i + 1),
                    String.valueOf(sol.getRectangles().size()),
                    String.valueOf(sol.boxes().size()),
                    String.valueOf(durations.get(i)),
                    String.format(Locale.US, "%.2f", cpuLoads.get(i))
            });
        }

        Utils.writeResult(csvData, path);
    }

    @Test
    void medium() {
        List<PackingSolution> solutions = new ArrayList<>();
        List<Long> durations = new ArrayList<>();
        List<Double> cpuLoads = new ArrayList<>(); // ← local
        int j = 0;

        for (Instance instance : mediumInstances) {
            PackingSolution initial = new PackingSolution(instance.boxSize());

            long start = System.currentTimeMillis();
            PackingSolution greedySolution = greedy.solve(initial, instance.rectangles());
            long end = System.currentTimeMillis() - start;

            double cpuLoad = getCpuLoadPercent();
            System.out.printf("Instance %d → CPU Load: %.2f%%%n", j + 1, cpuLoad);
            j++;

            solutions.add(greedySolution);
            durations.add(end);
            cpuLoads.add(cpuLoad);

            assertNotNull(greedySolution);
            assertFalse(greedySolution.boxes().isEmpty());

            for (var box : greedySolution.boxes()) {
                assertEquals(0.0, box.totalOverlapRate());
                for (Rectangle rectangle : box.getRectangles()) {
                    assertFalse(box.isOverflow(rectangle));
                }
            }
        }

        Path path = Paths.get("target", "csv", "greedy", "FFBL_AreaDESC_MedResults.csv");

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{"Instance", "NumRectangles", "NumBoxes", "Duration(ms)", "CPU(%)"});

        for (int i = 0; i < solutions.size(); i++) {
            PackingSolution sol = solutions.get(i);
            csvData.add(new String[]{
                    String.valueOf(i + 1),
                    String.valueOf(sol.getRectangles().size()),
                    String.valueOf(sol.boxes().size()),
                    String.valueOf(durations.get(i)),
                    String.format(Locale.US, "%.2f", cpuLoads.get(i))
            });
        }

        Utils.writeResult(csvData, path);
    }

    @Test
    void hard() {
        List<PackingSolution> solutions = new ArrayList<>();
        List<Long> durations = new ArrayList<>();
        List<Double> cpuLoads = new ArrayList<>(); // ← local
        int j = 0;

        for (Instance instance : hardInstances) {
            PackingSolution initial = new PackingSolution(instance.boxSize());

            long start = System.currentTimeMillis();
            PackingSolution greedySolution = greedy.solve(initial, instance.rectangles());
            long end = System.currentTimeMillis() - start;

            double cpuLoad = getCpuLoadPercent();
            System.out.printf("Instance %d → CPU Load: %.2f%%%n", j + 1, cpuLoad);
            j++;

            solutions.add(greedySolution);
            durations.add(end);
            cpuLoads.add(cpuLoad);

            assertNotNull(greedySolution);
            assertFalse(greedySolution.boxes().isEmpty());

            for (var box : greedySolution.boxes()) {
                assertEquals(0.0, box.totalOverlapRate());
                for (Rectangle rectangle : box.getRectangles()) {
                    assertFalse(box.isOverflow(rectangle));
                }
            }
        }

        Path path = Paths.get("target", "csv", "greedy", "FFBL_AreaDESC_HardResults.csv");

        List<String[]> csvData = new ArrayList<>();
        csvData.add(new String[]{"Instance", "NumRectangles", "NumBoxes", "Duration(ms)", "CPU(%)"});

        for (int i = 0; i < solutions.size(); i++) {
            PackingSolution sol = solutions.get(i);
            csvData.add(new String[]{
                    String.valueOf(i + 1),
                    String.valueOf(sol.getRectangles().size()),
                    String.valueOf(sol.boxes().size()),
                    String.valueOf(durations.get(i)),
                    String.format(Locale.US, "%.2f", cpuLoads.get(i))
            });
        }

        Utils.writeResult(csvData, path);
    }

    private static final OperatingSystemMXBean osBean =
            (com.sun.management.OperatingSystemMXBean)
                    ManagementFactory.getOperatingSystemMXBean();

    private double getCpuLoadPercent() {
        return osBean.getProcessCpuLoad() * 100.0;
    }
}
