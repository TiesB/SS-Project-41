/**
 * Created by Ties on 8-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Player;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.online.Protocol;

import java.util.ArrayList;

public class OnlinePlayer extends Player {
    private ClientHandler clientHandler;

    public OnlinePlayer(ClientHandler clientHandler, OnlineGame game) {
        super(clientHandler.getPlayerName(), game);
        this.clientHandler = clientHandler;
    }

    public void sendMessage(String message) {
        clientHandler.sendMessageToClient(message);
    }

    @Override
    public void prepareForGame() {
        super.prepareForGame();
        sendDeck();
    }

    public void sendDeck() {
        sendNewTiles(deck);
    }

    public void sendNewTiles(ArrayList<Tile> tiles) {
        String message = Protocol.SERVER_NEW_STONES_COMMAND;
        for (Tile tile : tiles) {
            if (tile != null) {
                message += " " + tile.toProtocolForm();
            }
        }

        sendMessage(message);
    }
}
