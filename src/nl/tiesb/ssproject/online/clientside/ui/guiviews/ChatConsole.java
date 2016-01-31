/**
 * Created by Ties on 24-1-2016.
 */
package nl.tiesb.ssproject.online.clientside.ui.guiviews;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nl.tiesb.ssproject.online.clientside.ui.ChatController;

import java.text.SimpleDateFormat;
import java.util.concurrent.locks.ReentrantLock;

public class ChatConsole extends Application {
    public static final String WAITING_MESSAGE = "Waiting for connection...";
    public static final String CONNECTED_MESSAGE = "Conected!";
    public static final String INPUT_PROMPT_TEXT = "Input chat message here.";
    public static final String SEND_BUTTON_TEXT = "Send!";

    private static ChatController chatController;
    private static String username;

    private final ReentrantLock lock = new ReentrantLock();

    private static ObservableList<String> messagesList;
    private static ObservableList<String> playersList;

    private static ListView<String> chatOutputView;
    private static ListView<String> playersView;
    private static TextField input;

    public static void setChatController(ChatController newChatController) {
        chatController = newChatController;
    }

    public static void setUsername(String newUsername) {
        username = newUsername;
    }

    private void sendMessage() {
        lock.lock();
        Platform.runLater(() -> {
            String message = input.getText();

            if (!message.equals("")) {
                ObservableList<String> recipients = playersView.getSelectionModel().getSelectedItems();
                if (recipients.size() > 0) {
                    for (String recipient : recipients) {
                        if (!recipient.equals("You")) {
                            chatController.sendPrivateMessage(recipient, message);
                            addSentPrivateMessage(recipient, message);
                        }
                    }
                } else {
                    chatController.sendGeneralMessage(message);
                }
            }
            input.setText("");
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

    public void addGeneralMessage(String sender, String message) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis());

        lock.lock();
        Platform.runLater(() -> {
            // Replace the sender with "You" if user is sender.
            String line = timeStamp + " - " + (username != null && sender.equals(username) ? "You" : sender) + ": " + message;
            messagesList.add(line);
        });
        lock.unlock();

        handleAddMessage();
    }

    public void addReceivedPrivateMessage(String sender, String message) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis());

        lock.lock();
        Platform.runLater(() -> {
            String line = "[PRIVATE] " + timeStamp + " - " + sender + ": " + message;
            messagesList.add(line);
        });
        lock.unlock();

        handleAddMessage();
    }

    public void addSentPrivateMessage(String recipient, String message) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis());

        lock.lock();
        Platform.runLater(() -> {
            String line = "[PRIVATE] " + timeStamp + " - You to " + recipient + ": " + message;
            messagesList.add(line);
        });
        lock.unlock();

        handleAddMessage();
    }

    public void addPlayer(String playerName) {
        lock.lock();
        Platform.runLater(() -> {
            if (!playersList.contains(playerName)) {
                playersList.add(playerName);
            }
        });
        lock.unlock();
    }

    public void removePlayer(String playerName) {
        lock.lock();
        Platform.runLater(() -> {
            playersList.remove(playerName);
            if (playersList.isEmpty()) {
                playersList.add("");
            }
        });
        lock.unlock();
    }

    public void connected(String newUsername) {
        lock.lock();
        Platform.runLater(() -> {
            messagesList.remove(WAITING_MESSAGE);
            messagesList.add(CONNECTED_MESSAGE);
            username = newUsername;
            input.requestFocus();
        });
        lock.unlock();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        lock.lock();
        primaryStage.setTitle("Chat console");
        primaryStage.setOnCloseRequest(event -> Platform.runLater(() -> {
            if (chatController != null) {
                chatController.close();
            }
        }));
        primaryStage.getIcons().add(new Image(getClass().getResource("icon.png").toString()));
        Group root = new Group();
        Scene scene = new Scene(root, 640, 480, Color.WHITESMOKE);

        messagesList = FXCollections.observableArrayList();
        playersList = FXCollections.observableArrayList();

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // Chat messages list
        chatOutputView = new ListView<>();
        chatOutputView.setPrefWidth(400);
        chatOutputView.setPrefHeight(400);
        GridPane.setHalignment(chatOutputView, HPos.LEFT);
        chatOutputView.setEditable(false);
        chatOutputView.setItems(messagesList);
        messagesList.add(WAITING_MESSAGE);

        // Players list
        playersView = new ListView<>();
        playersView.setPrefWidth(220);
        playersView.setPrefHeight(400);
        GridPane.setHalignment(playersView, HPos.LEFT);
        playersView.setEditable(false);
        playersView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                playersView.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    MultipleSelectionModel selectionModel = playersView.getSelectionModel();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                    } else {
                        selectionModel.select(index);
                    }
                    event.consume();
                }
            });
            return cell;
        });
        playersView.setItems(playersList);
        playersList.add("You");

        // Input field
        input = new TextField();
        input.setPrefWidth(400);
        GridPane.setHalignment(input, HPos.LEFT);
        GridPane.setValignment(input, VPos.CENTER);
        input.setPromptText(INPUT_PROMPT_TEXT);
        input.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Platform.runLater(this::sendMessage);
                event.consume();
            }
        });

        // Send button
        Button button = new Button(SEND_BUTTON_TEXT);
        button.setPrefWidth(100);
        button.setPrefHeight(50);
        GridPane.setHalignment(button, HPos.CENTER);
        GridPane.setValignment(button, VPos.CENTER);
        button.setOnAction(event -> Platform.runLater(this::sendMessage));

        // Construct grid
        gridPane.add(chatOutputView, 0, 0);
        gridPane.add(input, 0, 1);
        gridPane.add(playersView, 1, 0);
        gridPane.add(button, 1, 1);

        root.getChildren().add(gridPane);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        lock.unlock();
        primaryStage.show();


        // Usage alert.
        Alert usageAlert = new Alert(Alert.AlertType.INFORMATION);
        Stage alertStage = (Stage) usageAlert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResource("/icon.png").toString()));
        usageAlert.setTitle("Usage Information");
        usageAlert.setGraphic(new ImageView(getClass().getResource("/logo.png").toString()));
        usageAlert.setHeaderText("");
        usageAlert.setContentText("Wait for the list to show \"" + CONNECTED_MESSAGE + "\"." + System.lineSeparator() +
                "Now you can type a message in the lower input box, and send it by pressing the \"" + SEND_BUTTON_TEXT + "\" button." + System.lineSeparator() +
                System.lineSeparator() +
                "You can send private messages by selecting the recipient in the list on the right." + System.lineSeparator() +
                "To send general messages again, deselect the name you are sending private messages to."
        );

        usageAlert.show();
    }
}
