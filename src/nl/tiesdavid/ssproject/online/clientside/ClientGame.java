/**
 * Created by Ties on 23-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import javafx.util.Pair;
import nl.tiesdavid.ssproject.game.*;
import nl.tiesdavid.ssproject.game.exceptions.NonexistingPlayerException;

import java.util.ArrayList;

public class ClientGame {
    private ArrayList<Pair<String, Integer>> playersWithScores;
    private int amountOfTilesInBag;
    private Deck deck;
    private Board board;

    public ClientGame() {
        System.out.println("Creating new game");
        this.playersWithScores = new ArrayList<>();
        this.amountOfTilesInBag = Game.AMOUNT_OF_DUPLICATES_IN_BAG * 6 * 6;
        this.deck = new Deck(Player.DECK_SIZE * 2);
        this.board = new Board();
    }

    public synchronized void addTileToDeck(Tile tile) {
        deck.add(tile);
        if (ClientController.DEBUG) {
            System.out.println("Added to deck: " + tile.toLongString());
        }
    }

    public Tile getTileFromDeck(int no) {
        return deck.get(no);
    }

    public synchronized void removeTileFromDeck(Tile tile) {
        if (ClientController.DEBUG) {
            System.out.println("Removing tile from deck: " + tile.toLongString());
        }
        for (Tile tile1 : deck) {
            if (tile1.getColor().equals(tile.getColor()) &&
                    tile1.getShape().equals(tile.getShape())) {
                deck.remove(tile1);
                System.out.println("Lola: " + deck);
                return;
            }
        }
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

    public Deck getDeck() {
        return deck;
    }
}
