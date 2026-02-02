package utils;

import algorithm.AlgorithmRunner;

public class Utils {
    public static void validConfig(AlgorithmRunner.AlgorithmConfig config) {
        if (config.rectangleCount <= 0 || config.minWidth <= 0
                || config.maxWidth <= 0 || config.minHeight <= 0
                || config.maxHeight <= 0 || config.boxLength <= 0
        ) {
            throw new IllegalArgumentException(
                    "All inputs must be positive integers."
            );
        }

        if (config.minWidth > config.maxWidth) throw new IllegalArgumentException(
                "min width cannot be greater than max width."
        );
        if (config.minHeight > config.maxHeight) throw new IllegalArgumentException(
                "min height cannot be greater than max height."
        );

        // Ensure boxL can fit the min/max rectangle sizes
        if (config.boxLength < config.minWidth || config.boxLength < config.minHeight) {
            throw new IllegalArgumentException(
                    "Box length must be at least as big as minW and min height."
            );
        }

        if (config.boxLength < config.maxWidth || config.boxLength < config.maxHeight) {
            throw new IllegalArgumentException(
                    "Box length cannot be smaller than max width or max height."
            );
        }
    }
}
