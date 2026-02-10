package environment.utils;

import java.nio.file.Path;
import java.util.List;

public class Utils {

    public static void WriteResult(List<String[]> data, Path path) {
        try {
            CsvWriter.write(path, data);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while writing data to " + path, e
            );
        }
    }

}