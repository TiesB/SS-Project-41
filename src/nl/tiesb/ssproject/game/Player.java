package nl.tiesb.ssproject.game;

import nl.tiesb.ssproject.game.exceptions.NotInDeckException;

import java.util.ArrayList;

/**
 * Created by Ties on 19-12-2015.
 * @author Ties
 */
public abstract class Player implements Comparable<Player> {
    public static final int DECK_SIZE = 6;

    private final String name;
    protected Deck deck;
    protected final Game game;

    protected int score;

    public Player(String name, Game game) {
        this.name = name;
        this.deck = new Deck();
        this.game = game;

        score = 0;

        fillDeck();
    }

    void win() {
        score += 6; //TODO: Remove this function, but don't forget to retain the 6 points bonus.
        System.out.println(name + " has won! With a score of: " + score);
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    protected void drawTileFromBag() {
        Tile tile = game.getTileFromBag();
        if (tile != null) {
            deck.add(tile);
        }
    }

    public void prepareForGame() {

    }

    private void fillDeck() {
        while (deck.size() < DECK_SIZE) {
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

    protected boolean hasDuplicate(Tile tile) {
        int count = 0;
        for (Tile tile1 : deck) {
            if (tile1.getShape().equals(tile.getShape())
                    && tile1.getColor().equals(tile.getColor())) {
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

    public void addTilesToDeck(ArrayList<Tile> tiles) {
        for (Tile tile : tiles) {
            if (tile != null) {
                deck.add(tile);
            }
        }
    }

    public void removeTile(Tile tile) {
        Tile.Color color = tile.getColor();
        Tile.Shape shape = tile.getShape();
        for (Tile tile1 : deck) {
            if (tile1.getColor().equals(color) && tile1.getShape().equals(shape)) {
                deck.remove(tile1);
                return;
            }
        }
    }

    public boolean hasTile(Tile tile) {
        Tile.Color color = tile.getColor();
        Tile.Shape shape = tile.getShape();
        for (Tile tile1 : deck) {
            if (tile1.getColor().equals(color) && tile1.getShape().equals(shape)) {
                return true;
            }
        }
        return false;
    }

    public String getPlayerName() {
        return name;
    }

    public Deck getDeck() {
        return deck;
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
