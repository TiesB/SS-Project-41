/**
 * Created by Ties on 1-1-2016.
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class NotTouchingException extends MoveException {

    @Override
    public String getMessage() {
        return "The selected tiles don't touch each other.";
    }
}
