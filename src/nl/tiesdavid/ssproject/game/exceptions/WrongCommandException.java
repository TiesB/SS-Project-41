/**
 * Created by Ties on 2-1-2016.
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class WrongCommandException extends Exception {
    public WrongCommandException(String command) {
        super("Wrong command: " + command);
    }
}
