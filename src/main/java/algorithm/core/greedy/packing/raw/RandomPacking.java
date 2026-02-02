package algorithm.core.greedy.packing.raw;

import algorithm.core.greedy.packing.generic.PackingStrategy;
import algorithm.model.Box;
import algorithm.model.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class RandomPacking implements PackingStrategy {

    private record Candidate(int x, int y) { }

    @Override
    public TryPackResult tryPack(Rectangle rectangle, Box box) {
        List<RandomPacking.Candidate> candidates = new ArrayList<>();

        // First candidate is the origin (bottom left corner)
        candidates.add(new RandomPacking.Candidate(0, 0));

        for (Rectangle rect : box.getRectangles()) {
            int randomX = (int) (Math.random() * (box.getLength() - rect.getWidth()));
            int randomY = (int) (Math.random() * (box.getLength() - rect.getHeight()));

            candidates.add(
                    new RandomPacking.Candidate(randomX, rect.getY())
            );
            candidates.add(
                    new RandomPacking.Candidate(rect.getX(), randomY)
            );
        }

        Boolean[] orientations = new Boolean[] { false, true };

        for (Boolean rotated : orientations) {
            for (RandomPacking.Candidate candidate : candidates) {
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
