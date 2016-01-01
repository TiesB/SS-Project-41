/**
 * Created by Ties on 1-1-2016.
 */
package nl.tiesdavid.ssproject.exceptions;

public class NonMatchingAttributesException extends MoveException {
    @Override
    public String getMessage() {
        return "The selected tiles don't all share attributes.";
    }
}
