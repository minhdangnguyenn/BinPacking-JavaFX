package ui;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class InputValidator {

    private static final UnaryOperator<TextFormatter.Change> NUMERIC_FILTER = change -> {
        String newText = change.getControlNewText();
        return newText.matches("\\d*") ? change : null;
    };

    public static void applyNumericFilter(TextField... fields) {
        for (TextField field : fields) {
            field.setTextFormatter(new TextFormatter<>(NUMERIC_FILTER));
        }
    }

    public static int parseField(TextField field, int defaultValue) {
        try {
            return Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
