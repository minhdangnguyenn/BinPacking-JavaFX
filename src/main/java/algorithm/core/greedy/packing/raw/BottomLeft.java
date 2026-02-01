package algorithm.core.greedy.packing.raw;

import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.instances.Box;
import algorithm.instances.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class BottomLeft implements PackingStrategy {

    private record Candidate(int x, int y) { }

    @Override
    public TryPackResult tryPack(Rectangle rectangle, Box box) {
        List<Candidate> candidates = new ArrayList<>();
        // First candidate is the origin
        candidates.add(new Candidate(0, 0));

        // Then sides of placed rectangles
        for (Rectangle rect : box.getRectangles()) {
            candidates.add(
                    new Candidate(rect.getX() + rect.getWidth(), rect.getY())
            );
            candidates.add(
                    new Candidate(rect.getX(), rect.getY() + rect.getHeight())
            );
        }

        // Sort by y-coordinate, then by x-coordinate (bottom-left)
        candidates.sort((c1, c2) -> {
            int x1 = c1.x();
            int y1 = c1.y();
            int x2 = c2.x();
            int y2 = c2.y();
            if (y1 == y2) {
                return x1 - x2;
            }
            return y1 - y2;
        });

        Boolean[] orientations = new Boolean[] { false, true };

        for (Boolean rotated : orientations) {
            for (Candidate candidate : candidates) {
                Rectangle testRect = rotated
                        ? new Rectangle(rectangle.getId(), rectangle.getHeight(), rectangle.getWidth())
                        : new Rectangle(
                        rectangle.getId(),
                        rectangle.getWidth(),
                        rectangle.getHeight()
                );

                testRect.setPosition(candidate.x(), candidate.y());

                boolean isOverflow = box.isOverflow(testRect);
                boolean isOverlapping = box.isOverlapping(testRect);

                if (!isOverflow && !isOverlapping) {
                    return new TryPackResult(
                            candidate.x(),
                            candidate.y(),
                            rotated
                    );
                }
            }
        }

        return TryPackResult.fail();
    }
}