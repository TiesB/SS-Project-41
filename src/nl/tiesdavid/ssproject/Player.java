package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.MoveType;
import nl.tiesdavid.ssproject.exceptions.MoveException;

import java.util.ArrayList;

/**
 * Created by Ties on 19-12-2015.
 */
public abstract class Player implements Comparable {
    protected static final int DECK_SIZE = 6;

    protected String name;
    protected ArrayList<Tile> deck;
    protected Game game;

    protected int score;

    public Player(String name, Game game) {
        this.name = name;
        this.deck = new ArrayList<>();
        this.game = game;

        score = 0;

        fillDeck();
    }

    public abstract Move determineMove();
    public abstract void handleException(Exception e);

    public void makeMove() {
        Board board = game.getBoard();
        Move move = determineMove();
        if (move.getMoveType().equals(MoveType.TRADE_TILES)) {
            ArrayList<Tile> tilesToBeTraded = move.getTileList();
            for (Tile tile : tilesToBeTraded) {
                tradeTile(tile);
            }
            System.out.println(deck);
        } else if (move.getMoveType().equals(MoveType.ADD_TILE_AND_DRAW_NEW)) {
            try {
                Tile tile = move.getTile();
                board.placeTile(tile);
                addToScore(board.getScore(tile));

                deck.remove(tile);
                drawTileFromBag();
            } catch (MoveException e) {
                handleException(e);
            }
        }
    }

    protected void win() {
        score += 6;
        System.out.println(name + " has won! With a score of: " + score);
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    protected void addToScore(int increment) {
        score += increment;
    }

    protected void tradeTile(Tile tile) {
        deck.remove(tile);
        drawTileFromBag();
        game.addTileToBag(tile);
    }

    protected void drawTileFromBag() {
        Tile tile = game.getTileFromBag();
        if (tile != null) {
            deck.add(tile);
        }
    }

    protected void fillDeck() {
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

    protected Tile findTile(Tile tile) {
        for (Tile tile1 : deck) {
            if (tile.getColor().equals(tile1.getColor()) && tile.getShape().equals(tile1.getShape())) {
                return tile1;
            }
        }
        return null;
    }

    /**
     * Reorders the list so that Non-Empty deck are at the front of the array.
     *//* TODO: Probably not needed.
    public void reorderTiles() {
        ArrayList<Tile> tempList = new ArrayList<Tile>();

        for (Tile tile : deck) {
            if (!tile.getColor().equals(Color.EMPTY)) {
                tempList.add(tile);
            }
        }

        for (int i = tempList.size(); i < deck.length; i++) {
            tempList.add(new Tile());
        }

        for (int i = 0; i < deck.length; i++) {
            deck[i] = tempList.get(i);
        }
    } */

    /**
     * Gives the number of deck sharing a characteristic.
     * To be used at the start of a game.
     * @return The number of deck sharing a characteristic.
     */
    public int getNoOfTilesSharingACharacteristic() {
        //TODO: Fix this one. Now counts everything double.
        int count = 0;

        for (Tile tile1 : deck) {
            for (Tile tile2 : deck) {
                if (tile1 != tile2) {
                    if (tile1.getColor().equals(tile2.getColor())
                            || tile1.getShape().equals(tile2.getShape())) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    @Override
    public String toString() {
        return name + ": " + Integer.toString(score);
    }

    @Override
    public int compareTo(Object o) {
        return Integer.compare(score, ((Player) o).getScore());
    }
}
