/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesb.ssproject.game.exceptions;

public class UnacceptableNameException extends Exception {
    public UnacceptableNameException(String name) {
        super("Unacceptable name: " + name);
    }
}
