/**
 * Created by Ties on 20-12-2015.
 */
package nl.tiesdavid.ssproject.exceptions;

public class CoordinatesAlreadyFilledException extends MoveException {
    @Override
    public String getMessage() {
        return "There already is a tile on the specified coordinates.";
    }
}
