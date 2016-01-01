/**
 * Created by Ties on 26-12-2015.
 * @author Ties
 */
package test;

import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.Move;
import nl.tiesdavid.ssproject.game.Player;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.NonMatchingAttributesException;
import nl.tiesdavid.ssproject.game.exceptions.NotEnoughTilesGivenException;
import nl.tiesdavid.ssproject.game.exceptions.NotTouchingException;

import java.util.ArrayList;

public class TestPlayer extends Player {
    TestPlayer(Game game) {
        super("Test", game);
        this.deck.clear();
    }

    public void addTileToDeck(Tile tile) {
        this.deck.add(tile);
    }

    @Override
    public boolean checkCorrectTileSet(ArrayList<Tile> tiles) throws NotEnoughTilesGivenException, NotTouchingException, NonMatchingAttributesException {
        return super.checkCorrectTileSet(tiles);
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
