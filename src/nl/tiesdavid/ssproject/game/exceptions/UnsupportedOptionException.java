/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.game.exceptions;

public class UnsupportedOptionException extends Exception {
    public UnsupportedOptionException(String object, String option) {
        super(object + " does not support option: " + option);
    }
}
