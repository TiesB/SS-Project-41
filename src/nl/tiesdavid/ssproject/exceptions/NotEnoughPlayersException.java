/**
 * Created by Ties on 20-12-2015.
 */
package nl.tiesdavid.ssproject.exceptions;

public class NotEnoughPlayersException extends Exception {
    @Override
    public String getMessage() {
        return "Not enough players added to the game to start.";
    }
}
