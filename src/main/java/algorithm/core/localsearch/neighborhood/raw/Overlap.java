package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.core.greedy.packing.raw.BottomLeft;
import algorithm.core.greedy.packing.raw.TryPackResult;
import algorithm.core.greedy.strategy.generic.GreedyStrategy;
import algorithm.core.greedy.strategy.raw.FirstFitStrategy;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;
import algorithm.solution.raw.PackingSolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Overlap implements Neighborhood<OverlapPackingSolution> {

    private final GreedyAlgorithm<PackingSolution, Rectangle> greedySolver;
    private int currentIteration = 0;

    public Overlap() {
        GreedyOrdering<Rectangle> ordering = new AreaDescOrder();
        PackingStrategy puttingStrategy = new BottomLeft();
        GreedyStrategy<PackingSolution, Rectangle> extender = new FirstFitStrategy(puttingStrategy);
        this.greedySolver = new GreedyAlgorithm<>(ordering, extender);
    }

    private void PushApart(Rectangle rect1, Rectangle rect2, int boxSize, double progress) {
        // progress 0 -> 1, 0: no push, 1 completely push

        int xOverlap = Math.min(rect1.getX() + rect1.getWidth(), rect2.getX() + rect2.getWidth())
                - Math.max(rect1.getX(), rect2.getX());
        int yOverlap = Math.min(rect1.getY() + rect1.getHeight(), rect2.getY() + rect2.getHeight())
                - Math.max(rect1.getY(), rect2.getY());

        // no overlap -> nothing to do
        if (xOverlap <= 0 && yOverlap <= 0) {
            return;
        }

        // Scale overlap amount base on progress
        double scaledXOverlap = xOverlap * progress;
        double scaledYOverlap = yOverlap * progress;

        // clamp helper
        java.util.function.IntUnaryOperator clampX1 = v -> Math.max(0, Math.min(v, boxSize - rect1.getWidth()));
        java.util.function.IntUnaryOperator clampY1 = v -> Math.max(0, Math.min(v, boxSize - rect1.getHeight()));
        java.util.function.IntUnaryOperator clampX2 = v -> Math.max(0, Math.min(v, boxSize - rect2.getWidth()));
        java.util.function.IntUnaryOperator clampY2 = v -> Math.max(0, Math.min(v, boxSize - rect2.getHeight()));

        if (xOverlap >= yOverlap) {
            // push along X
            int rect1Distance = Math.min(rect1.getX(), boxSize - rect1.getX() - rect1.getWidth());
            int rect2Distance = Math.min(rect2.getX(), boxSize - rect2.getX() - rect2.getWidth());

            int rect1CenterX = rect1.getX() + rect1.getWidth() / 2;
            int rect2CenterX = rect2.getX() + rect2.getWidth() / 2;

            int direction1 = (rect1CenterX < rect2CenterX) ? -1 : 1;
            int direction2 = -direction1;

            if (rect1Distance > rect2Distance) {
                int toPush = Math.min(rect1Distance, (int)Math.ceil(scaledXOverlap));
                int newX = clampX1.applyAsInt(rect1.getX() + direction1 * toPush);
                rect1.setPosition(newX, rect1.getY());
            } else {
                int toPush = Math.min(rect2Distance, (int)Math.ceil(scaledXOverlap));
                int newX = clampX2.applyAsInt(rect2.getX() + direction2 * toPush);
                rect2.setPosition(newX, rect2.getY());
            }
        } else {
            // push along Y
            int rect1Distance = Math.min(rect1.getY(), boxSize - rect1.getY() - rect1.getHeight());
            int rect2Distance = Math.min(rect2.getY(), boxSize - rect2.getY() - rect2.getHeight());

            int rect1CenterY = rect1.getY() + rect1.getHeight() / 2;
            int rect2CenterY = rect2.getY() + rect2.getHeight() / 2;

            int direction1 = (rect1CenterY < rect2CenterY) ? -1 : 1;
            int direction2 = -direction1;

            if (rect1Distance > rect2Distance) {
                int toPush = Math.min(rect1Distance, (int)Math.ceil(scaledYOverlap));
                int newY = clampY1.applyAsInt(rect1.getY() + direction1 * toPush);
                rect1.setPosition(rect1.getX(), newY);
            } else {
                int toPush = Math.min(rect2Distance, (int)Math.ceil(scaledYOverlap));
                int newY = clampY2.applyAsInt(rect2.getY() + direction2 * toPush);
                rect2.setPosition(rect2.getX(), newY);
            }
        }
    }

    private void ResolveOverlapsInBox(Box box, int maxIterations, int progress) {
        List<Rectangle> rectangles = box.getRectangles();
        boolean overlapsExist;

        do {
            overlapsExist = false;

            for (int i = 0; i < rectangles.size(); i++) {
                for (int j = i + 1; j < rectangles.size(); j++) {
                    Rectangle rect1 = rectangles.get(i);
                    Rectangle rect2 = rectangles.get(j);

                    if (box.isOverlapping(rect1, rect2)) {
                        overlapsExist = true;
                        PushApart(rect1, rect2, box.getLength(), progress);
                    }
                }
            }
        } while (overlapsExist && --maxIterations > 0);
    }

    private boolean ContainsOverlap(Box box) {
        List<Rectangle> rectangles = box.getRectangles();

        for (int i = 0; i < rectangles.size(); i++) {
            for (int j = i + 1; j < rectangles.size(); j++) {
                Rectangle rect1 = rectangles.get(i);
                Rectangle rect2 = rectangles.get(j);

                if (box.isOverlapping(rect1, rect2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void ApplyGravity(Box box) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (Rectangle rectangle : box.getRectangles()) {
            if (rectangle.getX() < minX) {
                minX = rectangle.getX();
            }
            if (rectangle.getY() < minY) {
                minY = rectangle.getY();
            }
        }

        // Translate the whole system to bottom-left corner
        for (Rectangle rectangle : box.getRectangles()) {
            rectangle.setPosition(rectangle.getX() - minX, rectangle.getY() - minY);
        }
    }

    private double calculateOverlapArea(Rectangle a, Rectangle b) {
        // S_intersection/max(S_a, S_b)
        double xLeft = Math.max(a.getX(), b.getX());
        double xRight = Math.min(a.getX() + a.getWidth(), b.getX() + b.getWidth());
        double yBottom = Math.max(a.getY(), b.getY());
        double yTop = Math.min(a.getY() + a.getHeight(), b.getY() + b.getHeight());

        double width = xRight - xLeft;
        double height = yTop - yBottom;

        if (width > 0 && height > 0) {
            return width * height;
        }
        return 0.0;
    }

    private List<Rectangle> findViolatingRectangles(OverlapPackingSolution solution, double P) {
        List<Rectangle> violators = new ArrayList<>();
        for (Box box : solution.boxes()) {
            List <Rectangle> rects = box.getRectangles();
            int n = rects.size();

            for (int i = 0; i < n; i++) {
                for (int j = i+1; j < n; j++) {
                    Rectangle rA = rects.get(i);
                    Rectangle rB = rects.get(j);

                    double relO = calculateOverlapArea(rA, rB);

                    if (relO > P) {
                        if (!violators.contains(rA)) violators.add(rA);
                        if (!violators.contains(rB)) violators.add(rB);
                    }
                }
            }
        }

        return violators;
    }

    private double calculateAllowedOverlap(int currentIteration, int maxIterations) {
        if (maxIterations <= 0) return 0;

        // P drops linearly from 1.0 to 0.0
        double P = 1.0 - ((double) currentIteration / maxIterations);

        // Ensure it stays within [0.0, 1.0]
        return Math.max(0.0, Math.min(1.0, P));
    }

    @Override
    public Iterable<OverlapPackingSolution> getNeighbors(OverlapPackingSolution solution) {
        solution.currentIteration += 1;

        System.out.println("\n=== OVERLAP ITERATION " + solution.currentIteration + "/" + solution.maxIterations + " ===");

        List<OverlapPackingSolution> neighbors = new ArrayList<>();

        // 1. Calculate current allowed overlap percentage (P)
        double P = calculateAllowedOverlap(solution.currentIteration, solution.maxIterations);
        System.out.println("Allowed overlap P: " + String.format("%.4f", P));
        
        // Count current overlaps
        int currentOverlaps = countTotalOverlaps(solution);
        System.out.println("Current solution: " + solution.boxes().size() + " boxes, " + currentOverlaps + " overlaps");

        // 2. Identify "Problematic" Rectangles
        List<Rectangle> violators = findViolatingRectangles(solution, P);
        System.out.println("Violators found: " + violators.size());

        // If no violators, the current solution is 'feasible' for this stage
        if (violators.isEmpty()) {
            violators = solution.getAllRectangles();
        }

        // 3. Generate Neighbors based on the current Phase
        for (Rectangle r : selectSubset(violators, 5)) {
//            neighbors.add(createShiftNeighbor(solution, r, P));
//            neighbors.add(createSnapNeighbor(solution, r, P));

            if (P > 0.5) {
                // neighbors.add(createSwapNeighbor(solution, r));
            }
        }
        int progress = solution.currentIteration / solution.maxIterations;
        for (Box box : solution.boxes()) {
            ResolveOverlapsInBox(box, 100, progress);
        }
        neighbors.add(createSplitNeighbor(solution, P));

        // remove all overlaps in last 20% iteration
        double progressPercent = (double) solution.currentIteration / solution.maxIterations;
        if (progressPercent >= 0.8) {
            System.out.println(">>> FINAL PHASE: Adding greedy resolution neighbor");
            OverlapPackingSolution greedyNeighbor = createGreedyResolutionNeighbor(solution);
            int greedyOverlaps = countTotalOverlaps(greedyNeighbor);
            System.out.println(">>> Greedy neighbor: " + greedyNeighbor.boxes().size() + " boxes, " + greedyOverlaps + " overlaps");
            neighbors.add(greedyNeighbor);
        }

//        for (OverlapPackingSolution neighbor : neighbors) {
//            for (Box box : neighbor.boxes()) {
//                ResolveOverlapsInBox(box, 100, progress);
//            }
//        }

        System.out.println("Generated " + neighbors.size() + " neighbors\n");

        return neighbors;
    }
    
    private int countTotalOverlaps(OverlapPackingSolution solution) {
        int count = 0;
        for (Box box : solution.boxes()) {
            List<Rectangle> rects = box.getRectangles();
            for (int i = 0; i < rects.size(); i++) {
                for (int j = i + 1; j < rects.size(); j++) {
                    if (calculateOverlapArea(rects.get(i), rects.get(j)) > 0) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private Rectangle[] selectSubset(List<Rectangle> violators, int count) {
        if (violators == null || violators.isEmpty()) {
            return new Rectangle[0];
        }

        // if number violators < number we take, -> take all violators
        int actualCount = Math.min(count, violators.size());

        // 3. shuffle the list to guarantee the random
        Collections.shuffle(violators);

        Rectangle[] subset = new Rectangle[actualCount];
        for (int i = 0; i < actualCount; i++) {
            subset[i] = violators.get(i);
        }

        return subset;
    }

    private OverlapPackingSolution createSnapNeighbor(OverlapPackingSolution solution, Rectangle r, double P) {
        OverlapPackingSolution next = solution.copy();
        Rectangle target = next.findRectangleById(r.getId());

        // Tìm một hình chữ nhật khác đang chồng lấp với target
        for (Rectangle other : next.getAllRectangles()) {
            if (target == other) continue;
            if (calculateOverlapArea(target, other) > 0) {
                // Đẩy target sang phải của 'other' (ví dụ 1 trong 4 hướng ngẫu nhiên)
                double strategy = Math.random();
                if (strategy < 0.5) {
                    target.setX(Math.min(next.getBoxLength() - target.getWidth(), other.getX() + other.getWidth()));
                } else {
                    target.setY(Math.min(next.getBoxLength() - target.getHeight(), other.getY() + other.getHeight()));
                }
                break;
            }
        }
        return next;
    }

    private OverlapPackingSolution createSwapNeighbor(OverlapPackingSolution solution, Rectangle r) {
        OverlapPackingSolution next = solution.copy();
        Rectangle target = next.findRectangleById(r.getId());

        List<Rectangle> all = next.getAllRectangles();
        Rectangle other = all.get((int) (Math.random() * all.size()));

        if (target != other) {
            double tempX = target.getX();
            double tempY = target.getY();
            target.setX(other.getX());
            target.setY(other.getY());
            other.setX((int) tempX);
            other.setY((int) tempY);
        }

        return next;
    }

    private OverlapPackingSolution createShiftNeighbor(OverlapPackingSolution solution, Rectangle r, double P) {
        OverlapPackingSolution next = solution.copy();
        Rectangle target = next.findRectangleById(r.getId());
        Box box = next.findBoxContaining(target);

        double range = 10.0 * P + 1.0;
        double dx = (Math.random() * 2 - 1) * range;
        double dy = (Math.random() * 2 - 1) * range;

        int newX = (int) Math.max(0, Math.min(box.getLength() - target.getWidth(), target.getX() + dx));
        int newY = (int) Math.max(0, Math.min(box.getLength() - target.getHeight(), target.getY() + dy));

        box.addRectangle(target, newX, newY);

        return next;
    }

    private OverlapPackingSolution createGreedyResolutionNeighbor(OverlapPackingSolution solution) {
        List<Rectangle> allRects = solution.getAllRectangles(); // all are overlapped

        // 2. Tạo danh sách các Box trống mới (ID và kích thước giống cũ)
        List<Box> emptyBoxes = new ArrayList<>();
        for (Box b : solution.boxes()) {
            emptyBoxes.add(new Box(b.getId(), b.getLength()));
        }

        // 3. Tạo một PackingSolution trống để thuật toán Greedy làm việc
        PackingSolution emptySolution = new PackingSolution(emptyBoxes);

        PackingSolution feasible = greedySolver.solve(emptySolution, allRects);

        // 5. Trả về kết quả dưới dạng OverlapPackingSolution
        // Chú ý: dùng .boxes() hoặc .getBoxes() của feasible để lấy List<Box>
        return new OverlapPackingSolution(feasible.boxes());
    }

    private OverlapPackingSolution createSplitNeighbor(OverlapPackingSolution solution, double P) {
        OverlapPackingSolution next = solution.copy();

        // Tìm box có overlap cao nhất
        Box worstBox = null;
        double worstOverlap = -1;

        for (Box box : next.boxes()) {
            double boxOverlap = 0;
            List<Rectangle> rects = box.getRectangles();
            for (int i = 0; i < rects.size(); i++) {
                for (int j = i+1; j < rects.size(); j++) {
                    boxOverlap += calculateOverlapArea(rects.get(i), rects.get(j));
                }
            }
            if (boxOverlap > worstOverlap) {
                worstOverlap = boxOverlap;
                worstBox = box;
            }
        }

        // Chia box này thành 2
        if (worstBox != null && worstBox.getRectangles().size() > 1) {
            List<Rectangle> rects = new ArrayList<>(worstBox.getRectangles());
            next.boxes().remove(worstBox);

            Box box1 = new Box(next.boxes().size(), worstBox.getLength());
            Box box2 = new Box(next.boxes().size() + 1, worstBox.getLength());

            // Chia đôi rectangles
            int mid = rects.size() / 2;
            for (int i = 0; i < rects.size(); i++) {
                Rectangle rect = rects.get(i);
                if (i < mid) {
                    box1.addRectangle(rect.copy(),
                            (int)(Math.random() * (box1.getLength() - rect.getWidth())),
                            (int)(Math.random() * (box1.getLength() - rect.getHeight())));
                } else {
                    box2.addRectangle(rect.copy(),
                            (int)(Math.random() * (box2.getLength() - rect.getWidth())),
                            (int)(Math.random() * (box2.getLength() - rect.getHeight())));
                }
            }

            next.boxes().add(box1);
            next.boxes().add(box2);
        }

        return next;
    }

}