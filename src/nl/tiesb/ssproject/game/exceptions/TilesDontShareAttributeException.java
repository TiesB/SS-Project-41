/**
 * Created by Ties on 23-12-2015.
 * @author Ties
 */
package nl.tiesb.ssproject.game.exceptions;

public class TilesDontShareAttributeException extends MoveException {

    @Override
    public String getMessage() {
        return "The selected tiles don't share a attribute (color or shape).";
    }
}
