/**
 * Created by Ties on 26-1-2016.
 */
package nl.tiesb.ssproject.online.clientside.ai;

import nl.tiesb.ssproject.game.Tile;
import nl.tiesb.ssproject.game.Deck;

import java.util.*;
import java.util.stream.Collectors;

public class AIUtils {
    private static SortedSet<Tile> findTilesAlike(Deck deck, Tile tile, Tile.Color color) {
        TreeSet<Tile> result = new TreeSet<>(Tile.tileComparator);
        result.addAll(deck.stream().filter(checkTile -> !checkTile.equals(tile) &&
                checkTile.getColor().equals(color)).collect(Collectors.toList()));
        return result;
    }

    private static SortedSet<Tile> findTilesAlike(Deck deck, Tile tile, Tile.Shape shape) {
        TreeSet<Tile> result = new TreeSet<>(Tile.tileComparator);
        result.addAll(deck.stream().filter(checkTile -> !checkTile.equals(tile) &&
                checkTile.getShape().equals(shape)).collect(Collectors.toList()));
        return result;
    }

    public static ArrayList<ArrayList<Tile>> findSets(ArrayList<Tile> deck) {
        ArrayList<ArrayList<Tile>> result = new ArrayList<>();

        for (Tile.Color color : Tile.Color.values()) {
            ArrayList<Tile> colorList = deck.stream().filter(tile ->
                    tile.getColor().equals(color)).collect(Collectors.toCollection(ArrayList::new));
            if (colorList.size() > 0) {
                result.add(colorList);
            }
        }

        for (Tile.Shape shape : Tile.Shape.values()) {
            ArrayList<Tile> shapeList = deck.stream().filter(tile ->
                    tile.getShape().equals(shape)).collect(Collectors.toCollection(ArrayList::new));
            if (shapeList.size() > 0) {
                result.add(shapeList);
            }
        }

        return result;
    }
}
