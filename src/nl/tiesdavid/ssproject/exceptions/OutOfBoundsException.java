/**
 * Created by Ties on 21-12-2015.
 */
package nl.tiesdavid.ssproject.exceptions;

public class OutOfBoundsException extends InvalidTilePlacementException {
    @Override
    public String getMessage() {
        return "The specified coordinates are out of bounds.";
    }
}
