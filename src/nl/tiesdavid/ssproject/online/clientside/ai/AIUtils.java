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

    public static SortedSet<SortedSet<Tile>> findSets(Deck deck) {
        Set<SortedSet<Tile>> hashResult = new HashSet<>(); //HashSet to eliminate duplicates.
        for (Tile tile : deck) {
            hashResult.add(findTilesAlike(deck, tile, tile.getColor()));
            hashResult.add(findTilesAlike(deck, tile, tile.getShape()));
        }

        //Sorted set because it's easier to work with in a strategy.
        SortedSet<SortedSet<Tile>> sortedResult = new TreeSet<>(new Comparator<SortedSet<Tile>>() {
            @Override
            public int compare(SortedSet<Tile> o1, SortedSet<Tile> o2) {
                return Integer.compare(o1.size(), o2.size());
            }
        });

        sortedResult.addAll(hashResult);
        return sortedResult;
    }
}
