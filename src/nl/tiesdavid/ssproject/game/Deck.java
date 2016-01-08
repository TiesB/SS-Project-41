/**
 * Created by Ties on 21-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.game;

import java.util.ArrayList;

public class Deck extends ArrayList<Tile> {
    public Deck(int deckSize) {
        super(deckSize);
    }

    public String toUserString() {
        String line = "";
        for (int i = 0; i < size() - 1; i++) {
            line += get(i).toUserString() + " ";
        }
        line += get(size() - 1).toUserString();

        return line;
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
