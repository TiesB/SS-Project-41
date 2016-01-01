/**
 * Created by tiesb on 1-1-2016.
 */
package nl.tiesdavid.ssproject.exceptions;

public class NotInDeckException extends MoveException {
    @Override
    public String getMessage() {
        return "You do not have this tile in your deck.";
    }
}
