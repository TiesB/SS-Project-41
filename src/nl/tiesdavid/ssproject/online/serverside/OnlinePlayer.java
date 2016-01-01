/**
 * Created by Ties on 1-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.Move;
import nl.tiesdavid.ssproject.game.Player;

public class OnlinePlayer extends Player {
    OnlinePlayer(String name, Game game) {
        super(name, game);
    }

    @Override
    protected Move determineMove() {
        return null;
    }
}
