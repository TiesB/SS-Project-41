/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class NonexistingPlayerException extends Exception {
    public NonexistingPlayerException(String player) {
        super("Player " + player + " is not on this server or in this game.");
    }
}
