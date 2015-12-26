package nl.tiesdavid.ssproject;

import nl.tiesdavid.ssproject.enums.MoveType;
import nl.tiesdavid.ssproject.exceptions.MoveException;
import nl.tiesdavid.ssproject.exceptions.NotEnoughTilesGivenException;
import nl.tiesdavid.ssproject.exceptions.TilesDontShareAttributeException;

import java.util.ArrayList;

/**
 * Created by Ties on 19-12-2015.
 */
public abstract class Player implements Comparable {
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

        System.out.println(getName() + ": " + getNoOfTilesSharingACharacteristic());
    }

    public abstract Move determineMove();

    /**
     * You probably want to override this.
     * @param e the exception thrown.
     * @return a newly generated move.
     */
    public Move handleMoveException(Exception e) {
        System.out.println(e.getMessage());
        return determineMove();
    }

    public void makeMove() {
        Board board = game.getBoard();
        Move move = determineMove();

        if (move.getMoveType().equals(MoveType.TRADE_TILES)) {
            ArrayList<Tile> tilesToBeTraded = move.getTileList();
            tilesToBeTraded.forEach(this::tradeTile);
            System.out.println(deck);

        } else if (move.getMoveType().equals(MoveType.ADD_TILE_AND_DRAW_NEW)) {
            System.out.println(move.getTile());
            placeAndDrawTile(move.getTile(), board);

        } else {
            ArrayList<Tile> tiles = move.getTileList();

            try {
                if (!checkCorrectTileSet(tiles)) {
                    throw new TilesDontShareAttributeException();
                }
                for (Tile tile : tiles) {
                    placeAndDrawTile(tile, board);
                }

            } catch (MoveException e) {
                handleMoveException(e);
            }
        }
    }

    private Boolean decideCheckForColor(ArrayList<Tile> tiles, int i) {
        try {
            if (tiles.get(i).getColor().equals(tiles.get(i + 1).getColor())
                    && !tiles.get(0).getShape().equals(tiles.get(i).getShape())) {
                return true;
            } else if (!tiles.get(i).getColor().equals(tiles.get(i + 1).getColor())
                    && tiles.get(i).getShape().equals(tiles.get(i + 1).getShape())) {
                return false;
            } else {
                return decideCheckForColor(tiles, i + 1);
            }
        } catch (NullPointerException e) {
            return null;
        }
    }

    private boolean checkCorrectTileSet(ArrayList<Tile> tiles) throws NotEnoughTilesGivenException {

        if (tiles.size() < 1) {
            throw new NotEnoughTilesGivenException();
        }

        Boolean checkForColor = decideCheckForColor(tiles, 0);
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
        if (checkForColor == null) {
            return goodOnColor || goodOnShape;
        } else if (checkForColor) {
            return goodOnColor;
        } else {
            return goodOnShape;
        }
    }

    private void placeAndDrawTile(Tile tile, Board board) {
        try {
            board.placeTile(tile);
            addToScore(board.getScore(tile));
            deck.remove(tile);
            drawTileFromBag();
        } catch (MoveException e) {
            handleMoveException(e);
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

    private void tradeTile(Tile tile) {
        deck.remove(tile);
        drawTileFromBag();
        game.addTileToBag(tile);
    }

    private void drawTileFromBag() {
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

    protected Tile findTile(Tile tile) {
        for (Tile tile1 : deck) {
            if (tile.getColor().equals(tile1.getColor())
                    && tile.getShape().equals(tile1.getShape())) {
                return tile1;
            }
        }
        return null;
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
    public int compareTo(Object o) {
        return Integer.compare(score, ((Player) o).getScore());
    }
}
