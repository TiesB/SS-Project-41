/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.exceptions.ExistingNameException;
import nl.tiesdavid.ssproject.game.exceptions.NonexistingPlayerException;
import nl.tiesdavid.ssproject.game.exceptions.UnsupportedOptionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lobby {
    private static final boolean DEBUG = true;

    private static final String[] OPTIONS = new String[]{"chat"};

    public static final int NAME_ALREADY_EXISTS_ERROR = -42;
    public static final String WELCOME_COMMAND = "hellofromtheotherside";
    public static final String JOIN_COMMAND = "joinlobby";
    public static final String GENERAL_CHAT_MESSAGE_COMMAND = "msg";
    public static final String PRIVATE_CHAT_MESSAGE_COMMAND = "msgpm";

    private Map<String, ClientHandler> namesWithClients;
    private Map<OnlineGame, ArrayList<ClientHandler>> gamesWithClients;
    private ArrayList<ClientHandler> clientsInLobby;

    public Lobby() {
        this.namesWithClients = new HashMap<>();
        this.gamesWithClients = new HashMap<>();
        this.clientsInLobby = new ArrayList<>();
    }

    public void sendGeneralChatMessage(ClientHandler sender, String message) {
        if (DEBUG) {
            System.out.println("Sending message: " + sender.getPlayerName() + " " + message);
        }
        ArrayList<ClientHandler> receivers;
        if (sender.isInGame()) {
            OnlineGame game = sender.getCurrentGame();
            receivers = gamesWithClients.get(game);
        } else {
            receivers = clientsInLobby;
        }

        for (ClientHandler client : receivers) {
            if (client != sender) { //TODO: Decide whether sender should receive own message.
                client.sendMessageToClient(GENERAL_CHAT_MESSAGE_COMMAND + " " + sender.getPlayerName() + " " + message);
            }
        }
    }

    public void sendPrivateChatMessage(ClientHandler sender, String receiver, String message) throws UnsupportedOptionException, NonexistingPlayerException {
        if (DEBUG) {
            System.out.println("Sending private message " + sender.getPlayerName() + " " + receiver + " " + message);
        }

        ClientHandler receiverHandler = getClientHandlerByName(receiver);
        if (!receiverHandler.hasOption(ClientHandler.CHAT_OPTION)) {
            throw new UnsupportedOptionException(receiver, ClientHandler.CHAT_OPTION);
        }

        receiverHandler.sendMessageToClient(PRIVATE_CHAT_MESSAGE_COMMAND + " " + sender.getPlayerName() + " " + message);
    }

    private void sendMessageToAllClients(String message) {
        if (DEBUG) {
            System.out.println("Sending message to all clients: " + message);
        }

        for (ClientHandler client : namesWithClients.values()) {
            client.sendMessageToClient(message);
        }
    }

    public void connectClient(ClientHandler client) throws ExistingNameException {
        String name = client.getPlayerName();
        if (namesWithClients.containsKey(name)) {
            throw new ExistingNameException(client.getPlayerName());
        }

        String options = client.getOptionsString();

        namesWithClients.put(name, client);
        clientsInLobby.add(client);
        sendWelcomeMessage(client);
        sendMessageToAllClients(JOIN_COMMAND + " " + name + " " + options);
    }

    public void disconnectClient(ClientHandler client) {
        namesWithClients.remove(client.getPlayerName());
    }

    private void sendWelcomeMessage(ClientHandler client) {
        String message = WELCOME_COMMAND;
        for (String option : OPTIONS) {
            message += " " + option;
        }

        client.sendMessageToClient(message);
    }

    private ClientHandler getClientHandlerByName(String name) throws NonexistingPlayerException {
        ClientHandler handler = namesWithClients.get(name);

        if (handler == null) {
            throw new NonexistingPlayerException(name);
        }

        return handler;
    }
}
