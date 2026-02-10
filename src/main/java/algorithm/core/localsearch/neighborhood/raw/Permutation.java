package algorithm.core.localsearch.neighborhood.raw;

import algorithm.core.greedy.Greedy;
import algorithm.core.localsearch.neighborhood.generic.Neighborhood;
import algorithm.model.Rectangle;
import algorithm.solution.raw.PackingSolution;
import algorithm.solution.raw.PermutationSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Permutation implements Neighborhood<PermutationSolution> {

    static Greedy<PackingSolution, Rectangle> greedy;

    // private final int boxLength;

    public Permutation() {}


    /**
     * Many different permutations, derived from the given one permutation
     * @param initialSolution the first initial solution
     * @return a list of permutation solution
     * each permutation solution stores only different orders of triangles
     */
    @Override
    public Iterable<PermutationSolution> getNeighbors(PermutationSolution initialSolution) {
        List<PermutationSolution> neighbors = new ArrayList<>();

        PermutationSolution temp = initialSolution.copy();

        // NEIGHBOR 1
        // Swap randomly 2 rectangles in the permutation solution to create a new permutation
        Random random = new Random();
        int chosenIndex = random.nextInt(temp.getRectangles().size());
        int swapIndex;
        do {
            swapIndex = random.nextInt(temp.getRectangles().size());
        } while (chosenIndex == swapIndex);

        // Swap the 2 rectangles
        Rectangle rectangle = temp.getRectangles().get(swapIndex);
        temp.getRectangles().set(swapIndex, temp.getRectangles().get(chosenIndex));
        temp.getRectangles().set(chosenIndex, rectangle);

        neighbors.add(temp);

        // NEIGHBOR 2
        // Create a second neighbor by reversing a random subsequence
        PermutationSolution temp2 = initialSolution.copy();
        int startIndex = random.nextInt(temp2.getRectangles().size());
        int endIndex = random.nextInt(temp2.getRectangles().size());
        
        if (startIndex > endIndex) {
            int swap = startIndex;
            startIndex = endIndex;
            endIndex = swap;
        }
        
        if (startIndex != endIndex) {
            List<Rectangle> rectangles = temp2.getRectangles();
            while (startIndex < endIndex) {
                Rectangle tempRect = rectangles.get(startIndex);
                rectangles.set(startIndex, rectangles.get(endIndex));
                rectangles.set(endIndex, tempRect);
                startIndex++;
                endIndex--;
            }
            neighbors.add(temp2);
        }

        return neighbors;
    }
}
