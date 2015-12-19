/**
 * Created by Ties on 19-12-2015.
 */
package nl.tiesdavid.ssproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Game {
    private Board board;
    private ArrayList<Tile> bag;
    private Map<Player, Integer> playersAndScores;

    public Game() {
        board = new Board();
        bag = new ArrayList<Tile>();
        playersAndScores = new HashMap<Player, Integer>();
    }

    public void addPlayer(Player player) {
        if (playersAndScores.size() < 4) {
            playersAndScores.put(player, 0);
        }
    }

    public ArrayList<Tile> getBag() {
        return bag;
    }

    public void printScores() {
        //TODO: Format line3 nicely (same spacing as line1).

        String line1 = "";

        Object[] players = playersAndScores.keySet().toArray();

        for (int i = 0; i < players.length - 1; i++) {
            line1 += ((Player)players[i]).getName();
            line1 += " | ";
        }
        line1 += ((Player)players[players.length - 1]).getName();
        System.out.println(line1);

        String line2 = "";
        for (int i = 0; i < line1.length(); i++) {
            line2 += "-";
        }
        System.out.println(line2);

        String line3 = "";

        for (int i = 0; i < players.length - 1; i++) {
            line3 += playersAndScores.get(players[i]);
            line3 += " | ";
        }
        line3 += playersAndScores.get(players[players.length - 1]);

        System.out.println(line3);

        System.out.println();
    }
}
