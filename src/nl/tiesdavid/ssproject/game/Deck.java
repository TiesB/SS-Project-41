/**
 * Created by Ties on 21-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.game;

import java.util.ArrayList;

public class Deck extends ArrayList<Tile> {
    private int deckSize;

    public Deck(int deckSize) {
        super(deckSize);
        this.deckSize = deckSize;
    }

    public String toUserString() {
        String line = "";
        for (int i = 0; i < size() - 1; i++) {
            line += get(i).toUserString() + " ";
        }
        line += get(size() - 1).toUserString();

        return line;
    }

    public Deck getCopy() {
        Deck newDeck = new Deck(deckSize);
        for (Tile tile : this) {
            newDeck.add(tile);
        }
        return newDeck;
    }

    @Override
    public String toString() {
        String line = "";
        for (int i = 0; i < size() - 1; i++) {
            line += get(i) + " ";
        }
        line += get(size() - 1);

        return line;
    }
}
