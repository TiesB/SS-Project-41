/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.exceptions.ExistingNameException;

import java.util.HashMap;
import java.util.Map;

public class Lobby {
    public static final int NAME_ALREADY_EXISTS_ERROR = -42;
    public static final String JOIN_MESSAGE = "joinlobby";

    private Map<ClientHandler, String> clients;

    public Lobby() {
        this.clients = new HashMap<>();
    }

    private void sendMessageToAllClients(String message) {
        for (ClientHandler client : clients.keySet()) {
            client.sendMessageToClient(message);
        }
    }

    public void connectClient(ClientHandler client) throws ExistingNameException {
        String name = client.getPlayerName();
        if (clients.values().contains(name)) {
            throw new ExistingNameException(client.getPlayerName());
        }

        String options = client.getOptionsString();

        clients.put(client, client.getPlayerName());
        sendMessageToAllClients(JOIN_MESSAGE + " " + name + );
    }

    public void disconnectClient(ClientHandler client) {
        clients.remove(client);
    }
}
