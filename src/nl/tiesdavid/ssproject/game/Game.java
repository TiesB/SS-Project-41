/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
package nl.tiesdavid.ssproject.game;

import nl.tiesdavid.ssproject.game.exceptions.*;

import java.util.*;

public class Game {
    private static final int MIN_AMOUNT_OF_PLAYERS = 2;
    private static final int MAX_AMOUNT_OF_PLAYERS = 4;
    private static final int AMOUNT_OF_DUPLICATES_IN_BAG = 3;

    private Board board;
    private final ArrayList<Tile> bag;
    private final LinkedHashMap<Player, Integer> playersWithScores;

    private Player currentPlayer;

    private final Random randomGenerator;

    private boolean running = false;

    public Game() {
        this.board = new Board();
        this.bag = new ArrayList<>();
        this.playersWithScores = new LinkedHashMap<>();

        this.fillBag();

        this.randomGenerator = new Random();
        this.randomGenerator.setSeed(System.currentTimeMillis());
    }

    /**
     * Starts a game.
     * @throws NotEnoughPlayersException when not enough playersWithScores (<2) have been added to the game.
     */
    public void play() throws NotEnoughPlayersException {
        if (playersWithScores.size() >= MIN_AMOUNT_OF_PLAYERS) {
            running = true;

            board.reset();

            Iterator<Player> iterator = playersWithScores.keySet().iterator();

            Player highestScoringPlayer = null;
            int highestScore = -1;

            while (iterator.hasNext()) {
                Player player = iterator.next();
                if (player.getNoOfTilesSharingACharacteristic() > highestScore) {
                    highestScoringPlayer = player;
                    highestScore = player.getNoOfTilesSharingACharacteristic();
                }
            }

            iterator = playersWithScores.keySet().iterator();

            while (iterator.hasNext() && !iterator.next().equals(highestScoringPlayer)) {
                //Nothing to do here. Just let let the iterator start at the correct player.
                //noinspection UnnecessaryContinue
                continue;
            }

            while (!gameOver()) {
                while (iterator.hasNext()) {
                    takeTurn(currentPlayer);
                    currentPlayer = iterator.next();
                }
                iterator = playersWithScores.keySet().iterator();
            }
        } else {
            throw new NotEnoughPlayersException();
        }
    }

    protected void takeTurn(Player player) {
        //TODO. Is overridden in OnlineGame.
    }

