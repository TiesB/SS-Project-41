package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.Color;
import java.util.ArrayList;

/**
 * Created by Ties on 19-12-2015.
 */
public abstract class Player {
    private String name;
    private Tile[] tiles;

    public Player(String name) {
        this.name = name;
        this.tiles = new Tile[6];
    }

    public abstract Tile determineMove();

    public String getName() {
        return name;
    }

    /**
     * Gives whether or not the player has tiles that are Non-Empty left.
     * To be used to determine whether a game is over.
     * @return Whether or not the player has tiles that are Non-Empty left.
     */
    public boolean hasTilesLeft() {
        for (Tile tile : tiles) {
            if (!tile.getColor().equals(Color.EMPTY)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reorders the list so that Non-Empty tiles are at the front of the array.
     */
    public void reorderTiles() {
        ArrayList<Tile> tempList = new ArrayList<Tile>();

        for (Tile tile : tiles) {
            if (!tile.getColor().equals(Color.EMPTY)) {
                tempList.add(tile);
            }
        }

        for (int i = tempList.size(); i < tiles.length; i++) {
            tempList.add(new Tile());
        }

        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = tempList.get(i);
        }
    }

    /**
     * Gives the number of tiles sharing a characteristic.
     * To be used at the start of a game.
     * @return The number of tiles sharing a characteristic.
     */
    public int getNoOfTilesSharingACharacteristic() {
        int count = 0;

        for (Tile tile1 : tiles) {
            for (Tile tile2 : tiles) {
                if (tile1 != tile2) {
                    if (tile1.getColor().equals(tile2.getColor())
                            || tile1.getShape().equals(tile2.getShape())) {
                        count++;
                    }
                }
            }
        }

        return count;
    }
}
