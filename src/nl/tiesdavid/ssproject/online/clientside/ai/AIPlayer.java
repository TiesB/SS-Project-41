/**
 * Created by Ties on 22-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ai;

import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.online.Protocol;
import nl.tiesdavid.ssproject.online.clientside.ClientController;
import nl.tiesdavid.ssproject.online.clientside.ClientGame;

import java.util.*;

public class AIPlayer extends Thread implements Observer {
    public static final boolean YOEP = true;

    private final ClientController clientController;
    private final Scanner scanner;

    private final String localName;
    private Strategy strategy;

    public AIPlayer(ClientController clientController) {
        this.clientController = clientController;
        this.scanner = new Scanner(System.in);
        if (!YOEP) {
            this.localName = "xXx_Optic_Qwirkle_" +
                    Integer.toString(new Random(System.currentTimeMillis()).nextInt()) +
                    "_xXx";
        } else {
            localName = Integer.toString(new Random(System.currentTimeMillis()).nextInt());
        }
    }

    private void makeMove() {
        ClientGame game = clientController.getCurrentGame();
        System.out.println(game);
        ArrayList<Tile> tilesToBePlaced = strategy.
                determinePlaceMove(game);
        if (tilesToBePlaced == null) {
            tradeAllTiles();
        } else {
            clientController.sendPlaceCommand(tilesToBePlaced);
        }
    }

    private void tradeAllTiles() {
        System.out.println("Trading all tiles.");
        clientController.sendTradeCommand(clientController.getCurrentGame().getDeck());
    }

    private void init() {
        askForServerAndConnect();
        askAmountPlayersAndStrategy();
    }

    private void askForServerAndConnect() {
        if (YOEP) {
            clientController.parseGeneralStartupResult(this, localName, "130.89.136.146", 2727);
        } else {
            printMessage(false, "Enter the server IP address: ");
            String serverIP = readString();
            printMessage(false, "Enter the server port: ");
            int serverPort = readInt("Invalid port number. Please try again.", 0, Integer.MAX_VALUE);
            clientController.parseGeneralStartupResult(this, localName, serverIP, serverPort);
        }
    }

    private void askAmountPlayersAndStrategy() {
        if (YOEP) {
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
            makeMove();
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
            }
        }
    }
}
