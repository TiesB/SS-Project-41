/**
 * Created by Ties on 25-12-2015.
 */
package nl.tiesdavid.ssproject.exceptions;

public class NotEnoughTilesGivenException extends MoveException {
    @Override
    public String getMessage() {
        return "There were no tiles given.";
    }
}