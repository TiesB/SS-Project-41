/**
 * Created by Ties on 20-12-2015.
 */
package nl.tiesdavid.ssproject.exceptions;

public class InvalidMoveTypeWithArgumentsException extends MoveException {
    @Override
    public String getMessage() {
        return "The arguments do not correspond with the given MoveType.";
    }
}