    private void wrongMove(Player player) {
        System.out.println("Invalid move made by player: " + currentPlayer.getName());
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Rounds up the game by printing the final scores
     * and calling the win() function on the victorious player.
     */
    private void finish() {
        running = false;

        int highestScore = 0;
        Player highestScoringPlayer = null;

        for (Player player : playersWithScores.keySet()) {
            int score = playersWithScores.get(player);
            if (score > highestScore || highestScoringPlayer == null) {
                highestScore = score;
                highestScoringPlayer = player;
            }
        }
        printScores();
        if (highestScoringPlayer != null) {
            highestScoringPlayer.win();
        }
    }

    /**
     * Checks whether the game is over.
     * @return true when game is over.
     */
    public boolean gameOver() {
        if (playersWithScores.size() < MIN_AMOUNT_OF_PLAYERS) {
            return true;
        }
        for (Player player : playersWithScores.keySet()) {
            if (!player.hasTilesLeft()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fills the bag with #shapes * #colors * AMOUNT_OF_DUPLICATES_IN_BAG.
     */
    protected void fillBag() {
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
     * Adds a player to the game. Makes sure the # of playersWithScores doesn't exceed 4.
     * @param player The player to be added.
     */
    public void addPlayer(Player player) {
        if (playersWithScores.size() <= MAX_AMOUNT_OF_PLAYERS) {
            playersWithScores.put(player, 0);
        }
    }

    public ArrayList<Tile> place(Player player, ArrayList<Tile> tiles) throws NotCurrentPlayerException, MoveException {
        if (!currentPlayer.equals(player)) {
            throw new NotCurrentPlayerException(player);
        }

        checkCorrectTileSet(tiles); //Throws an exception when parsing an incorrect set.

        for (Tile tile : tiles) {
            if (!player.hasTile(tile)) {
                throw new NotInDeckException();
            }
        }

        int score = 0;

        int amount = tiles.size();
        ArrayList<Tile> tilesToBePlaced = new ArrayList<>();
        tilesToBePlaced.addAll(tiles);
        for (int i = 0; i < tilesToBePlaced.size(); i++) {
            for (Tile tile : tilesToBePlaced) {
                //TODO: Check whether this works when tiles get removed.
                try {
                    int possibleScore = board.placeTile(tile);
                    score += possibleScore; //So that when an exception is thrown, it doesn't get added to the score.
                    tilesToBePlaced.remove(tile);
                } catch (InvalidTilePlacementException e) {
                    //Nothing to be done here.
                }
            }
        }
        if (tilesToBePlaced.size() > 0) { //Failed: one or more tiles could not be placed.
            throw new InvalidTilePlacementException();
        }

        ArrayList<Tile> tilesToBeDealed = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            tilesToBeDealed.add(getTileFromBag());
        }

        for (Tile tile : tiles) {
            player.removeTile(tile);
        }

        handlePlaced(player, score, tiles);

        return tilesToBeDealed;
    }

    public ArrayList<Tile> trade(Player player, ArrayList<Tile> tiles) throws NotCurrentPlayerException, MoveException {
        if (!currentPlayer.equals(player)) {
            throw new NotCurrentPlayerException(player);
        }

        int amount = tiles.size();

        if (amount > amountOfTilesLeft()) {
            throw new NoTilesLeftInBagException();
        }

        for (Tile tile : tiles) {
            if (!player.hasTile(tile)) {
                throw new NotInDeckException();
            }
        }

        for (Tile tile : tiles) {
            player.removeTile(tile);
        }

        ArrayList<Tile> tilesToBeDealed = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            tilesToBeDealed.add(getTileFromBag());
        }

        putBackInBag(tiles);

        handleTraded(player, tiles);

        return tilesToBeDealed;
    }

    protected void handlePlaced(Player player, int score, ArrayList<Tile> tiles) {
        int oldScore = playersWithScores.get(player);
        playersWithScores.replace(player, oldScore, oldScore + score);
    }

    protected void handleTraded(Player player, ArrayList<Tile> tiles) {

    }

    public int amountOfTilesLeft() {
        return this.bag.size();
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

    public void putBackInBag(ArrayList<Tile> tiles) {
        for (Tile tile : tiles) {
            addTileToBag(tile);
        }
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
        /**
        //TODO: Format line3 nicely (same spacing as line1).
        String line1 = "";

        for (int i = 0; i < playersWithScores.size() - 1; i++) {
            line1 += String.format("%12s", playersWithScores.get(i).getName());
            line1 += " | ";
        }
        line1 += String.format("%12s", playersWithScores.get(playersWithScores.size() - 1).getName());
        System.out.println(line1);

        String line2 = "";
        for (int i = 0; i < line1.length(); i++) {
            line2 += "-";
        }
        System.out.println(line2);

        String line3 = "";

        for (int i = 0; i < playersWithScores.size() - 1; i++) {
            line3 += String.format("%12s", playersWithScores.get(i).getScore() + " | ");
        }
        line3 += String.format("%12s", playersWithScores.get(playersWithScores.size() - 1).getScore());

        System.out.println(line3);

        System.out.println();
         TODO: This should be removed, but can be used for spare parts.
         **/
    }

    public Board getBoard() {
        return this.board;
    }

    @Override
    public String toString() {
        if (playersWithScores.size() == 0) {
            return "";
        }

        String string = "";

        for (int i = 0; i < playersWithScores.size() - 1; i++) {
            string += playersWithScores.get(i) + System.lineSeparator();
        }
        string += playersWithScores.get(playersWithScores.size() - 1);

        return string;
    }

    // *** Utils ***
    protected void checkCorrectTileSet(ArrayList<Tile> tiles) throws MoveException {
        if (tiles.size() == 1) {
            return;
        }

        if (!checkCorrectOnXY(tiles)) {
            throw new NotTouchingException();
        }

        if (!checkCorrectOnAttributes(tiles)) {
            throw new TilesDontShareAttributeException();
        }
    }

    private boolean checkCorrectOnXY(ArrayList<Tile> tiles) {
        Collections.sort(tiles, Tile.tileComparator);

        boolean goodOnX = true, goodOnY = true;

        for (int i = 1; i < tiles.size(); i++) {
            if (!(tiles.get(i - 1).getX() == tiles.get(i).getX() - 1
                    || tiles.get(i - 1).getX() == tiles.get(i).getX() + 1)) {
                goodOnX = false;
            }
            if (!(tiles.get(i - 1).getY() == tiles.get(i).getY() - 1
                    || tiles.get(i - 1).getY() == tiles.get(i).getY() + 1)) {
                goodOnY = false;
            }
        }

        return goodOnX || goodOnY;
    }

    private boolean checkCorrectOnAttributes(ArrayList<Tile> tiles) {
        Boolean checkOnColor = decideCheckOnColor(tiles, 0);
        Tile.Color color = tiles.get(0).getColor();
        Tile.Shape shape = tiles.get(0).getShape();

        boolean goodOnColor = true, goodOnShape = true;

        for (int i = 1; i < tiles.size(); i++) {
            if (!tiles.get(i - 1).getColor().equals(color)) {
                goodOnColor = false;
            }
            if (!tiles.get(i - 1).getShape().equals(shape)) {
                goodOnShape = false;
            }
        }
        if (checkOnColor == null) {
            return goodOnColor || goodOnShape;
        } else if (checkOnColor) {
            return goodOnColor;
        } else {
            return goodOnShape;
        }
    }

    private Boolean decideCheckOnColor(ArrayList<Tile> tiles, int i) {
        try {
            if (tiles.get(i).getColor().equals(tiles.get(i + 1).getColor())
                    && !tiles.get(0).getShape().equals(tiles.get(i).getShape())) {
                return true;
            } else if (!tiles.get(i).getColor().equals(tiles.get(i + 1).getColor())
                    && tiles.get(i).getShape().equals(tiles.get(i + 1).getShape())) {
                return false;
            } else {
                return decideCheckOnColor(tiles, i + 1);
            }
        } catch (NullPointerException e) {
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
