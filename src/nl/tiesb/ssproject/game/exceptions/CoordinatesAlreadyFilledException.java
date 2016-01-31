/**
 * Created by Ties on 20-12-2015.
 * @author Ties
 */
package nl.tiesb.ssproject.game.exceptions;

public class CoordinatesAlreadyFilledException extends InvalidTilePlacementException {

    @Override
    public String getMessage() {
        return "There already is a tile on the specified coordinates.";
    }
}
