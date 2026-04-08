package localsearch;

import algorithm.core.greedy.Greedy;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.LocalSearch;
import algorithm.core.localsearch.neighborhood.raw.Geometry;
import algorithm.core.localsearch.objective.raw.MaximizeUsedArea;
import algorithm.model.Rectangle;
import algorithm.solution.raw.PackingSolution;
import environment.Instance;
import environment.TestEnvironment;
import environment.utils.Utils;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class GeometryTest {

    // {numInstances, numRects, minW, minH, maxW, maxH, boxLen}
    private static final int[][] SMALL_TUPLES = {
            {  5,   500,  1, 1, 100, 100, 100 },
            {  5,  1000,  1, 1, 100, 100, 100 },
            {  3,  3000,  1, 1, 100, 100, 100 }
    };

    private static final int[][] LARGE_TUPLES = {
            { 10,  1000,  1, 1, 100, 100, 300 },
            { 10,  3000,  1, 1, 100, 100, 300 },
            {  5,  5000,  1, 1, 100, 100, 300 },
            {  3, 10000,  1, 1, 100, 100, 300 },
    };

    // ── Time utilities ───────────────────────────────────────────────────────
    private static final ThreadMXBean THREAD_MX = ManagementFactory.getThreadMXBean();

    private long cpuTimeNs() {
        return THREAD_MX.getCurrentThreadCpuTime();
    }

    private final MaximizeUsedArea objective = new MaximizeUsedArea();

    // ── Solvers ──────────────────────────────────────────────────────────────
    private Greedy<PackingSolution, Rectangle> buildGreedy() {
        PackingStrategy bottomLeft = new BottomLeft();
        return new Greedy<>(new AreaDescOrder(), new FirstFitStrategy(bottomLeft));
    }

    private LocalSearch<PackingSolution> buildLocalSearch(int maxIterations) {
        return new LocalSearch<>(new Geometry(), objective, maxIterations);
    }

    // ── Main test — runs BOTH modes, writes TWO files ────────────────────────
    @Test
    void runTestEnvironment() {
        runMode("small", SMALL_TUPLES,  1000);
        runMode("large", LARGE_TUPLES, 1000);
    }

    private void runMode(String modeName, int[][] tuples, int maxIterations) {
        Greedy<PackingSolution, Rectangle> greedy = buildGreedy();
        LocalSearch<PackingSolution> localSearch  = buildLocalSearch(maxIterations);

        List<String[]> csv = new ArrayList<>();
        csv.add(new String[]{
                "Mode", "Tuple",
                "Instance", "NumRects", "BoxLen",
                "NumBoxes",
                "ObjectiveScore",
                "CpuTime_ms",
                "WallTime_ms"
        });

        for (int t = 0; t < tuples.length; t++) {
            int[] tp         = tuples[t];
            int numInstances = tp[0];
            int numRects     = tp[1];
            int minW         = tp[2];
            int minH         = tp[3];
            int maxW         = tp[4];
            int maxH         = tp[5];
            int boxLen       = tp[6];

            String tupleLabel = String.format(Locale.US,
                    "T%d(n=%d,r=%d,min=%d/%d,max=%d/%d,L=%d)",
                    t + 1, numInstances, numRects, minW, minH, maxW, maxH, boxLen);

            System.out.printf(Locale.US, "%n=== [%s] %s ===%n", modeName.toUpperCase(), tupleLabel);

            TestEnvironment env = new TestEnvironment();
            List<Instance> instances = env.generateInstances(
                    numInstances, numRects, minW, minH, maxW, maxH, boxLen);

            for (int i = 0; i < instances.size(); i++) {
                Instance instance = instances.get(i);
                PackingSolution initial = new PackingSolution(instance.boxSize());

                // ── Greedy initial solution + Local Search ────────────────
                long cpuStart  = cpuTimeNs();
                long wallStart = System.nanoTime();

                PackingSolution greedySolution = greedy.solve(initial, instance.rectangles());
                PackingSolution solution       = localSearch.solve(greedySolution);
                double objectiveScore          = objective.evaluate(solution);

                double cpuMs  = (cpuTimeNs()       - cpuStart)  / 1_000_000.0;
                double wallMs = (System.nanoTime() - wallStart) / 1_000_000.0;

                int numBoxes = solution.boxes().size();

                System.out.printf(Locale.US,
                        "  inst %d/%d → boxes=%d | objective score=%.2f, cpu=%.2f ms | wall=%.2f ms%n",
                        i + 1, numInstances, numBoxes, objectiveScore, cpuMs, wallMs);

                // ── Correctness assertions ────────────────────────────────
                org.junit.jupiter.api.Assertions.assertNotNull(solution);
                org.junit.jupiter.api.Assertions.assertFalse(solution.boxes().isEmpty());
                for (var box : solution.boxes()) {
                    org.junit.jupiter.api.Assertions.assertEquals(0.0, box.totalOverlapRate(),
                            "Overlap in instance " + (i + 1));
                    for (Rectangle rect : box.getRectangles()) {
                        org.junit.jupiter.api.Assertions.assertFalse(box.isOverflow(rect),
                                "Overflow in instance " + (i + 1));
                    }
                }

                // ── Log row ───────────────────────────────────────────────
                csv.add(new String[]{
                        modeName,
                        tupleLabel,
                        String.valueOf(i + 1),
                        String.valueOf(instance.rectangles().size()),
                        String.valueOf(boxLen),
                        String.valueOf(numBoxes),
                        String.format(Locale.US, "%.4f", objectiveScore),
                        String.format(Locale.US, "%.2f", cpuMs),
                        String.format(Locale.US, "%.2f", wallMs)
                });
            }
        }

        // target/csv/localsearch_geometry_fast.csv  or  ..._large.csv
        Path path = Paths.get("test", "csv", "localsearch_geometry_" + modeName + ".csv");
        Utils.writeResult(csv, path);
        System.out.printf(Locale.US, "%nLog written → %s%n", path.toAbsolutePath());
    }
}
