/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
package nl.tiesb.ssproject.game;

import nl.tiesb.ssproject.game.exceptions.*;
import nl.tiesb.ssproject.online.serverside.ClientHandler;

import java.util.*;

public class Game extends Thread {
    public static final int MIN_AMOUNT_OF_PLAYERS = 2;
    public static final int MAX_AMOUNT_OF_PLAYERS = 4;
    public static final int AMOUNT_OF_DUPLICATES_IN_BAG = 3;

    private Board board;
    protected final ArrayList<Tile> bag;
    private final LinkedHashMap<Player, Integer> playersWithScores;

    private Player currentPlayer;
    private boolean firstMoveDone;
    private boolean moveFinished;

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

    @Override
    public void run() {
        try {
            play();
        } catch (NotEnoughPlayersException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a game.
     * @throws NotEnoughPlayersException when not enough playersWithScores (<2)
     * have been added to the game.
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

            currentPlayer = highestScoringPlayer;

            iterator = playersWithScores.keySet().iterator();

            while (iterator.hasNext() && !iterator.next().equals(highestScoringPlayer)) {
                //Nothing to do here. Just let let the iterator setUp at the correct player.
                //noinspection UnnecessaryContinue
                continue;
            }

            //Start of game.
            firstMoveDone = false;

            playersWithScores.keySet().forEach(Player::prepareForGame);

            while (running) {
                while (iterator.hasNext() && running) {
                    moveFinished = false;
                    if (ClientHandler.DEBUG) {
                        System.out.println("New move time!!");
                    }
                    takeTurn(currentPlayer);
                    while (!moveFinished) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (gameOver()) {
                        finish();
                        return;
                    }

                    currentPlayer = iterator.next();
                }
                if (ClientHandler.DEBUG) {
                    System.out.println("[DEBUG] End of iterator.");
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

    protected void finish() {
        running = false;
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
    public synchronized void addPlayer(Player player) {
        if (playersWithScores.size() <= MAX_AMOUNT_OF_PLAYERS) {
            playersWithScores.put(player, 0);
        }
    }



    public synchronized ArrayList<Tile> place(Player player, ArrayList<Tile> tiles)
            throws MoveException {
        if (!currentPlayer.equals(player)) {
            throw new NotCurrentPlayerException(player);
        }

        checkCorrectTileSet(tiles); //Throws an exception when parsing an incorrect set.

        for (Tile tile : tiles) {
            if (!player.hasTile(tile)) {
                throw new NotInDeckException();
            }
        }

        int score = board.placeTiles(tiles);
        int amount = tiles.size();

        ArrayList<Tile> tilesToBeDealed = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            tilesToBeDealed.add(getTileFromBag());
        }

        tiles.forEach(player::removeTile);

        handlePlaced(player, score, tiles);

        return tilesToBeDealed;
    }

    public synchronized ArrayList<Tile> trade(Player player, ArrayList<Tile> tiles)
            throws MoveException {
        if (!firstMoveDone) {
            throw new FirstMoveException();
        }

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

        tiles.forEach(player::removeTile);

        ArrayList<Tile> tilesToBeDealed = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            tilesToBeDealed.add(getTileFromBag());
        }

        putBackInBag(tiles);

        handleTraded(player, tiles);

        return tilesToBeDealed;
    }

    protected void handleMoveFinished() {
        if (!firstMoveDone) {
            firstMoveDone = true;
        }
        moveFinished = true;
    }

    protected void handlePlaced(Player player, int score, ArrayList<Tile> tiles) {
        int oldScore = playersWithScores.get(player);
        playersWithScores.replace(player, oldScore, oldScore + score);

        handleMoveFinished();
    }

    protected void handleTraded(Player player, ArrayList<Tile> tiles) {
        handleMoveFinished();
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
        return bag.remove(randomGenerator.nextInt(bag.size()));
    }

    public void putBackInBag(ArrayList<Tile> tiles) {
        tiles.forEach(this::addTileToBag);
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


    public Board getBoard() {
        return this.board;
    }

    public int getScore(Player player) {
        return playersWithScores.get(player);
    }

    @Override
    public String toString() {
        if (playersWithScores.size() == 0) {
            return "";
        }

        String string = "";

        for (Player player : playersWithScores.keySet()) {
            string += player.getPlayerName() + ": " +
                    playersWithScores.get(player) + System.lineSeparator();
        }

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
