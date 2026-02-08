package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.GreedyAlgorithm;
import algorithm.core.greedy.ordering.generic.GreedyOrdering;
import algorithm.core.greedy.ordering.raw.AreaDescOrder;
import algorithm.core.greedy.ordering.raw.SideDescOrder;
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
import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static utils.Utils.calculateAllowedOverlap;

public class Overlap implements Neighborhood<OverlapPackingSolution> {

    private final GreedyAlgorithm<PackingSolution, Rectangle> greedySolver;
    private int currentIteration = 0;

    public Overlap() {
        GreedyOrdering<Rectangle> ordering = new SideDescOrder();
        PackingStrategy puttingStrategy = new BottomLeft();
        GreedyStrategy<PackingSolution, Rectangle> extender = new FirstFitStrategy(puttingStrategy);
        this.greedySolver = new GreedyAlgorithm<>(ordering, extender);
    }

    private void moveRectangleToNewBox(
            OverlapPackingSolution solution,
            Box sourceBox
    ) {
        for (int i = 0; i< sourceBox.getRectangles().size(); i++) {
            for (int j = i+1; j<sourceBox.getRectangles().size(); j++ ) {
                Rectangle rect1 = sourceBox.getRectangles().get(i);
                Rectangle rect2 = sourceBox.getRectangles().get(j);
                if (sourceBox.isOverlapping(rect1, rect2)) {
                    // Remove from old box
                    sourceBox.removeRectangle(rect2);

                    // Create new box
                    int newBoxId = solution.boxes().size();
                    Box newBox = new Box(newBoxId, sourceBox.getLength());

                    // Place rectangle (safe position)
                    newBox.addRectangle(rect2, 0, 0);

                    solution.boxes().add(newBox);
                }
            }
        }
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

    @Override
    public Iterable<OverlapPackingSolution> getNeighbors(OverlapPackingSolution solution) {
        solution.currentIteration++;
        int boxLength = solution.boxes().getFirst().getLength();
        List<OverlapPackingSolution> neighbors = new ArrayList<>();

        OverlapPackingSolution neighbor = solution.copy();
        for (Box box : solution.boxes()) {
            if (!ContainsOverlap(box)) continue;
            moveRectangleToNewBox(neighbor, box);
        }

        List<Rectangle> totalUnpackRectangles = new ArrayList<>();
        for (int i = neighbor.boxes().size()/2; i<neighbor.boxes().size(); i++) {
            List<Rectangle> rects = neighbor.boxes().get(i).unpackAllRectangles();
            totalUnpackRectangles.addAll(rects);
        }

        PackingSolution base = neighbor.toPackingSolution();
        PackingSolution solvedPacking = this.greedySolver.solve(base, totalUnpackRectangles);

        OverlapPackingSolution neighbor1 = solvedPacking.toOverlapSolution();
        neighbor1.boxes().removeIf(box -> box.getRectangles().isEmpty());
        for (int i = 0; i<neighbor1.boxes().size(); i++) {
            neighbor1.boxes().get(i).setId(i);
        }

        neighbors.add(neighbor1);
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


    private OverlapPackingSolution createSnapNeighbor(OverlapPackingSolution solution, Rectangle r, double P) {
        OverlapPackingSolution next = solution.copy();
        Rectangle target = next.findRectangleById(r.getId());

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