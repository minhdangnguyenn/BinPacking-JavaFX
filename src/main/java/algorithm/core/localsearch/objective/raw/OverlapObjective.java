package algorithm.core.localsearch.objective.raw;

import algorithm.core.localsearch.objective.generic.Objective;
import algorithm.model.Box;
import algorithm.model.Rectangle;
import algorithm.solution.raw.OverlapPackingSolution;

import java.util.List;


public class OverlapObjective implements Objective<OverlapPackingSolution> {

    private double OverlapThreshold(int iteration, int maxIterations) {
        double progress = (double) iteration / maxIterations;
        return 100 * Math.pow((1 - progress), 2);
    }

    private int penalty(double threshold) {
//        if (threshold > 70) {
//            return 10;
//        }
//        else if (threshold > 50) {
//            return 100;
//        }
//        else if (threshold > 20) {
//            return 1000;
//        } else if (threshold > 5) {
//            return 5000;
//        } else return 10000;
        // Penalty factor tăng khi threshold giảm
        // Khi threshold cao (đầu), penalty nhỏ
        // Khi threshold thấp (cuối), penalty lớn

        if (threshold > 70) return 10;
        else if (threshold > 50) return 100;
        else if (threshold > 20) return 1000;
        else if (threshold > 5) return 5000;
        else return 10000;
    }

    public double evaluate(OverlapPackingSolution solution) {
        int numBoxes = solution.boxes().size();

        // Tính threshold (0-100)
        double threshold = OverlapThreshold(solution.currentIteration, solution.maxIterations);
        double factor = penalty(threshold);

        double totalOverlapPenalty = 0;

        for (Box box : solution.boxes()) {
            List<Rectangle> rects = box.getRectangles();
            for (int i = 0; i < rects.size() - 1; i++) {
                for (int j = i + 1; j < rects.size(); j++) {
                    // overlapRate() trả về 0-100
                    double overlap = box.overlapRate(rects.get(i), rects.get(j));

                    if (overlap > threshold) {
                        double violation = overlap - threshold;
                        totalOverlapPenalty += factor * violation * violation;
                    }
                }
            }
        }

        // Minimize: -boxes (càng ít box càng tốt) + penalty (càng ít overlap càng tốt)
        // Với penalty nặng hơn nhiều so với box count
        return -1000 * numBoxes + totalOverlapPenalty;
    }
}