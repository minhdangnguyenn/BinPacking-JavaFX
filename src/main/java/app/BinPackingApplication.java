package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BinPackingApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("binpacking.fxml"));
        Scene scene = new Scene(loader.load(), 1250, 900); // width x height

        stage.setTitle("Bin Packing Problem");
        stage.setScene(scene);
        stage.show();
    }
}
