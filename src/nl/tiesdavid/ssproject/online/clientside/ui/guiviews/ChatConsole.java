/**
 * Created by Ties on 24-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ui.guiviews;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nl.tiesdavid.ssproject.online.clientside.ui.ChatController;

import java.util.Observable;
import java.util.Observer;

public class ChatConsole extends Application implements Observer {
    private ChatController chatController;

    private static GridPane gridPane;
    private static ListView<String> output;
    private static ObservableList<String> items;
    private static TextField input;

    public void startUI(ChatController newChatController) {
        this.chatController = newChatController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Chat console");
        primaryStage.getIcons().add(new Image(getClass().getResource("/icon.png").toString()));
        Group root = new Group();
        Scene scene = new Scene(root, 640, 480, Color.WHITESMOKE);

        items = FXCollections.observableArrayList();

        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        output = new ListView<>();
        output.setPrefWidth(400);
        output.setPrefHeight(400);
        GridPane.setHalignment(output, HPos.LEFT);
        output.setEditable(false);
        output.setItems(items);
        items.add("Waiting to connect...");
        //output.setPrefRowCount(10);

        input = new TextField();
        input.setPrefWidth(400);
        GridPane.setHalignment(input, HPos.LEFT);
        input.setPromptText("Input chat message here.");

        gridPane.add(output, 0, 0);
        gridPane.add(input, 0, 1);

        root.getChildren().add(gridPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            Platform.runLater(() -> {
                items.add((String) arg);
            });
        }
    }
}
