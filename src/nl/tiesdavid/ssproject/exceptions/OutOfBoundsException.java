/**
 * Created by Ties on 21-12-2015.
 */
package nl.tiesdavid.ssproject.exceptions;

public class OutOfBoundsException extends MoveException {
    @Override
    public String getMessage() {
        return "The specified coordinates are out of bounds.";
    }
}
