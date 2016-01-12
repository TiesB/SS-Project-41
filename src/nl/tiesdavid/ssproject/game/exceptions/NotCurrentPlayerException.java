/**
 * Created by Ties on 12-1-2016.
 */
package nl.tiesdavid.ssproject.game.exceptions;

import nl.tiesdavid.ssproject.game.Player;

public class NotCurrentPlayerException extends Exception {
    public NotCurrentPlayerException(Player player) {
        super(player.getName() + " is not the current player.");
    }
}
