/**
 * Created by Ties on 22-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ai;

import javafx.collections.ObservableList;
import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.online.Protocol;
import nl.tiesdavid.ssproject.online.clientside.ClientController;
import nl.tiesdavid.ssproject.online.clientside.ClientGame;

import java.util.*;

public class AIPlayer extends Thread implements Observer {
    private final ClientController clientController;
    private final Scanner scanner;

    private String localName;
    private Strategy strategy;

    private ArrayList<ArrayList<Tile>> previousPlaceMoves;

    public AIPlayer(ClientController clientController) {
        this.clientController = clientController;
        this.scanner = new Scanner(System.in);
        this.previousPlaceMoves = new ArrayList<>();
    }

    private void makeSingleTileMove() {
        Random random = new Random(System.currentTimeMillis());
        ObservableList<Tile> deck = clientController.getCurrentGame().getDeck();
        try {
            Tile tile = deck.get(random.nextInt(deck.size()));
            ArrayList<Tile> tiles = new ArrayList<>();
            tiles.add(tile);
            clientController.sendTradeCommand(tiles);
        } catch (IllegalArgumentException e) {
            //Shouldn't happen because then the deck would be empty.
            //The server should have stopped the game before a deck is empty.
        }
    }

    private void makeMove() {
        ClientGame game = clientController.getCurrentGame();
        if (ClientController.DEBUG) {
            System.out.println("Amount of tiles in bag: " + game.getAmountOfTilesInBag());
        }
        ArrayList<Tile> tilesToBePlaced = strategy.
                determinePlaceMove(game, previousPlaceMoves);
        if (tilesToBePlaced == null) {
            makeSingleTileMove();
        } else {
            clientController.sendPlaceCommand(tilesToBePlaced);
        }
    }

    private void init() {
        askForServerAndConnect();
        setUsername();
        askAmountPlayersAndStrategy();
    }

    private void askForServerAndConnect() {
        while (!clientController.isConnected()) {
            printMessage(false, "Enter the server IP address: ");
            String serverIP = readString();
            printMessage(false, "Enter the server port: ");
            int serverPort = readInt("Invalid port number. Please try again.", 0, Integer.MAX_VALUE);
            clientController.parseGeneralStartupResult(this, serverIP, serverPort);
        }
    }

    private void setUsername() {
        localName = "TiesB_Qwirkle_" +
                Integer.toString(new Random(System.currentTimeMillis()).nextInt(10));
        clientController.setUsername(localName);
    }

    private void askAmountPlayersAndStrategy() {
        while (strategy == null) {
            printMessage(false, "Enter the strategy you want to use (smart, brute): ");
            String strategyString = readString();
            switch (strategyString) {
                case "smart":
                    strategy = new SmartStrategy();
                    break;
                case "brute":
                    strategy = new BruteStrategy();
            }
        }
        printMessage(false, "Enter the amount of players you want to play with (2, 3 or 4): ");
        int amount = readInt("Invalid amount. Please try again.",
                Game.MIN_AMOUNT_OF_PLAYERS - 1, Game.MAX_AMOUNT_OF_PLAYERS + 1);
        clientController.sendJoinCommand(amount);
    }

    private void printMessage(boolean notification, String message) {
        System.out.print((notification ? " --- " : "")
                + message
                + (notification ? " --- " : ""));
    }

    private void printMessageLine(boolean notification, String message) {
        System.out.println((notification ? " --- " : "")
                + message
                + (notification ? " --- " : ""));
    }

    private int readInt(String errorMessage, int lowerBound, int upperBound) {
        boolean read = false;
        int response = 0;
        while (!read) {
            String input = scanner.next();
            try {
                response = Integer.parseInt(input);
                if (response > lowerBound && response < upperBound) {
                    read = true;
                } else {
                    printMessageLine(true, errorMessage);
                }
            } catch (NumberFormatException e) {
                printMessageLine(true, errorMessage);
            }
        }
        return response;
    }

    private String readString() {
        return scanner.nextLine();
    }

    private void receiveTurnCommand(String[] messageParts) {
        previousPlaceMoves.clear();
        String player = messageParts[1];
        if (player.equals(localName)) {
            makeMove();
        }
    }

    private void receiveErrorCommand(String[] messageParts) {
        if (messageParts.length < 2) {
            return;
        }

        int error;
        try {
            error = Integer.parseInt(messageParts[1]);
        } catch (NumberFormatException e) {
            return;
        }
        switch (error) {
            case 1:
                if (ClientController.DEBUG) {
                    System.out.println("[DEBUG] Making move because of error.");
                }
                makeMove();
                break;
            case 2:
                setUsername();
        }
    }

    private void receivePlacedCommand(String[] messageParts) {
        previousPlaceMoves.clear();
    }

    private void receiveTradedCommand(String[] messageParts) {
        previousPlaceMoves.clear();
    }

    @Override
    public void run() {
        init();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String line = (String) arg;
            String[] parts = line.split(" ");
            String command = parts[0];
            switch (command) {
                case Protocol.SERVER_TURN_COMMAND:
                    receiveTurnCommand(parts);
                    break;
                case Protocol.SERVER_ERROR_COMMAND:
                    receiveErrorCommand(parts);
                    break;
                case Protocol.SERVER_PLACED_COMMAND:
                    receivePlacedCommand(parts);
                    break;
                case Protocol.SERVER_TRADED_COMMAND:
                    receiveTradedCommand(parts);
            }
        }
    }
}
