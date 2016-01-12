/**
 * Created by Ties on 8-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Move;
import nl.tiesdavid.ssproject.game.Player;
import nl.tiesdavid.ssproject.game.Tile;

import java.util.ArrayList;

public class OnlinePlayer extends Player {
    public static final String NEW_STONES_COMMAND = "newstones";
    private ClientHandler clientHandler;

    public OnlinePlayer(ClientHandler clientHandler, OnlineGame game) {
        super(clientHandler.getPlayerName(), game);
        this.clientHandler = clientHandler;
    }

    public void sendMessage(String message) {
        clientHandler.sendMessageToClient(message);
    }

    public void sendDeck() {
        System.out.println("hoi");
        sendNewTiles(deck);
    }

    public void sendNewTiles(ArrayList<Tile> tiles) {
        String message = NEW_STONES_COMMAND;
        for (Tile tile : tiles) {
            message += " " + tile.toProtocolForm();
        }

        sendMessage(message);
    }

    @Override
    protected Move determineMove() {
        return null;
    }
}
