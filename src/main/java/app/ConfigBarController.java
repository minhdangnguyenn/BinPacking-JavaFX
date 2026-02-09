package app;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import algorithm.AlgorithmType;
import algorithm.core.greedy.ordering.raw.GreedyOrderingType;
import algorithm.core.localsearch.neighborhood.raw.NeighborhoodType;
import ui.InputValidator;

public class ConfigBarController {

    @FXML public TextField rectanglesNumberField;
    @FXML public TextField minWField;
    @FXML public TextField maxWField;
    @FXML public TextField minHField;
    @FXML public TextField maxHField;
    @FXML public TextField boxLField;
    @FXML public Label generatedInstancesCount;
    @FXML public Button generateInstancesButton;
    @FXML public ComboBox<String> algorithmCombo;
    @FXML public ComboBox<String> neighborhoodCombo;
    @FXML public ComboBox<String> selectionCombo;
    @FXML public CheckBox showRectangleID;
    @FXML public Button runButton;
    @FXML public Label runtimeLabel;
    @FXML public Label greedyBoxesLabel;
    @FXML public Label localSearchBoxesLabel;
    @FXML public Label rectanglesLabel;

    private BinPackingController mainController;

    @FXML
    public void initialize() {
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
                NeighborhoodType.PERMUTATION.name(),
                NeighborhoodType.OVERLAP.name()
        ));

        neighborhoodCombo.getSelectionModel().selectFirst();
    }

    private void updateAlgorithmUI() {
        boolean isLocal = AlgorithmType.LOCALSEARCH.name().equals(algorithmCombo.getValue());
        selectionCombo.setDisable(isLocal);
        neighborhoodCombo.setDisable(!isLocal);
    }

    public void setMainController(BinPackingController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void handleGenerateInstances() {
        if (mainController != null) {
            mainController.handleGenerateInstances();
        }
    }

    @FXML
    public void handleRun() {
        if (mainController != null) {
            mainController.handleRun();
        }
    }
}
