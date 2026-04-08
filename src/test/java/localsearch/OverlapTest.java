package localsearch;

import algorithm.core.localsearch.LocalSearch;
import algorithm.core.localsearch.neighborhood.raw.Overlap;
import algorithm.core.localsearch.objective.raw.OverlapObjective;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;
import environment.Instance;
import environment.TestEnvironment;
import environment.utils.Utils;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

class OverlapTest {

    // {numInstances, numRects, minW, minH, maxW, maxH, boxLen}
    private static final int[][] SMALL_TUPLES = {
            {  5,   500,  1, 1, 100, 100, 100 },
            {  5,  1000,  1, 1, 100, 100, 100 },
            {  3,  3000,  1, 1, 100, 100, 100 },
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

    private OverlapObjective objective = new OverlapObjective();

    private LocalSearch<OverlapPackingSolution> buildLocalSearch(int maxIterations) {
        return new LocalSearch<>(new Overlap(), objective, maxIterations);
    }

    /**
     * Bad initial solution: randomly place all rectangles into the minimum
     * number of boxes needed by area — rectangles overlap freely.
     * Mirrors Controller.initOverlapSolution().
     */
    private OverlapPackingSolution buildBadInitial(List<Rectangle> rectangles, int boxLen, int maxIterations) {
        // Shuffle a copy — mirrors getShuffleCopyRectangles()
        List<Rectangle> rects = new ArrayList<>();
        for (Rectangle rect : rectangles) {
            rects.add(rect.copy());
        }
        Collections.shuffle(rects);

        Random rand = new Random();

        // Minimum boxes by area
        int sumArea = 0;
        for (Rectangle rect : rects) sumArea += rect.getArea();
        int boxArea    = boxLen * boxLen;
        int minNumBox  = (int) Math.ceil((double) sumArea / boxArea);

        List<Box> boxes = new ArrayList<>();
        for (int i = 0; i < Math.max(1, minNumBox); i++) {
            boxes.add(new Box(i, boxLen));
        }

        // Randomly assign each rectangle to a box at a random valid position
        for (Rectangle rect : rects) {
            int boxIdx = rand.nextInt(boxes.size());
            Box selectedBox = boxes.get(boxIdx);

            int maxX = boxLen - rect.getWidth();
            int maxY = boxLen - rect.getHeight();
            int x    = (maxX > 0) ? rand.nextInt(maxX + 1) : 0;
            int y    = (maxY > 0) ? rand.nextInt(maxY + 1) : 0;

            selectedBox.addRectangle(rect, x, y);
        }

        // Build solution from non-empty boxes
        OverlapPackingSolution solution = new OverlapPackingSolution(boxLen);
        solution.boxes().clear();
        for (Box box : boxes) {
            if (!box.getRectangles().isEmpty()) {
                solution.addBox(box);
            }
        }
        if (solution.boxes().isEmpty()) {
            solution.addBox(new Box(0, boxLen));
        }

        // Wrap with iteration budget
        OverlapPackingSolution initial = OverlapPackingSolution.init(solution.boxes(), maxIterations);
        initial.maxIterations    = maxIterations;
        initial.currentIteration = 0;

        System.out.printf(Locale.US,
                "  bad init → boxes=%d | rects=%d%n",
                initial.boxes().size(), rects.size());

        return initial;
    }

    // ── Main test — runs BOTH modes, writes TWO files ────────────────────────
    @Test
    void runTestEnvironment() {
        runMode("small", SMALL_TUPLES,  100);
        runMode("large", LARGE_TUPLES, 100);
    }

    private void runMode(String modeName, int[][] tuples, int maxIterations) {
        LocalSearch<OverlapPackingSolution> localSearch = buildLocalSearch(maxIterations);

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

                // ── Bad initial: random overlapping placement ─────────────
                OverlapPackingSolution initial = buildBadInitial(
                        instance.rectangles(), boxLen, maxIterations);

                // ── Local Search ──────────────────────────────────────────
                long cpuStart  = cpuTimeNs();
                long wallStart = System.nanoTime();

                OverlapPackingSolution solution = localSearch.solve(initial);
                double objectiveScore          = objective.evaluate(solution);

                double cpuMs  = (cpuTimeNs()       - cpuStart)  / 1_000_000.0;
                double wallMs = (System.nanoTime() - wallStart) / 1_000_000.0;

                int numBoxes = solution.boxes().size();

                System.out.printf(Locale.US,
                        "  inst %d/%d → boxes=%d | objective score=%.2f | cpu=%.2f ms | wall=%.2f ms%n",
                        i + 1, numInstances, numBoxes, objectiveScore, cpuMs, wallMs);

                // ── Correctness assertions ────────────────────────────────
                org.junit.jupiter.api.Assertions.assertNotNull(solution);
                org.junit.jupiter.api.Assertions.assertFalse(solution.boxes().isEmpty());
                for (Box box : solution.boxes()) {
                    org.junit.jupiter.api.Assertions.assertEquals(0.0, box.totalOverlapRate(),
                            "Box " + box.getId() + " has overlap in instance " + (i + 1));
                    for (Rectangle rect : box.getRectangles()) {
                        org.junit.jupiter.api.Assertions.assertFalse(box.isOverflow(rect),
                                "Rectangle " + rect.getId() + " overflows in instance " + (i + 1));
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

        Path path = Paths.get("test", "csv", "localsearch_overlap_" + modeName + ".csv");
        Utils.writeResult(csv, path);
        System.out.printf(Locale.US, "%nLog written → %s%n", path.toAbsolutePath());
    }
}
