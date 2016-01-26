/**
 * Created by Ties on 23-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import javafx.util.Pair;
import nl.tiesdavid.ssproject.game.Deck;
import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.NonexistingPlayerException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

public class ClientGame {
    private TreeSet<Pair<String, Integer>> playersWithScores;
    private int amountOfTilesInBag;
    private Deck deck;
    private ClientBoard board;

    public ClientGame() {
        this.playersWithScores = new TreeSet<Pair<String, Integer>>(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                return Integer.compare(o1.getValue(), o2.getValue());
            }
        });
        this.amountOfTilesInBag = Game.AMOUNT_OF_DUPLICATES_IN_BAG * 6 * 6;
    }

    public void addTileToDeck(Tile tile) {
        deck.add(tile);
    }

    public Tile getTileFromDeck(int no) {
        return deck.get(no);
    }

    public void removeTileFromDeck(Tile tile) {
        deck.remove(tile);
    }

    public void removeTilesFromDeck(ArrayList<Tile> tiles) {
        for (Tile tile : tiles) {
            removeTileFromDeck(tile);
        }
    }

    public void placeTile(Tile tile) {
        board.forcePlace(tile);
        deck.remove(tile);
        amountOfTilesInBag--;
    }

    public void addPlayer(String name) {
        playersWithScores.add(new Pair<>(name, 0));
    }

    public TreeSet<Pair<String, Integer>> getPlayersWithScores() {
        return playersWithScores;
    }

    public synchronized void removePlayer(String name) {
        try {
            playersWithScores.remove(getPlayerFromSet(name));
        } catch (NonexistingPlayerException e) {
            // Really nothing to do here.
        }
    }

    public synchronized void raiseScore(String name, int score) throws NonexistingPlayerException {
        Pair<String, Integer> player = getPlayerFromSet(name);
        int oldScore = player.getValue();
        playersWithScores.remove(player);
        playersWithScores.add(new Pair<>(name, oldScore + score));
    }

    public int getAmountOfTilesInBag() {
        return amountOfTilesInBag;
    }

    public synchronized int getScore(String name) throws NonexistingPlayerException {
        Pair<String, Integer> player = getPlayerFromSet(name);
        return player.getValue();
    }

    private Pair<String, Integer> getPlayerFromSet(String name) throws NonexistingPlayerException {
        for (Pair<String, Integer> playerWithScore : playersWithScores) {
            if (playerWithScore.getKey().equals(name)) {
                return playerWithScore;
            }
        }
        throw new NonexistingPlayerException(name);
    }

    public ClientBoard getBoard() {
        return board;
    }

    public Deck getDeck() {
        return deck;
    }
}
