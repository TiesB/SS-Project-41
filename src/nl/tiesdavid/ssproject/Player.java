package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.Color;

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

    public boolean hasTilesLeft() {
        for (Tile tile : tiles) {
            if (!tile.getColor().equals(Color.EMPTY)) {
                return true;
            }
        }
        return false;
    }

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
