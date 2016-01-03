/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.game;

import nl.tiesdavid.ssproject.game.exceptions.MoveException;
import nl.tiesdavid.ssproject.game.exceptions.NotEnoughPlayersException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {
    private static final int MIN_AMOUNT_OF_PLAYERS = 2;
    private static final int MAX_AMOUNT_OF_PLAYERS = 4;
    private static final int AMOUNT_OF_DUPLICATES_IN_BAG = 3;

    private Board board;
    private final ArrayList<Tile> bag;
    private final ArrayList<Player> players;

    private final Random randomGenerator;

    private boolean running = false;

    public Game() {
        board = new Board();
        bag = new ArrayList<>();
        players = new ArrayList<>();

        fillBag();

        randomGenerator = new Random();
        randomGenerator.setSeed(System.currentTimeMillis());
    }

    /**
     * Starts a game.
     * @throws NotEnoughPlayersException when not enough players (<2) have been added to the game.
     */
    public void play() throws NotEnoughPlayersException {
        if (players.size() >= MIN_AMOUNT_OF_PLAYERS) {
            running = true;

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
                try {
                    currentPlayer.makeMove(null);

                    while(!currentPlayer.moveFinished()){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    currentPlayerI = (currentPlayerI + 1) % players.size();
                    currentPlayer = players.get(currentPlayerI);
                } catch (MoveException e) {
                    System.out.println("Invalid move made by player: " + currentPlayer.getName());
                }
            }

            finish();
        } else {
            throw new NotEnoughPlayersException();
        }
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Rounds up the game by printing the final scores and calling the win() function on the victorious player.
     */
    private void finish() {
        running = false;
        Collections.sort(players);
        printScores();
        players.get(0).win();
    }

    /**
     * Checks whether the game is over.
     * @return true when game is over.
     */
    public boolean gameOver() {
        if (players.size() < MIN_AMOUNT_OF_PLAYERS) {
            return true;
        }
        for (Player player : players) {
            if (!player.hasTilesLeft()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fills the bag with #shapes * #colors * AMOUNT_OF_DUPLICATES_IN_BAG.
     */
    private void fillBag() {
        Tile.Color[] colors = Tile.Color.values();
        Tile.Shape[] shapes = Tile.Shape.values();
        for (int i = 0; i < AMOUNT_OF_DUPLICATES_IN_BAG; i++) {
            for (Tile.Color color : colors) {
                for (Tile.Shape shape : shapes) {
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

    public boolean hasTilesLeft() {
        return bag.size() > 0;
    }

    /**
     * Gives a random tile from the bag, when there are tiles left.
     * @return a tile from the bag.
     */
    public Tile getTileFromBag() {
        if (!hasTilesLeft()) {
            return null;
        }
        return bag.get(randomGenerator.nextInt(bag.size()));
    }

    /**
     * Adds the given tile to the bag.
     * @param tile the tile to be added to the bag.
     */
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
            line1 += String.format("%12s", players.get(i).getName());
            line1 += " | ";
        }
        line1 += String.format("%12s", players.get(players.size() - 1).getName());
        System.out.println(line1);

        String line2 = "";
        for (int i = 0; i < line1.length(); i++) {
            line2 += "-";
        }
        System.out.println(line2);

        String line3 = "";

        for (int i = 0; i < players.size() - 1; i++) {
            line3 += String.format("%12s", players.get(i).getScore() + " | ");
        }
        line3 += String.format("%12s", players.get(players.size() - 1).getScore());

        System.out.println(line3);

        System.out.println();
    }

    public void printBoard() {
        System.out.println(board);
    }

    public Board getBoard() {
        return this.board;
    }

    @Override
    public String toString() {
        if (players.size() == 0) {
            return "";
        }

        String string = "";

        for (int i = 0; i < players.size() - 1; i++) {
            string += players.get(i) + System.lineSeparator();
        }
        string += players.get(players.size() - 1);

        return string;
    }
}
