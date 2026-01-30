package ui;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.binpacking.Box;
import model.binpacking.BinRectangle;

import java.util.List;

public class BoxVisualizer {

    private static final double SCALE = 4.0;
    private static final double SPACING_X = 50;
    private static final double SPACING_Y = 50;
    private static final int COLUMNS = 2;
    private static final double START_X = 20;
    private static final double START_Y = 20;

    private final Pane solutionPane;

    public BoxVisualizer(Pane solutionPane) {
        this.solutionPane = solutionPane;
    }

    public void drawBoxes(List<Box> boxes) {
        solutionPane.getChildren().clear();

        for (int i = 0; i < boxes.size(); i++) {
            Box box = boxes.get(i);
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            
            double offsetX = START_X + col * (box.getLength() * SCALE + SPACING_X);
            double offsetY = START_Y + row * (box.getLength() * SCALE + SPACING_Y);

            drawBox(box, offsetX, offsetY);
            drawRectanglesInBox(box, offsetX, offsetY);
        }
    }

    private void drawBox(Box box, double offsetX, double offsetY) {
        Rectangle boxRect = new Rectangle(box.getLength() * SCALE, box.getLength() * SCALE);
        boxRect.setFill(Color.LIGHTGRAY);
        boxRect.setStroke(Color.BLACK);
        boxRect.setX(offsetX);
        boxRect.setY(offsetY);
        solutionPane.getChildren().add(boxRect);

        Text boxIdText = new Text("Box: " + box.getId());
        boxIdText.setX(offsetX + 5);
        boxIdText.setY(offsetY - 5);
        boxIdText.setFill(Color.BLACK);
        solutionPane.getChildren().add(boxIdText);
    }

    private void drawRectanglesInBox(Box box, double offsetX, double offsetY) {
        double boxSize = box.getLength() * SCALE;

        for (BinRectangle rect : box.getRectangles()) {
            double rx = offsetX + rect.getPosition().getX() * SCALE;
            double ry = offsetY + boxSize - (rect.getPosition().getY() + rect.getHeight()) * SCALE;
            double rw = rect.getWidth() * SCALE;
            double rh = rect.getHeight() * SCALE;

            Rectangle r = new Rectangle(rw, rh);
            r.setX(rx);
            r.setY(ry);
            r.setFill(rect.getIsRotated() ? Color.RED : Color.BLUE);
            r.setStroke(Color.BLACK);
            solutionPane.getChildren().add(r);

            Text idText = new Text(String.valueOf(rect.getId()));
            idText.setX(rx + rw / 4);
            idText.setY(ry + rh / 2);
            idText.setFill(Color.WHITE);
            solutionPane.getChildren().add(idText);
        }
    }

    public static List<Box> selectBoxesToDisplay(List<Box> allBoxes) {
        if (allBoxes.size() <= 4) {
            return allBoxes;
        }
        return List.of(
                allBoxes.get(0),
                allBoxes.get(1),
                allBoxes.get(allBoxes.size() - 2),
                allBoxes.get(allBoxes.size() - 1)
        );
    }
}
