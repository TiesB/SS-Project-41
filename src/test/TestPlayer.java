/**
 * Created by Ties on 26-12-2015.
 */
package test;

import nl.tiesdavid.ssproject.Game;
import nl.tiesdavid.ssproject.Move;
import nl.tiesdavid.ssproject.Player;
import nl.tiesdavid.ssproject.Tile;

public class TestPlayer extends Player {
    TestPlayer() {
        super("Test", new Game());
        deck.clear();
    }

    public void addTileToDeck(Tile tile) {
        deck.add(tile);
        System.out.println(deck);
    }

    @Override
    public Move determineMove() {
        return null;
    }
}
