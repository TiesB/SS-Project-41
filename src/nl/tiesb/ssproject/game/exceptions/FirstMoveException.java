/**
 * Created by Ties on 15-1-2016.
 */
package nl.tiesb.ssproject.game.exceptions;

public class FirstMoveException extends MoveException {
    public FirstMoveException() {
    }

    @Override
    public String getMessage() {
        return "It is not allowed to trade tiles in the first move.";
    }
}
