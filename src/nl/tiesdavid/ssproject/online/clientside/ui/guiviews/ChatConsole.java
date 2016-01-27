/**
 * Created by Ties on 24-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ui.guiviews;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nl.tiesdavid.ssproject.online.clientside.ui.ChatController;

import java.util.concurrent.locks.ReentrantLock;

public class ChatConsole extends Application {
    private static ChatController chatController;

    private ReentrantLock lock = new ReentrantLock();

    private static ObservableList<String> messagesList;
    private static ObservableList<String> playersList;

    private static ListView<String> chatOutputView;
    private static ListView<String> playersView;
    private static TextField input;
    private static Button button;

    public static void setChatController(ChatController newChatController) {
        chatController = newChatController;
    }

    public void sendMessage() {
        lock.lock();
        Platform.runLater(() -> {
            String message = input.getText();

            ObservableList<String> recipients = playersView.getSelectionModel().getSelectedItems();
            if (recipients.size() > 0) {
                for (String recipient : recipients) {
                    chatController.sendPrivateMessage(recipient, message);
                }
            } else {
                chatController.sendGeneralMessage(message);
            }
        });
        lock.unlock();
    }

    private void handleAddMessage() {
        lock.lock();
        Platform.runLater(() -> {
            if (messagesList.size() == 1) {
                if (messagesList.get(0).equals("Connected!")) {
                    messagesList.remove(0);
                }
            }

            chatOutputView.scrollTo(Integer.MAX_VALUE);
        });
        lock.unlock();
    }

    public void addGeneralMessage(String timeStamp, String sender, String message) {
        lock.lock();
        Platform.runLater(() -> {
            String line = timeStamp + " - " + sender + ": " + message;
            messagesList.add(line);
        });
        lock.unlock();

        handleAddMessage();
    }

    public void addPrivateMessage(String timeStamp, String sender, String message) {
        lock.lock();
        Platform.runLater(() -> {
            String line = "[PRIVATE] " + timeStamp + " - " + sender + ": " + message;
            messagesList.add(line);
        });
        lock.unlock();

        handleAddMessage();
    }

    public void addPlayer(String playerName) {
        lock.lock();
        Platform.runLater(() -> {
            playersList.add(playerName);
        });
        lock.unlock();
    }

    public void removePlayer(String playerName) {
        lock.lock();
        Platform.runLater(() -> {
            playersList.remove(playerName);
        });
        lock.unlock();
    }

    public void connected() {
        lock.lock();
        Platform.runLater(() -> {
            messagesList.remove(0);
            messagesList.add("Connected!");
            input.requestFocus();
        });
        lock.unlock();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        lock.lock();
        primaryStage.setTitle("Chat console");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(() -> {
                    if (chatController != null) {
                        chatController.close();
                    }
                });
            }
        });
        primaryStage.getIcons().add(new Image(getClass().getResource("/icon.png").toString()));
        Group root = new Group();
        Scene scene = new Scene(root, 640, 480, Color.WHITESMOKE);

        messagesList = FXCollections.observableArrayList();
        playersList = FXCollections.observableArrayList();

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        chatOutputView = new ListView<>();
        chatOutputView.setPrefWidth(400);
        chatOutputView.setPrefHeight(400);
        GridPane.setHalignment(chatOutputView, HPos.LEFT);
        chatOutputView.setEditable(false);
        chatOutputView.setItems(messagesList);
        messagesList.add("Waiting for connection...");

        playersView = new ListView<>();
        playersView.setPrefWidth(220);
        playersView.setPrefHeight(400);
        GridPane.setHalignment(playersView, HPos.LEFT);
        playersView.setEditable(false);
        playersView.setItems(playersList);

        input = new TextField();
        input.setPrefWidth(400);
        GridPane.setHalignment(input, HPos.LEFT);
        GridPane.setValignment(input, VPos.CENTER);
        input.setPromptText("Input chat message here.");

        button = new Button("Send!");
        button.setPrefWidth(100);
        button.setPrefHeight(50);
        GridPane.setHalignment(button, HPos.CENTER);
        GridPane.setValignment(button, VPos.CENTER);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.runLater(() -> {
                    sendMessage();
                });
            }
        });

        gridPane.add(chatOutputView, 0, 0);
        gridPane.add(input, 0, 1);
        gridPane.add(playersView, 1, 0);
        gridPane.add(button, 1, 1);

        root.getChildren().add(gridPane);
        primaryStage.setScene(scene);
        lock.unlock();
        primaryStage.show();
    }
}
