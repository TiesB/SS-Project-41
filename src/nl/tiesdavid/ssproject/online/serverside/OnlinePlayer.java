/**
 * Created by Ties on 8-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Move;
import nl.tiesdavid.ssproject.game.Player;

public class OnlinePlayer extends Player {
    private ClientHandler clientHandler;

    public OnlinePlayer(ClientHandler clientHandler, OnlineGame game) {
        super(clientHandler.getPlayerName(), game);
        this.clientHandler = clientHandler;
    }

    @Override
    protected Move determineMove() {
        return null;
    }
}
