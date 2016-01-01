/**
 * Created by tiesb on 1-1-2016.
 * @author tiesb
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class NotInDeckException extends MoveException {
    @Override
    public String getMessage() {
        return "You do not have this tile in your deck.";
    }
}
