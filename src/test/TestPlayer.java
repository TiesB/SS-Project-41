/**
 * Created by Ties on 26-12-2015.
 * @author Ties
 */
package test;

import nl.tiesdavid.ssproject.Game;
import nl.tiesdavid.ssproject.Move;
import nl.tiesdavid.ssproject.Player;
import nl.tiesdavid.ssproject.Tile;

public class TestPlayer extends Player {
    TestPlayer(Game game) {
        super("Test", game);
        this.deck.clear();
    }

    public void addTileToDeck(Tile tile) {
        this.deck.add(tile);
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void clearDeck() {
        this.deck.clear();
    }

    @Override
    public Move determineMove() {
        return null;
    }
}
