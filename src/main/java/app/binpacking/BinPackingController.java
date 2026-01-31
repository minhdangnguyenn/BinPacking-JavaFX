package app.binpacking;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import model.binpacking.BinRectangle;
import ui.AlgorithmRunner;
import ui.BoxVisualizer;
import ui.InputValidator;
import java.util.ArrayList;

public class BinPackingController {

    public Label boxesLabel;
    public Label rectanglesLabel;
    public Button runButton;
    public Label runtimeLabel;
    public ArrayList<BinRectangle> testInstances = new ArrayList<>();
    public Button generateInstancesButton;
    public Label generatedInstancesCount;
    @FXML private ComboBox<String> algorithmCombo;
    @FXML private ComboBox<String> neighborhoodCombo;
    @FXML private ComboBox<String> selectionCombo;
    @FXML private TextField rectanglesNumberField;
    @FXML private TextField minWField;
    @FXML private TextField maxWField;
    @FXML private TextField minHField;
    @FXML private TextField maxHField;
    @FXML private TextField boxLField;
    @FXML private Pane solutionPane;

    private BoxVisualizer visualizer;
    private final AlgorithmRunner algorithmRunner = new AlgorithmRunner();
    @FXML
    public void initialize() {
        visualizer = new BoxVisualizer(solutionPane);

        InputValidator.applyNumericFilter(
                rectanglesNumberField, minWField, maxWField,
                minHField, maxHField, boxLField
        );

        initializeComboBoxes();
        algorithmCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateAlgorithmUI());
        updateAlgorithmUI();
    }

    private void initializeComboBoxes() {
        algorithmCombo.setItems(FXCollections.observableArrayList("Greedy", "Local Search"));
        algorithmCombo.getSelectionModel().selectFirst();

        selectionCombo.setItems(FXCollections.observableArrayList("Area-based", "Height-based"));
        selectionCombo.getSelectionModel().selectFirst();

        neighborhoodCombo.setItems(FXCollections.observableArrayList(
                "Geometry-based", "Partially Overlapped", "Rule-based"
        ));
        neighborhoodCombo.getSelectionModel().selectFirst();
    }


    private void updateAlgorithmUI() {
        boolean isLocal = "Local Search".equals(algorithmCombo.getValue());
        selectionCombo.setDisable(isLocal);
        neighborhoodCombo.setDisable(!isLocal);
    }

    @FXML
    public void handleRun() {
        solutionPane.getChildren().clear();

        AlgorithmRunner.AlgorithmConfig config = new AlgorithmRunner.AlgorithmConfig();
        config.rectangleCount = InputValidator.parseField(rectanglesNumberField, 100);
        config.minWidth = InputValidator.parseField(minWField, 1);
        config.maxWidth = InputValidator.parseField(maxWField, 50);
        config.minHeight = InputValidator.parseField(minHField, 1);
        config.maxHeight = InputValidator.parseField(maxHField, 50);
        config.boxLength = InputValidator.parseField(boxLField, 100);
        config.algorithm = algorithmCombo.getValue();
        config.neighborhood = neighborhoodCombo.getValue();
        config.selectionStrategy = selectionCombo.getValue();

        this.algorithmRunner.runAlgorithm(config, this::updateUIWithResults);
    }

    public void handleGenerateInstances() {
        AlgorithmRunner.AlgorithmConfig config = new AlgorithmRunner.AlgorithmConfig();
        config.rectangleCount = InputValidator.parseField(rectanglesNumberField, 1000);
        config.minWidth = InputValidator.parseField(minWField, 1);
        config.maxWidth = InputValidator.parseField(maxWField, 50);
        config.minHeight = InputValidator.parseField(minHField, 1);
        config.maxHeight = InputValidator.parseField(maxHField, 50);
        config.boxLength = InputValidator.parseField(boxLField, 100);
        config.algorithm = algorithmCombo.getValue();
        config.neighborhood = neighborhoodCombo.getValue();
        config.selectionStrategy = selectionCombo.getValue();

        this.testInstances = this.algorithmRunner.generateTestInstances(config);
        int numberGeneratedIstances = config.rectangleCount;

        this.updateUIGenerateInstances(numberGeneratedIstances);
    }

    private void updateUIWithResults(AlgorithmRunner.AlgorithmResult result) {
        visualizer.drawBoxes(result.boxes);
        runtimeLabel.setText("Runtime: " + result.runtime);
        boxesLabel.setText("Used Boxes: " + result.totalBoxes);
        rectanglesLabel.setText("Rectangles: " + result.totalRectangles);
    }

    private void updateUIGenerateInstances(int numbInstances) {
        // public Button generateInstancesButton;
        generatedInstancesCount.setText("Generated Instances: " + numbInstances);
    }
}

