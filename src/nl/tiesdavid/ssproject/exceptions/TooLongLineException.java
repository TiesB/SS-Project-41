/**
 * Created by Ties on 20-12-2015.
 */
package nl.tiesdavid.ssproject.exceptions;

public class TooLongLineException extends InvalidTilePlacementException {
    @Override
    public String getMessage() {
        return "A line that is too long would be created when placing a tile at these coordinates.";
    }
}
