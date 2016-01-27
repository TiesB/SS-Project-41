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
import nl.tiesdavid.ssproject.online.serverside.ClientHandler;

import java.util.*;

public class AIPlayer extends Thread implements Observer {
    public static final boolean YOEP = false;
    public static final boolean LOCAL = true;

    private final ClientController clientController;
    private final Scanner scanner;

    private final String localName;
    private Strategy strategy;

    private ArrayList<ArrayList<Tile>> previousPlaceMoves;

    public AIPlayer(ClientController clientController) {
        this.clientController = clientController;
        this.scanner = new Scanner(System.in);
        this.previousPlaceMoves = new ArrayList<>();

        if (YOEP || LOCAL) {
            this.localName = Integer.toString(new Random(System.currentTimeMillis()).nextInt());
        } else {
            this.localName = "xXx_Optic_Qwirkle_" +
                    Integer.toString(new Random(System.currentTimeMillis()).nextInt()) +
                    "_xXx";
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
            int amountPossibleToTrade = game.getAmountOfTilesInBag();
            if (amountPossibleToTrade > game.getDeck().size()) {
                tradeAllTiles();
            } else if (amountPossibleToTrade < game.getDeck().size()) {
                tradeTiles(amountPossibleToTrade);
            } else {
                System.out.println("FUDGED");
            }
        } else {
            clientController.sendPlaceCommand(tilesToBePlaced);
        }
    }

    private void tradeTiles(int amount) {
        ObservableList<Tile> deck = clientController.getCurrentGame().getDeck();
        ArrayList<Tile> tilesToBeTraded = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < amount; i++) {
            Tile tile = deck.get(random.nextInt(amount));
            if (!tilesToBeTraded.contains(tile)) {
                tilesToBeTraded.add(tile);
            }
        }
    }

    private void tradeAllTiles() {
        System.out.println("Trading all tiles.");
        ObservableList<Tile> deck = clientController.getCurrentGame().getDeck();
        System.out.println("Trading deck: " + deck);
        clientController.sendTradeCommand(new ArrayList<>(deck));
    }

    private void init() {
        askForServerAndConnect();
        askAmountPlayersAndStrategy();
    }

    private void askForServerAndConnect() {
        if (YOEP) {
            clientController.parseGeneralStartupResult(this, localName, "130.89.136.146", 2727);
        } else if (LOCAL) {
            clientController.parseGeneralStartupResult(this, localName, "127.0.0.1", 3339);
        } else {
            printMessage(false, "Enter the server IP address: ");
            String serverIP = readString();
            printMessage(false, "Enter the server port: ");
            int serverPort = readInt("Invalid port number. Please try again.", 0, Integer.MAX_VALUE);
            clientController.parseGeneralStartupResult(this, localName, serverIP, serverPort);
        }
    }

    private void askAmountPlayersAndStrategy() {
        if (YOEP || LOCAL) {
            strategy = new SmartStrategy();
            clientController.waitForGame(2);
        } else {
            while (strategy == null) {
                printMessage(false, "Enter the strategy you want to use (smart): ");
                String strategyString = readString();
                switch (strategyString) {
                    case "smart":
                        strategy = new SmartStrategy();
                        break;
                }
            }

            printMessage(false, "Enter the amount of players you want to play with (2, 3 or 4): ");
            int amount = readInt("Invalid amount. Please try again.",
                    Game.MIN_AMOUNT_OF_PLAYERS - 1, Game.MAX_AMOUNT_OF_PLAYERS + 1);
            clientController.waitForGame(amount);
        }
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
        String player = messageParts[1];
        if (player.equals(localName)) {
            if (ClientHandler.DEBUG) {
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println("New move.");
            }
            makeMove();
            if (ClientHandler.DEBUG) {
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
            }
        }
    }

    private void receiveErrorCommand(String[] messageParts) {
        if (messageParts.length < 2) {
            return;
        }

        int error = -1;
        try {
            error = Integer.parseInt(messageParts[1]);
        } catch (NumberFormatException e) {
            return;
        }
        if (error == 0 && !previousPlaceMoves.isEmpty()) {
            makeMove();
        }
    }

    private void receivePlacedCommand(String[] messageParts) {
        if (messageParts.length < 5) {
            return;
        }

        String player = messageParts[1];
        if (player.equals(localName)) {
            previousPlaceMoves.clear();
        }
    }

    private void receiveTradedCommand(String[] messageParts) {
        if (messageParts.length < 3) {
            return;
        }

        String player = messageParts[1];
        if (player.equals(localName)) {
            previousPlaceMoves.clear();
        }
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
