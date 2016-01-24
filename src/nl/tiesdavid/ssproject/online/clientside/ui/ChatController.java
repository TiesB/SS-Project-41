/**
 * Created by Ties on 24-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ui;

import javafx.application.Application;
import nl.tiesdavid.ssproject.online.Protocol;
import nl.tiesdavid.ssproject.online.clientside.ClientController;
import nl.tiesdavid.ssproject.online.clientside.ui.guiviews.ChatConsole;

import java.util.Observable;
import java.util.Observer;

public class ChatController extends Thread implements Observer {
    private ClientController clientController;
    private ChatConsole chatConsole;

    public ChatController(ClientController clientController) {
        this.clientController = clientController;
        this.chatConsole = new ChatConsole();
    }

    @Override
    public void run() {
        Application.launch(ChatConsole.class, "");
        chatConsole.startUI(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String[] parts = ((String) arg).split(" ");
            String command = parts[0];
            if (command.equals(Protocol.SERVER_GENERAL_CHAT_MESSAGE_COMMAND)) {

            } else if (command.equals(Protocol.SERVER_PRIVATE_CHAT_MESSAGE_COMMAND)) {

            }
        }
    }

    public ChatConsole getChatConsole() {
        return chatConsole;
    }
}
