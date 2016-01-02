/**
 * Created by Ties on 1-1-2016.
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class NoTilesLeftInBagException extends MoveException {
    @Override
    public int getCode() {
        return 11;
    }

    @Override
    public String getMessage() {
        return "There are no tiles left in the bag.";
    }
}
