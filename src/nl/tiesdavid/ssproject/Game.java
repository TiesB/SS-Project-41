/**
 * Created by Ties on 19-12-2015.
 */
package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.Color;
import nl.tiesdavid.ssproject.enums.Shape;
import nl.tiesdavid.ssproject.exceptions.NotEnoughPlayersException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {
    private static final int MAX_AMOUNT_OF_PLAYERS = 4;

    private Board board;
    private ArrayList<Tile> bag;
    private ArrayList<Player> players;

    private Random randomGenerator;

    public Game() {
        board = new Board();
        bag = new ArrayList<>();
        players = new ArrayList<>();

        fillBag();

        randomGenerator = new Random();
        randomGenerator.setSeed(System.currentTimeMillis());
    }

    public void start() throws NotEnoughPlayersException {
        if (players.size() >= 2) {
            board.reset();

            Player currentPlayer = players.get(0);
            int currentPlayerI = 0;
            int highestCount = 0;
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                int n = player.getNoOfTilesSharingACharacteristic();
                if (n > highestCount) {
                    highestCount = n;
                    currentPlayer = player;
                    currentPlayerI = i;
                }
            }
            while (!gameOver()) {
                currentPlayer.makeMove();
                currentPlayerI = (currentPlayerI + 1) % players.size();
                currentPlayer = players.get(currentPlayerI);
            }

            finish();
        } else {
            throw new NotEnoughPlayersException();
        }
    }

    private void finish() {
        Collections.sort(players);
        for (Player player : players) {
            System.out.println(player);
        }
    }

    private boolean gameOver() {
        for (Player player : players) {
            if (!player.hasTilesLeft()) {
                return true;
            }
        }
        return false;
    }

    private void fillBag() {
        Color[] colors = Color.values();
        Shape[] shapes = Shape.values();
        for (int i = 0; i < 3; i++) {
            for (Color color : colors) {
                for (Shape shape : shapes) {
                    bag.add(new Tile(color, shape));
                }
            }
        }
    }

    /**
     * Adds a player to the game. Makes sure the # of players doesn't exceed 4.
     * @param player The player to be added.
     */
    public void addPlayer(Player player) {
        if (players.size() <= MAX_AMOUNT_OF_PLAYERS) {
            players.add(player);
        }
    }

    public void removePlayer(Player player) {
        int index = players.indexOf(player);
        players.remove(index);
    }

    /**
     * Gives the bag containing the remaining not given tiles.
     * @return The bag containing the remaining not given tiles.
     */
    public ArrayList<Tile> getBag() {
        return bag;
    }

    public int amountOfTilesLeft() {
        return bag.size();
    }

    public boolean hasTilesLeft() {
        return bag.size() > 0;
    }

    public Tile getTileFromBag() {
        if (!hasTilesLeft()) {
            return null;
        }
        return bag.get(randomGenerator.nextInt(bag.size()));
    }

    public void addTileToBag(Tile tile) {
        bag.add(tile);
    }

    /**
     * Prints the scores in a user-friendly table.
     */
    public void printScores() {
        //TODO: Format line3 nicely (same spacing as line1).

        String line1 = "";

        for (int i = 0; i < players.size() - 1; i++) {
            line1 += players.get(i).getName();
            line1 += " | ";
        }
        line1 += players.get(players.size() - 1).getName();
        System.out.println(line1);

        String line2 = "";
        for (int i = 0; i < line1.length(); i++) {
            line2 += "-";
        }
        System.out.println(line2);

        String line3 = "";

        for (int i = 0; i < players.size() - 1; i++) {
            line3 += players.get(i).getScore() + " | ";
        }
        line3 += players.get(players.size() - 1).getScore();

        System.out.println(line3);

        System.out.println();
    }

    public Board getBoard() {
        return this.board;
    }
}
