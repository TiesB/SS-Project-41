/**
 * Created by Ties on 26-12-2015.
 * @author Ties
 */
package test;

import nl.tiesb.ssproject.game.Game;
import nl.tiesb.ssproject.game.Player;
import nl.tiesb.ssproject.game.Tile;

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
}
