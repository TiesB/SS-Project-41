/**
 * Created by Ties on 19-12-2015.
 */
package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.exceptions.MoveException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Game {
    private Board board;
    private ArrayList<Tile> bag;
    private Map<Player, Integer> playersAndScores;

    private Random randomGenerator;

    public Game() {
        board = new Board();
        bag = new ArrayList<Tile>();
        playersAndScores = new HashMap<Player, Integer>();

        randomGenerator = new Random();
        randomGenerator.setSeed(System.currentTimeMillis());
    }

    /**
     * Adds a player to the game. Makes sure the # of players doesn't exceed 4.
     * @param player The player to be added.
     */
    public void addPlayer(Player player) {
        if (playersAndScores.size() < 4) {
            playersAndScores.put(player, 0);
        }
    }

    public boolean makeMove(Move move) throws MoveException {
        //TODO: Implement. Return whether move was successful (probably want to use exceptions).
        return true;
    }

    /**
     * Gives the bag containing the remaining not given tiles.
     * @return The bag containing the remaining not given tiles.
     */
    public ArrayList<Tile> getBag() {
        return bag;
    }

    public boolean hasTilesLeft() {
        return bag.size() > 0;
    }

    public Tile getTileFromBag() {
        return bag.get(randomGenerator.nextInt(bag.size()));
    }

    /**
     * Prints the scores in a user-friendly table.
     */
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
