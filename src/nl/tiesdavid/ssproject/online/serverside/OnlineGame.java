/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.Player;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.MoveException;
import nl.tiesdavid.ssproject.online.Protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OnlineGame extends Game {

    private final Lobby lobby;
    private Map<ClientHandler, OnlinePlayer> clientHandlers;

    public OnlineGame(Lobby lobby) {
        super();
        this.lobby = lobby;
        this.clientHandlers = new HashMap<>();
    }

    @Override
    protected void takeTurn(Player player) {
        super.takeTurn(player);
        distributeMessage(Protocol.SERVER_TURN_COMMAND + " " + player.getPlayerName());
    }

    private synchronized void distributeMessage(String message) {
        for (ClientHandler handler : clientHandlers.keySet()) {
            handler.sendMessageToClient(message);
        }
    }

    @Override
    protected void handlePlaced(Player player, int score, ArrayList<Tile> tiles) {
        super.handlePlaced(player, score, tiles);
        String message = Protocol.SERVER_PLACED_COMMAND + " " + player.getName() + " " + Integer.toString(score);
        for (Tile tile : tiles) {
            message += " " + tile.toProtocolForm();
        }

        if (ClientHandler.DEBUG) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
        }

        distributeMessage(message);

        if (ClientHandler.DEBUG) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
        }
    }

    @Override
    protected void handleTraded(Player player, ArrayList<Tile> tiles) {
        super.handleTraded(player, tiles);
        String message = Protocol.SERVER_TRADED_COMMAND + " " + player.getName()
                + " " + Integer.toString(tiles.size());

        distributeMessage(message);

        if (ClientHandler.DEBUG) {
            System.out.println("Tiles left in bag: " + bag);
        }
    }

    public void place(ClientHandler client, ArrayList<Tile> tiles) throws MoveException {
        OnlinePlayer player = clientHandlers.get(client);
        ArrayList<Tile> tilesToBeDealed = this.place(player, tiles);
        player.addTilesToDeck(tilesToBeDealed);
        player.sendNewTiles(tilesToBeDealed);
        if (ClientHandler.DEBUG) {
            System.out.println(player.getPlayerName() + "'s deck: " + player.getDeck());
            System.out.println(client.getPlayerName() + " score: " + getScore(player));
        }
    }

    public void trade(ClientHandler client, ArrayList<Tile> tiles) throws MoveException {
        OnlinePlayer player = clientHandlers.get(client);
        ArrayList<Tile> tilesToBeDealed = this.trade(player, tiles);
        player.addTilesToDeck(tilesToBeDealed);
        player.sendNewTiles(tilesToBeDealed);
        if (ClientHandler.DEBUG) {
            System.out.println(client.getPlayerName() + " score: " + getScore(player));
        }
    }

    public void addPlayer(ClientHandler clientHandler) {
        OnlinePlayer player = new OnlinePlayer(clientHandler, this);
        this.addPlayer(player);
        clientHandlers.put(clientHandler, player);
        //TODO
    }

    public OnlinePlayer getPlayer(ClientHandler clientHandler) {
        return clientHandlers.get(clientHandler);
    }

    public synchronized void disconnectClient(ClientHandler client) {
        clientHandlers.remove(client);
        shutdown();
    }

    private synchronized void shutdown() {
        finish();
        distributeMessage(Protocol.SERVER_END_GAME_COMMAND);
    }

    public ArrayList<ClientHandler> getClientHandlers() {
        return new ArrayList<>(clientHandlers.keySet());
    }
}
