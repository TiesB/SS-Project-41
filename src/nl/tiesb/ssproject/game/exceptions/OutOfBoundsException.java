/**
 * Created by Ties on 21-12-2015.
 * @author Ties
 */
package nl.tiesb.ssproject.game.exceptions;

public class OutOfBoundsException extends InvalidTilePlacementException {

    @Override
    public String getMessage() {
        return "The specified coordinates are out of bounds.";
    }
}
