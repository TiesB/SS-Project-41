/**
 * Created by Ties on 25-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.exceptions;

public class InvalidTilePlacementException extends MoveException {
    @Override
    public String getMessage() {
        return "Invalid place to put tile.";
    }
}
