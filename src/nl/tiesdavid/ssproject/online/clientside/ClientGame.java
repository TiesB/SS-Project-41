/**
 * Created by Ties on 23-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import nl.tiesdavid.ssproject.game.*;
import nl.tiesdavid.ssproject.game.exceptions.NonexistingPlayerException;

import java.util.ArrayList;

public class ClientGame {
    private ArrayList<Pair<String, Integer>> playersWithScores;
    private int amountOfTilesInBag;
    private ObservableList<Tile> deck;
    private Board board;

    public ClientGame() {
        System.out.println("Creating new game");
        this.playersWithScores = new ArrayList<>();
        this.amountOfTilesInBag = Game.AMOUNT_OF_DUPLICATES_IN_BAG * 6 * 6;
        this.deck = FXCollections.observableArrayList();
        this.deck.addListener(new ListChangeListener<Tile>() {
            @Override
            public void onChanged(Change<? extends Tile> c) {
                System.out.println("Tile changed: " + c);
            }
        });
        this.board = new Board();
    }

    public synchronized void addTileToDeck(Tile tile) {
        deck.add(tile);
        if (ClientController.DEBUG) {
            System.out.println("[DEBUG] Added to deck: " + tile.toLongString());
            System.out.println("[DEBUG] Current state of deck: " + deck);
        }
    }

    public Tile getTileFromDeck(int no) {
        return deck.get(no);
    }

    public synchronized void removeTileFromDeck(Tile tile) {
        if (ClientController.DEBUG) {
            System.out.println("[DEBUG] Removing tile from deck: " + tile.toLongString());
            System.out.println("[DEBUG] State of deck before removal: " + deck);
        }
        deck.remove(tile);
        if (ClientController.DEBUG) {
            System.out.println("[DEBUG] State of deck after removal: " + deck);
        }
    }

    public void removeTilesFromDeck(ArrayList<Tile> tiles) {
        for (Tile tile : tiles) {
            removeTileFromDeck(tile);
        }
    }

    public void placeTile(Tile tile) {
        board.forcePlace(tile);
        amountOfTilesInBag--;
    }

    public void addPlayer(String name) {
        playersWithScores.add(new Pair<>(name, 0));
    }

    public ArrayList<Pair<String, Integer>> getPlayersWithScores() {
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

    public void decreaseAmountOfTilesInBag(int amount) {
        amountOfTilesInBag -= amount;
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

    public Board getBoard() {
        return board;
    }

    public ObservableList<Tile> getDeck() {
        return deck;
    }
}
