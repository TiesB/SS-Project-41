package nl.tiesdavid.ssproject.online.clientside.ui.guiviews;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.tiesdavid.ssproject.online.clientside.ClientController;

import java.util.Observable;
import java.util.Observer;

public class StartupController extends Application implements Observer {
    @FXML public TextField usernameInput;
    @FXML public TextField serverIPInput;
    @FXML public TextField serverPortInput;

    private ClientController clientController;

    private Stage stage;

    @FXML public void onJoinButtonClick(ActionEvent event) {

        String username = usernameInput.getText();
        String serverIP = serverIPInput.getText();
        String serverPortString = serverPortInput.getText();
        int serverPort;
        try {
            serverPort = Integer.parseInt(serverPortString);
        } catch (NumberFormatException e) {
            serverPort = -1;
        }

        clientController.connect(username, serverIP, serverPort);

        Platform.exit();
    }

    public void startUI() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("/layout/startup_dialog.fxml"));
        primaryStage.setTitle("Qwirkle Client");
        Scene scene = new Scene(root, 480, 320);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
        System.out.println("CC set");
        System.out.println(clientController);
    }
}
