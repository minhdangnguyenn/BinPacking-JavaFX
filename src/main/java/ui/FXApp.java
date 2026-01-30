package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../../../Documents/repo/binpacking--java-version/src/main.fxml"));
        Scene scene = new Scene(loader.load(), 1600, 1000); // width x height

        stage.setTitle("Bin Packing Problem");
        stage.setScene(scene);
        stage.show();
    }
}
