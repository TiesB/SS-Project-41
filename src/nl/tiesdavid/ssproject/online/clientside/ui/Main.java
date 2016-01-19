package nl.tiesdavid.ssproject.online.clientside.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static final String VERSION = "v0.70";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/layout/startup_dialog.fxml"));
        primaryStage.setTitle("Qwirkle Client " + VERSION);
        primaryStage.setScene(new Scene(root, 480, 320));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
