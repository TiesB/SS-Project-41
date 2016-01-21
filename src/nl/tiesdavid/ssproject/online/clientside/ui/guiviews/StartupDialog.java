package nl.tiesdavid.ssproject.online.clientside.ui.guiviews;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import nl.tiesdavid.ssproject.online.clientside.ClientController;

import java.util.ArrayList;
import java.util.Optional;

public class StartupDialog extends Application {
    private ClientController clientController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create the custom dialog.
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Qwirkle Client");
        dialog.setGraphic(new ImageView(this.getClass().getResource("/logo.png").toString()));

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Join!", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        TextField serverIP = new TextField();
        serverIP.setPromptText("Server IP");
        TextField serverPort = new TextField();
        serverPort.setPromptText("Server Port");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Server IP:"), 0, 1);
        grid.add(serverIP, 1, 1);
        grid.add(new Label("Server Port:"), 0, 2);
        grid.add(serverPort, 1, 2);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a username-serverIP-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                ArrayList<String> values = new ArrayList<>();
                values.add(username.getText());
                values.add(serverIP.getText());
                values.add(serverPort.getText());
                return values;
            }
            return null;
        });

        Optional<ArrayList<String>> result = dialog.showAndWait();

        result.ifPresent(results -> clientController.parseUIStartupResult(results));
    }

    public void startUI(ClientController newClientController) {
        this.clientController = newClientController;
        launch();
    }
}
