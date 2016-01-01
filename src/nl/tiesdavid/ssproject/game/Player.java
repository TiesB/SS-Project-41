package nl.tiesdavid.ssproject.game;

import nl.tiesdavid.ssproject.game.enums.MoveType;
import nl.tiesdavid.ssproject.game.exceptions.MoveException;
import nl.tiesdavid.ssproject.game.exceptions.NonMatchingAttributesException;
import nl.tiesdavid.ssproject.game.exceptions.NotEnoughTilesGivenException;
import nl.tiesdavid.ssproject.game.exceptions.NotInDeckException;
import nl.tiesdavid.ssproject.game.exceptions.NotTouchingException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
public abstract class Player implements Comparable<Player> {
    protected static final int DECK_SIZE = 6;

    private final String name;
    protected final Deck deck;
    protected final Game game;

    protected int score;

    public Player(String name, Game game) {
        this.name = name;
        this.deck = new Deck(DECK_SIZE);
        this.game = game;

        score = 0;

        fillDeck();
    }

    /**
     * The function to determine the player's move. Will be called in makeMove().
     * @return the move player wants to make.
     */
    protected abstract Move determineMove();

    /**
     * You probably want to override this.
     * @param e the exception thrown.
     * @return a newly generated move.
     */
    protected Move handleMoveException(Exception e) {
        System.out.println(e.getMessage());
        return determineMove();
    }

    /**
     * Makes the move
     */
    public void makeMove() {
        Board board = game.getBoard();
        Move move = determineMove();

        if (move == null) {
            return;
        }

        if (move.getMoveType().equals(MoveType.TRADE_TILES)) {
            ArrayList<Tile> tilesToBeTraded = move.getTileList();
            tilesToBeTraded.forEach(this::tradeTile);
        } else if (move.getMoveType().equals(MoveType.ADD_TILE_AND_DRAW_NEW)) {
            System.out.println(move.getTile());
            placeAndDrawTile(move.getTile(), board);

        } else {
            ArrayList<Tile> tiles = move.getTileList();

            try {
                checkCorrectTileSet(tiles);

                for (Tile tile : tiles) {
                    placeAndDrawTile(tile, board);
                }

            } catch (MoveException e) {
                handleMoveException(e);
            }
        }
    }

    protected boolean checkCorrectTileSet(ArrayList<Tile> tiles) throws NotEnoughTilesGivenException, NotTouchingException, NonMatchingAttributesException {

        if (tiles.size() < 1) {
            throw new NotEnoughTilesGivenException();
        }

        if (!checkCorrectOnXY(tiles)) {
            throw new NotTouchingException();
        }

        if (!checkCorrectOnAttributes(tiles)) {
            throw new NonMatchingAttributesException();
        }

        return true;
    }

    private boolean checkCorrectOnXY(ArrayList<Tile> tiles) {
        Collections.sort(tiles, Tile.COMPARE_TILE);

        boolean goodOnX = true, goodOnY = true;

        for (int i = 1; i < tiles.size(); i++) {
            if (!(tiles.get(i - 1).getX() == tiles.get(i).getX() - 1 || tiles.get(i - 1).getX() == tiles.get(i).getX() + 1)){
                goodOnX = false;
            }
            if (!(tiles.get(i - 1).getY() == tiles.get(i).getY() - 1 || tiles.get(i - 1).getY() == tiles.get(i).getY() + 1)){
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
            //TODO: Wrong set entered.
        }
    }

    protected void placeAndDrawTile(Tile tile, Board board) {
        try {
            board.placeTile(tile);
            addToScore(board.getScore(tile));
            deck.remove(tile);
            drawTileFromBag();
        } catch (MoveException e) {
            handleMoveException(e);
        }
    }

    void win() {
        score += 6;
        System.out.println(name + " has won! With a score of: " + score);
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    private void addToScore(int increment) {
        if (increment < 0) {
            return;
        }
        score += increment;
    }

    private void tradeTile(Tile tile) {
        deck.remove(tile);
        game.addTileToBag(tile);
        drawTileFromBag();
    }

    protected void drawTileFromBag() {
        Tile tile = game.getTileFromBag();
        if (tile != null) {
            deck.add(tile);
        }
    }

    private void fillDeck() {
        while (deck.size() <= DECK_SIZE) {
            drawTileFromBag();
        }
    }

    /**
     * Gives whether or not the player has deck that are Non-Empty left.
     * To be used to determine whether a game is over.
     * @return Whether or not the player has deck that are Non-Empty left.
     */
    public boolean hasTilesLeft() {
        return deck.size() > 0;
    }

    protected boolean hasDuplicate (Tile tile) {
        int count = 0;
        for (Tile tile1 : deck) {
            if (tile1.getShape().equals(tile.getShape()) && tile1.getColor().equals(tile.getColor())) {
                count++;
            }
        }
        return count > 1;
    }

    protected Tile findTile(Tile tile) throws NotInDeckException {
        if (tile == null) {
            throw new NotInDeckException();
        }
        for (Tile tile1 : deck) {
            if (tile.getColor().equals(tile1.getColor())
                    && tile.getShape().equals(tile1.getShape())) {
                return tile1;
            }
        }
        throw new NotInDeckException();
    }

    /**
     * Gives the number of deck sharing a characteristic.
     * To be used at the play of a game.
     * @return The number of deck sharing a characteristic.
     */
    public int getNoOfTilesSharingACharacteristic() {
        //TODO: Fix this one. Now counts everything double.
        int count = 0;

        for (int i = 0; i < deck.size(); i++) {
            Tile tile1 = deck.get(i);
            for (int j = i + 1; j < deck.size(); j++) {
                Tile tile2 = deck.get(j);
                if (tile1.getColor().equals(tile2.getColor())
                        || tile1.getShape().equals(tile2.getShape())) {
                    count++;
                    tile1.setChecked();
                    tile2.setChecked();
                }
            }
            if (!tile1.getChecked()) {
                count++;
            }
        }

        return count;
    }

    @Override
    public String toString() {
        return name + ": " + Integer.toString(score);
    }

    @Override
    public int compareTo(Player o) {
        return Integer.compare(score, o.getScore());
    }
}