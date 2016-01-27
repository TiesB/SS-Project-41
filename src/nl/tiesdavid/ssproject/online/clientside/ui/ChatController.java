/**
 * Created by Ties on 24-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ui;

import javafx.application.Application;
import javafx.application.Platform;
import nl.tiesdavid.ssproject.online.Protocol;
import nl.tiesdavid.ssproject.online.clientside.ClientController;
import nl.tiesdavid.ssproject.online.clientside.ui.guiviews.ChatConsole;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ChatController extends Thread implements Observer {
    private ClientController clientController;
    private ChatConsole chatConsole;

    public ChatController(ClientController clientController) {
        this.clientController = clientController;
        this.chatConsole = new ChatConsole(); // TODO: 25-1-2016 Make messageList not crash when size is extremely big.
    }

    public void sendGeneralMessage(String message) {
        clientController.sendGeneralChatCommand(message);
    }

    public void sendPrivateMessage(String recipient, String message) {
        clientController.sendPrivateChatCommand(recipient, message);
    }

    public void close() {
        clientController.close();
    }

    @Override
    public void run() {
        chatConsole.setChatController(this);
        Application.launch(chatConsole.getClass());
    }

    private void receiveDisconnectCommand(String[] messageParts) {
        if (messageParts.length == 2) {
            Platform.runLater(() -> {
                chatConsole.removePlayer(messageParts[1]);
            });
        }
    }

    private void receiveJoinCommand(String[] messageParts) {
        ArrayList<String> players = new ArrayList<>();
        for (int i = 1; i < messageParts.length; i++) {
            String part = messageParts[i];
            if (!part.toLowerCase().equals(Protocol.CHAT_FEATURE)
                    && !part.toLowerCase().equals(Protocol.CHALLENGE_FEATURE)
                    && !part.toLowerCase().equals(Protocol.LEADERBOARD_FEATURE)) {
                players.add(part);
            }
        }
        Platform.runLater(() -> {
            for (String player : players) {
                if (!player.equals(clientController.getUsername())) {
                    chatConsole.addPlayer(player);
                }
            }
        });
    }

    private void receivePlayersCommand(String[] messageParts) {
        Platform.runLater(() -> {
            for (int i = 1; i < messageParts.length; i++) {
                if (!messageParts[i].equals(clientController.getUsername())) {
                    chatConsole.addPlayer(messageParts[i]);
                }
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis());

            String[] parts = ((String) arg).split(" ");
            String command = parts[0];

            String message = "";
            if (parts.length >= 3) {
                for (int i = 2; i < parts.length - 1; i++) {
                    message += parts[i] + " ";
                }
                message += parts[parts.length - 1];
            }

            final String m = message;

            switch (command) {
                case Protocol.SERVER_WELCOME_COMMAND:
                    Platform.runLater(() -> {
                        chatConsole.connected();
                    });
                    break;
                case Protocol.SERVER_DISCONNECT_COMMAND:
                    receiveDisconnectCommand(parts);
                    break;
                case Protocol.SERVER_JOIN_COMMAND:
                    receiveJoinCommand(parts);
                    break;
                case Protocol.SERVER_PLAYERS_COMMAND:
                    receivePlayersCommand(parts);
                    break;
                case Protocol.SERVER_GENERAL_CHAT_MESSAGE_COMMAND:
                    Platform.runLater(() -> {
                        chatConsole.addGeneralMessage(timeStamp, parts[1], m); //TODO
                    });
                    break;
                case Protocol.SERVER_PRIVATE_CHAT_MESSAGE_COMMAND:
                    Platform.runLater(() -> {
                        chatConsole.addPrivateMessage(timeStamp, parts[1], m);
                    });
                    break;
            }
        }
    }
}
