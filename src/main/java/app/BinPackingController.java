package app;


import algorithm.AlgorithmType;
import algorithm.core.greedy.ordering.raw.GreedyOrderingType;
import algorithm.core.localsearch.neighborhood.raw.NeighborhoodType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import algorithm.model.Rectangle;
import algorithm.AlgorithmRunner;
import ui.BoxVisualizer;
import ui.InputValidator;
import utils.Utils;

import java.util.ArrayList;

public class BinPackingController {

    public Label greedyBoxesLabel;
    public Label rectanglesLabel;
    public Button runButton;
    public Label runtimeLabel;
    public ArrayList<Rectangle> rectangles = new ArrayList<>();
    public Button generateInstancesButton;
    public Label generatedInstancesCount;
    public Label localSearchBoxesLabel;
    public CheckBox showRectangleID;
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
    private final static int MAXITERATION = 100; // max iteration for local search

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
        algorithmCombo.setItems(
                FXCollections.observableArrayList(
                    AlgorithmType.GREEDY.name(),
                    AlgorithmType.LOCALSEARCH.name()
                )
        );
        algorithmCombo.getSelectionModel().selectFirst();

        selectionCombo.setItems(FXCollections.observableArrayList(
                GreedyOrderingType.LARGEST_AREA_FIRST.name(),
                GreedyOrderingType.LARGEST_SIDE_FIRST.name()
        ));

        selectionCombo.getSelectionModel().selectFirst();

        neighborhoodCombo.setItems(FXCollections.observableArrayList(
                NeighborhoodType.GEOMETRY.name(),
                NeighborhoodType.RULEBASED.name(),
                NeighborhoodType.OVERLAP.name()
        ));

        neighborhoodCombo.getSelectionModel().selectFirst();
    }

    private void updateAlgorithmUI() {
        boolean isLocal = AlgorithmType.LOCALSEARCH.name().equals(algorithmCombo.getValue());
        selectionCombo.setDisable(isLocal);
        neighborhoodCombo.setDisable(!isLocal);
    }

    @FXML
    public void handleRun() {
        solutionPane.getChildren().clear();
        AlgorithmRunner.AlgorithmConfig config = parseConfig();

        this.algorithmRunner.runAlgorithm(
                config,
                result->updateUIWithResults(result, showRectangleID.isSelected()),
                MAXITERATION);
    }

    public void handleGenerateInstances() {
        AlgorithmRunner.AlgorithmConfig config = parseConfig();

        this.rectangles = this.algorithmRunner.generateTestInstances(config);

        this.updateUIGenerateInstances(config.rectangleCount);
    }

    private void updateUIWithResults(AlgorithmRunner.AlgorithmResult result, boolean showRectangleID) {
        visualizer.drawBoxes(result.boxes, showRectangleID);
        runtimeLabel.setText("Runtime: " + result.runtime);
        
        // Show greedy boxes count
        greedyBoxesLabel.setText("Greedy Boxes: " + result.totalGreedyBoxes);
        
        // Show local search boxes count (if available)
        if (result.totalLocalSearchBoxes > 0) {
            localSearchBoxesLabel.setText("Local Search Boxes: " + result.totalLocalSearchBoxes + 
                    " (Saved: " + (result.numBadBoxes - result.totalLocalSearchBoxes)
            + "\n Init greedy runtime: " + result.initRuntime);
            localSearchBoxesLabel.setVisible(true);
        } else {
            localSearchBoxesLabel.setText("");
            localSearchBoxesLabel.setVisible(false);
        }
        
        rectanglesLabel.setText("Rectangles: " + result.totalRectangles);
    }

    private void updateUIGenerateInstances(int numbInstances) {
        generatedInstancesCount.setText("Generated Instances: " + numbInstances);
    }

    private AlgorithmRunner.AlgorithmConfig parseConfig() {
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
        Utils.validConfig(config);

        return config;
    }
}

