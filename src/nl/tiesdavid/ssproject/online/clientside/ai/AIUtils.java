/**
 * Created by Ties on 26-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ai;

import nl.tiesdavid.ssproject.game.Deck;
import nl.tiesdavid.ssproject.game.Tile;

import java.util.*;

public class AIUtils {
    private static SortedSet<Tile> findTilesAlike(Deck deck, Tile tile, Tile.Color color) {
        TreeSet<Tile> result = new TreeSet<>(Tile.tileComparator);
        for (Tile checkTile : deck) {
            if (!checkTile.equals(tile) && checkTile.getColor().equals(color)) {
                result.add(checkTile);
            }
        }
        return result;
    }

    private static SortedSet<Tile> findTilesAlike(Deck deck, Tile tile, Tile.Shape shape) {
        TreeSet<Tile> result = new TreeSet<>(Tile.tileComparator);
        for (Tile checkTile : deck) {
            if (!checkTile.equals(tile) && checkTile.getShape().equals(shape)) {
                result.add(checkTile);
            }
        }
        return result;
    }

    public static ArrayList<ArrayList<Tile>> findSets(Deck deck) {
        ArrayList<ArrayList<Tile>> result = new ArrayList<>();

        for (Tile.Color color : Tile.Color.values()) {
            ArrayList<Tile> colorList = new ArrayList<>();
            for (Tile tile : deck) {
                if (tile.getColor().equals(color)) {
                    colorList.add(tile);
                }
            }
            if (colorList.size() > 0) {
                result.add(colorList);
            }
        }

        for (Tile.Shape shape : Tile.Shape.values()) {
            ArrayList<Tile> shapeList = new ArrayList<>();
            for (Tile tile : deck) {
                if (tile.getShape().equals(shape)) {
                    shapeList.add(tile);
                }
            }
            if (shapeList.size() > 0) {
                result.add(shapeList);
            }
        }

        return result;
    }
}
