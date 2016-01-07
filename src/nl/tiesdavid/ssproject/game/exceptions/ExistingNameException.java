/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class ExistingNameException extends Exception {
    public ExistingNameException(String name) {
        super("Name already exists: " + name);
    }
}
