package environment.utils;

import algorithm.model.Box;
import algorithm.model.Rectangle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    public static void writeResult(List<String[]> data, Path path) {
        try {
            CsvWriter.write(path, data);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while writing data to " + path, e
            );
        }
    }

    public static List<Box> createOverlapBoxes(int boxLength, List<Rectangle> rects) {
        Random rand = new Random();

        int sumArea = 0;
        for (Rectangle rect : rects) {
            sumArea += rect.getArea();
        }
        int boxArea = boxLength * boxLength;
        int minNumBox = (int) Math.ceil((double) sumArea / boxArea);

        List<Box> boxes = new ArrayList<>();

        for (int i = 0; i < Math.max(1, minNumBox); i++) {
            Box box = new Box(i, boxLength);
            boxes.add(box);
        }

        for (Rectangle rect : rects) {
            // Rectangle rectCopy = rect.copy();

            int boxId = rand.nextInt(boxes.size());
            Box selectedBox = boxes.get(boxId);

            int maxX = boxLength - rect.getWidth();
            int maxY = boxLength - rect.getHeight();

            int x = (maxX > 0) ? rand.nextInt(maxX + 1) : 0;
            int y = (maxY > 0) ? rand.nextInt(maxY + 1) : 0;

            selectedBox.addRectangle(rect, x, y);
        }

        return boxes;
    }

}