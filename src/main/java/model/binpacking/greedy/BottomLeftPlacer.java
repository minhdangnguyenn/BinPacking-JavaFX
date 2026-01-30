package model.binpacking.greedy;

import java.util.List;
import model.algorithm.ToPlacePosition;
import model.algorithm.greedy.GreedyPlacement;
import model.binpacking.AlgSolution;
import model.binpacking.BinRectangle;
import model.binpacking.Box;
import model.binpacking.InitPosition;

public class BottomLeftPlacer
    extends GreedyPlacement<BinRectangle, Box, AlgSolution>
{

    private int boxL;

    public BottomLeftPlacer(int boxL) {
        super();
        this.boxL = boxL;
    }

    @Override
    protected ToPlacePosition canPlace(
        BinRectangle item,
        AlgSolution solution
    ) {
        List<Box> boxes = solution.getItems();

        // Try to place in existing boxes
        for (Box box : boxes) {
            ToPlacePosition pos = canPlaceInABox(item, box);
            if (pos != null) {
                return pos;
            }
        }

        // No existing box fits, create a new box
        int newBoxId = boxes.size();
        Box newBox = new Box(newBoxId, boxL);
        solution.getItems().add(newBox);

        // Try to place in the new box
        return canPlaceInABox(item, newBox);
    }

    private ToPlacePosition canPlaceInABox(BinRectangle item, Box box) {
        // 1. normal orientation
        ToPlacePosition normalResult = tryPlaceInBox(item, box, false);
        if (normalResult != null) return normalResult;

        // 2. rotated (only if rectangle is not square)
        if (item.getWidth() != item.getHeight()) {
            ToPlacePosition rotatedResult = tryPlaceInBox(item, box, true);
            if (rotatedResult != null) return rotatedResult;
        }

        return null;
    }

    private ToPlacePosition tryPlaceInBox(
        BinRectangle item,
        Box box,
        boolean shouldRotate
    ) {
        // Temporarily rotate
        if (shouldRotate) item.rotate();

        ToPlacePosition result = null;

        // 1. bottom-left corner
        InitPosition originalPos = new InitPosition(0, 0);
        if (box.checkPossible(item, originalPos)) {
            result = new ToPlacePosition(box.getId(), 0, 0, shouldRotate);
        }

        // 2. positions induced by placed rectangles
        if (result == null) {
            for (BinRectangle placed : box.getRectangles()) {
                // to the right
                InitPosition posRight = new InitPosition(
                    placed.getPosition().getX() + placed.getWidth(),
                    placed.getPosition().getY()
                );
                if (box.checkPossible(item, posRight)) {
                    result = new ToPlacePosition(
                        box.getId(),
                        placed.getPosition().getX() + placed.getWidth(),
                        placed.getPosition().getY(),
                        shouldRotate
                    );
                    break;
                }

                // above
                InitPosition posLeft = new InitPosition(
                    placed.getPosition().getX(),
                    placed.getPosition().getY() + placed.getHeight()
                );

                if (box.checkPossible(item, posLeft)) {
                    result = new ToPlacePosition(
                        box.getId(),
                        placed.getPosition().getX(),
                        placed.getPosition().getY() + placed.getHeight(),
                        shouldRotate
                    );
                    break;
                }
            }
        }

        // Rotate back to original orientation
        if (shouldRotate) item.rotate();

        return result;
    }

    @Override
    protected void place(
        BinRectangle rect,
        AlgSolution solution,
        ToPlacePosition pos
    ) {
        // Cast to ToPlacePosition
        ToPlacePosition toPlacePos = (ToPlacePosition) pos;

        Box box = solution.getItems().get(toPlacePos.getCid());

        // Rotate if needed
        if (
            toPlacePos.getShouldRotate() != null && toPlacePos.getShouldRotate()
        ) {
            rect.rotate();
        }

        // Set position and add to box
        rect.setPosition(toPlacePos.getX(), toPlacePos.getY());
        box.addRectangle(rect);
    }
}
