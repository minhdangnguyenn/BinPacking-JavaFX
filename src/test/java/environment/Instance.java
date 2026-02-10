package environment;

import algorithm.model.Rectangle;

import java.util.List;

public record Instance(int boxSize, List<Rectangle> rectangles) {}