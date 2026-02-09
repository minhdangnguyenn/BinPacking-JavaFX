package app;

import algorithm.Controller;
import algorithm.Controller;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import algorithm.model.Rectangle;
import ui.BoxVisualizer;
import ui.InputValidator;
import utils.Utils;

import java.util.ArrayList;

public class BinPackingController {

    @FXML private Pane solutionPane;
    @FXML private ConfigBarController configBarController;

    public ArrayList<Rectangle> rectangles = new ArrayList<>();
    private BoxVisualizer visualizer;
    private final Controller controller = new Controller();
    private final static int MAXITERATION = 100;

    @FXML
    public void initialize() {
        visualizer = new BoxVisualizer(solutionPane);
        
        // Connect the config bar controller to this main controller
        if (configBarController != null) {
            configBarController.setMainController(this);
        }
    }

    @FXML
    public void handleRun() {
        solutionPane.getChildren().clear();
        Controller.Config config = parseConfig();

        this.controller.runAlgorithm(
                config,
                result -> updateUIWithResults(result, configBarController.showRectangleID.isSelected()),
                MAXITERATION);
    }

    public void handleGenerateInstances() {
        Controller.Config config = parseConfig();

        this.controller.generateInstances(config);

        this.updateUIGenerateInstances(config.rectangleCount);
    }

    private void updateUIWithResults(Controller.AlgorithmResult result, boolean showRectangleID) {
        visualizer.drawBoxes(result.boxes, showRectangleID);
        configBarController.runtimeLabel.setText("Runtime: " + result.runtime);
        
        // Show greedy boxes count
        configBarController.greedyBoxesLabel.setText("Greedy Boxes: " + result.totalGreedyBoxes);
        
        // Show local search boxes count (if available)
        if (result.totalLocalSearchBoxes > 0) {
            configBarController.localSearchBoxesLabel.setText("Local Search Boxes: " + result.totalLocalSearchBoxes + 
                    " (Saved: " + (result.numBadBoxes - result.totalLocalSearchBoxes)+")"
            + "\n Init greedy runtime: " + result.initRuntime);
            configBarController.localSearchBoxesLabel.setVisible(true);
        } else {
            configBarController.localSearchBoxesLabel.setText("");
            configBarController.localSearchBoxesLabel.setVisible(false);
        }
        
        configBarController.rectanglesLabel.setText("Rectangles: " + result.totalRectangles);
    }

    private void updateUIGenerateInstances(int numbInstances) {
        configBarController.generatedInstancesCount.setText("Generated Instances: " + numbInstances);
    }

    private Controller.Config parseConfig() {
        Controller.Config config = new Controller.Config();

        config.rectangleCount = InputValidator.parseField(configBarController.rectanglesNumberField, 1000);
        config.minWidth = InputValidator.parseField(configBarController.minWField, 1);
        config.maxWidth = InputValidator.parseField(configBarController.maxWField, 50);
        config.minHeight = InputValidator.parseField(configBarController.minHField, 1);
        config.maxHeight = InputValidator.parseField(configBarController.maxHField, 50);
        config.boxLength = InputValidator.parseField(configBarController.boxLField, 100);
        config.algorithm = configBarController.algorithmCombo.getValue();
        config.neighborhood = configBarController.neighborhoodCombo.getValue();
        config.selectionStrategy = configBarController.selectionCombo.getValue();
        Utils.validConfig(config);

        return config;
    }
}
