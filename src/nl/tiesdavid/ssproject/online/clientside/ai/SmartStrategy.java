/**
 * Created by Ties on 26-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ai;

import nl.tiesdavid.ssproject.game.Board;
import nl.tiesdavid.ssproject.game.Deck;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.online.clientside.ClientGame;

import java.util.ArrayList;
import java.util.SortedSet;

public class SmartStrategy implements Strategy {
    @Override
    public ArrayList<Tile> determinePlaceMove(ClientGame game) {
        Board board = game.getBoard();
        Deck deck = game.getDeck();

        SortedSet<SortedSet<Tile>> sets = AIUtils.findSets(deck);

        if (sets.size() == 0) {
            return null;
        } else if (sets.first() == null) {
            return null;
        } else if (sets.first().size() == 0) {
            return null;
        }



        return null;
    }
}
