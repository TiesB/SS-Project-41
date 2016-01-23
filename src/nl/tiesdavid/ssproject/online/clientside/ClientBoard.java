/**
 * Created by Ties on 23-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import nl.tiesdavid.ssproject.game.Board;
import nl.tiesdavid.ssproject.game.Tile;

public class ClientBoard extends Board {
    public void forcePlace(Tile tile) {
        tiles.add(tile);
        calculateMinMax(tile.getX(), tile.getY());
    }
}
