/**
 * Created by Ties on 24-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ui;

import javafx.application.Application;
import nl.tiesdavid.ssproject.online.Protocol;
import nl.tiesdavid.ssproject.online.clientside.ClientController;
import nl.tiesdavid.ssproject.online.clientside.ui.guiviews.ChatConsole;

import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

public class ChatController extends Thread implements Observer {
    private ClientController clientController;
    private ChatConsole chatConsole;

    public ChatController(ClientController clientController) {
        this.clientController = clientController;
        this.chatConsole = new ChatConsole();
    }

    public void sendGeneralMessage(String message) {
        clientController.sendGeneralChatCommand(message);
    }

    public void sendPrivateMessage(String recipient, String message) {
        clientController.sendPrivateChatCommand(recipient, message);
    }

    @Override
    public void run() {
        Application.launch(ChatConsole.class, "");
        chatConsole.startUI(this);
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

            if (command.equals(Protocol.SERVER_WELCOME_COMMAND)) {
                chatConsole.connected();
            } else if (command.equals(Protocol.SERVER_GENERAL_CHAT_MESSAGE_COMMAND)) {
                chatConsole.addGeneralMessage(timeStamp, parts[1], message); //TODO
            } else if (command.equals(Protocol.SERVER_PRIVATE_CHAT_MESSAGE_COMMAND)) {
                chatConsole.addPrivateMessage(timeStamp, parts[1], message);
            }
        }
    }

    public ChatConsole getChatConsole() {
        return chatConsole;
    }
}
