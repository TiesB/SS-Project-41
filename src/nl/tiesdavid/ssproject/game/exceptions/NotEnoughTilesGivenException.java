/**
 * Created by Ties on 25-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class NotEnoughTilesGivenException extends MoveException {
    @Override
    public String getMessage() {
        return "There were no tiles given.";
    }
}
