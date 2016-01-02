/**
 * Created by Ties on 1-1-2016.
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class NonMatchingAttributesException extends MoveException {
    @Override
    public int getCode() {
        return 13;
    }

    @Override
    public String getMessage() {
        return "The selected tiles don't all share attributes.";
    }
}
