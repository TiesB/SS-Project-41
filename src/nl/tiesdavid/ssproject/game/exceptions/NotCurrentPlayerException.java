/**
 * Created by Ties on 12-1-2016.
 */
package nl.tiesdavid.ssproject.game.exceptions;

import nl.tiesdavid.ssproject.game.Player;

public class NotCurrentPlayerException extends MoveException {
    private final Player player;

    public NotCurrentPlayerException(Player player) {
        this.player = player;
    }

    @Override
    public String getMessage() {
        return player.getName() + " is not the current player.";
    }
}
