/**
 * Created by Ties on 8-1-2016.
 */
package nl.tiesb.ssproject.game.exceptions;

public class UnparsableDataException extends Exception {
    public UnparsableDataException(String data) {
        super("Unparsable data: " + data);
    }
}
