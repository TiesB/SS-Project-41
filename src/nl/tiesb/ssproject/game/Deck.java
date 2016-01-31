/**
 * Created by Ties on 21-12-2015.
 * @author Ties
 */
package nl.tiesb.ssproject.game;

import java.util.ArrayList;

public class Deck extends ArrayList<Tile> {
    public Deck() {
        super();
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
        Deck newDeck = new Deck();
        for (Tile tile : this) {
            newDeck.add(tile);
        }
        return newDeck;
    }

    @Override
    public String toString() {
        String line = "";
        if (size() > 0) {
            for (int i = 0; i < size() - 1; i++) {
                line += get(i) + " ";
            }
            line += get(size() - 1);
        }

        return line;
    }
}
