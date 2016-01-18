/**
 * Created by Ties on 25-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class NoNeighboringTileException extends InvalidTilePlacementException {

    @Override
    public String getMessage() {
        return "On that spot the tile would have no neighboring tile(s) that share its attributes.";
    }
}
