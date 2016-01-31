/**
 * Created by Ties on 26-1-2016.
 */
package nl.tiesb.ssproject.game;

import nl.tiesb.ssproject.game.exceptions.UnparsableDataException;

public class Test {
    public static void main(String[] args) {
        try {
            System.out.println(Tile.fromProtocolString("03,0"));
            System.out.println(Tile.fromProtocolString("3,0"));
            System.out.println(Tile.fromProtocolString("0,3"));
            System.out.println(Tile.fromProtocolString("2,5"));
            System.out.println(Tile.fromProtocolString("03,0", "1,0").toLongString());
        } catch (UnparsableDataException e) {
            e.printStackTrace();
        }
    }
}
