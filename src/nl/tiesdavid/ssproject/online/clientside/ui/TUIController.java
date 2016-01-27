/**
 * Created by Ties on 23-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ui;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.UnparsableDataException;
import nl.tiesdavid.ssproject.online.Protocol;
import nl.tiesdavid.ssproject.online.clientside.ClientController;

import java.util.*;

public class TUIController extends Thread implements Observer {
    private Scanner scanner;

    private final ClientController clientController;
    private final Map<String, ArrayList<String>> playersWithFeatures;

    public TUIController(ClientController clientController) {
        this.clientController = clientController;
        this.playersWithFeatures = new HashMap<>();
    }

    @Override
    public void run() {
        scanner = new Scanner(System.in);
        init();
        try {
            Thread.sleep(100); // So that the
        } catch (InterruptedException ignored) {
        }
        askIfPlayerWantsToJoin();
    }

    private void init() {
        String username = readUsername();
        printMessage(false, "Enter the server IP address: ");
        String serverIP = readString();
        printMessage(false, "Enter the server port: ");
        int serverPort = readInt("Invalid port number. Please try again.", 0, Integer.MAX_VALUE);
        clientController.parseGeneralStartupResult(this, serverIP, serverPort);
        clientController.setUsername(username);
    }

    private void askIfPlayerWantsToJoin() {
        printMessage(false, "With how many players would you like to play? ");
        int amount = readInt("That's not a valid amount. Choose 2-4.", 1, 5);
        clientController.sendJoinCommand(amount);
    }

    private void takeTurn() {
        printDeck();
        printMessageLine(false, "Do you want to:");
        printMessageLine(false, " (1) Place (a) tile(s).");
        printMessageLine(false, " (2) Trade (a) tile(s).");
        int response = readInt("That's not a valid choice. Choose 1 or 2.", 0, 3);
        switch (response) {
            case 1:
                placeTiles();
                break;
            case 2:
                tradeTiles();
                break;
        }
    }

    private void placeTiles() {
        ArrayList<Tile> tiles = readTilesForMove();
        if (tiles != null) {
            clientController.sendPlaceCommand(tiles);
        }
    }

    private void tradeTiles() {
        ArrayList<Tile> tiles = readTilesForMove();
        if (tiles != null) {
            clientController.sendTradeCommand(tiles);
        }
    }

    /**
     * Offers the player a choice to change the type of move.
     * @param reason The reason why the player is offered this choice.
     * @return true when the player wants to make a different move.
     */
    private boolean offerCancelMoveChoice(String reason) {
        printMessageLine(false, reason);
        printMessageLine(false, "Do you want to make a different move?");
        printMessageLine(false, "Enter 1 for no, 2 for yes.");
        int response = readInt("That's not a valid choice. Choose 1 or 2.", 0, 3);
        switch (response) {
            case 1:
                return false;
            case 2:
                return true;
            default:
                return true;
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

    private void printPlayersOnServer() {
        String message = "Current players on server: ";
        int i = 0;
        for (String player : playersWithFeatures.keySet()) {
            if (!player.equals(clientController.getUsername())) {
                message += (i == 0 ? "" : ',') + player;
                i++;
            }
        }
        printMessageLine(true, message);
    }

    private void printBoard() {
        //TODO
    }

    private void printDeck() {
        String message = "Your current deck is:";
        ObservableList deck = clientController.getCurrentGame().getDeck();
        for (int i = 0; i < deck.size(); i++) {
            message += " (" + Integer.toString(i) + ") " + deck.get(i).toString();
        }
        printMessageLine(false, message);
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

    private ArrayList<Tile> readTilesForMove() {
        ArrayList<Tile> tiles = new ArrayList<>();
        boolean read = false;
        while (!read) {
            tiles = readTiles();
            if (tiles.size() > 0) {
                read = true;
            } else {
                boolean wantsToMakeDifferentMove =
                        offerCancelMoveChoice("You have entered 0 tiles.");
                if (wantsToMakeDifferentMove) {
                    takeTurn();
                    return null;
                }
            }
        }
        return tiles;
    }

    private ArrayList<Tile> readTiles() {
        ArrayList<Tile> tiles = new ArrayList<>();
        ArrayList<Tile> tempDeck = new ArrayList<>(clientController.getCurrentGame().getDeck());
        while (tiles.size() < 6
                && tiles.size() < clientController.getCurrentGame().getAmountOfTilesInBag()) {
            printMessageLine(false, "Which tile do you want to place?");
            printMessage(true, "Choose one per time, by entering the corresponding number: ");
            int response = readInt("That's not a valid choice. Choose 0 to 5.", -1, 6);
            Tile tile = clientController.getCurrentGame().getTileFromDeck(response);
            if (tile != null && tempDeck.contains(tile)) {
                tiles.add(tile);
                tempDeck.remove(tile);
            } else if (!tempDeck.contains(tile)) {
                printMessageLine(false, "You have already chosen this tile.");
            }
        }
        return tiles;
    }

    private String readUsername() {
        printMessage(false, "Enter your username: ");
        String username = readString();
        if (!username.contains("\\") && !username.contains(" ")) {
            return username;
        } else {
            printMessageLine(false, "Invalid username. Please enter again.");
            return readUsername();
        }
    }

    private void showError(Exception e) {
        System.out.println(e.getMessage());
    }

    // TODO: 27-1-2016 Fix that tui still receives message when asking for input.

    private void receiveWelcomeCommand(String[] messageParts) {
        printMessageLine(true, "You have succesfully joined the server.");
    }

    private void receiveJoinCommand(String[] messageParts) {
        if (messageParts.length < 2) {
            return;
        }

        String currentPlayer = "";
        int playersAdded = 0;
        for (int i = 1; i < messageParts.length; i++) {
            String part = messageParts[i];
            if (!part.toLowerCase().equals(Protocol.CHAT_FEATURE)
                    && !part.toLowerCase().equals(Protocol.CHALLENGE_FEATURE)
                    && !part.toLowerCase().equals(Protocol.LEADERBOARD_FEATURE)) {
                currentPlayer = part;
                playersAdded++;
                if (!playersWithFeatures.containsKey(currentPlayer)) {
                    playersWithFeatures.put(currentPlayer, new ArrayList<>());
                }
            } else {
                if (playersWithFeatures.get(currentPlayer) != null) {
                    if (!playersWithFeatures.get(currentPlayer).contains(part)) {
                        playersWithFeatures.get(currentPlayer).add(part);
                    }
                }
            }
        }

        if (playersAdded == 1) {
            if (!currentPlayer.equals(clientController.getUsername())) {
                printMessageLine(true, currentPlayer + " has joined the server.");
            }
        } else {
            printPlayersOnServer();
        }
    }

    private void receiveDisconnectCommand(String[] messageParts) {
        if (messageParts.length < 2) {
            return;
        }

        String player = messageParts[1];
        playersWithFeatures.remove(player);
        printMessageLine(true, player + " has disconnected from the server.");
    }

    private void receiveStartGameCommand(String[] messageParts) {
        String message = "Starting game with: ";
        for (int i = 1; i < messageParts.length; i++) {
            message += (i == 1 ? "" : ',') + messageParts[i];
        }
        printMessageLine(false, message);
    }

    private void receiveTurnCommand(String[] messageParts) {
        if (messageParts.length < 2) {
            return;
        }

        String player = messageParts[1];
        if (player.equals(clientController.getUsername())) {
            printMessageLine(false, "It's your turn!");
            takeTurn();
        } else {
            printMessageLine(false, "It's " + player + "'"
                    + (player.toCharArray()[player.length() - 1] == 's' ? "" : 's') + " turn!");
        }
    }

    private void receiveNewStonesCommand(String[] messageParts) {
        String message = "You have received:";
        for (int i = 1; i < messageParts.length; i++) {
            try {
                message += (i == 1 ? " " : ", ")
                        + Tile.fromProtocolString(messageParts[i]).toString();
            } catch (UnparsableDataException e) {
                message += "";
            }
        }
        printMessageLine(false, message);
        printDeck();
    }

    private void receivePlacedCommand(String[] messageParts) {
        if (messageParts.length < 4) {
            return;
        }

        String player = messageParts[1];
        int count = (messageParts.length - 2) / 2;
        printMessageLine(false, player + " has placed " + Integer.toString(count) + " tiles.");

        printBoard();
    }

    private void receiveTradedCommand(String[] messageParts) {
        if (messageParts.length < 3) {
            return;
        }

        printMessageLine(false, messageParts[1] + " has traded " + messageParts[2] + " tiles.");
    }

    private void receiveEndGameCommand(String[] messageParts) {
        ArrayList<Pair<String, Integer>> previousScore = clientController.getPreviousScore();
        previousScore.sort(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                return Integer.compare(o1.getValue(), o2.getValue());
            }
        });
        Pair<String, Integer> winner = clientController.getPreviousScore().
                get(previousScore.size() - 1);
        printMessageLine(false, winner.getKey() + " has won with "
                + Integer.toString(winner.getValue()) + " points!");
    }

    @Override
    public void update(Observable o, Object arg) {
        synchronized (this) {
            if (arg instanceof Exception) {
                showError((Exception) arg);
            } else if (arg instanceof String) {
                String[] parts = ((String) arg).split(" ");
                String command = parts[0];
                switch (command) {
                    case Protocol.SERVER_WELCOME_COMMAND:
                        receiveWelcomeCommand(parts);
                        break;
                    case Protocol.SERVER_JOIN_COMMAND:
                        receiveJoinCommand(parts);
                        break;
                    case Protocol.SERVER_DISCONNECT_COMMAND:
                        receiveDisconnectCommand(parts);
                        break;
                    case Protocol.SERVER_START_GAME_COMMAND:
                        receiveStartGameCommand(parts);
                        break;
                    case Protocol.SERVER_TURN_COMMAND:
                        receiveTurnCommand(parts);
                        break;
                    case Protocol.SERVER_NEW_STONES_COMMAND:
                        receiveNewStonesCommand(parts);
                        break;
                    case Protocol.SERVER_PLACED_COMMAND:
                        receivePlacedCommand(parts);
                        break;
                    case Protocol.SERVER_TRADED_COMMAND:
                        receiveTradedCommand(parts);
                        break;
                    case Protocol.SERVER_END_GAME_COMMAND:
                        receiveEndGameCommand(parts);
                        break;
                }
            }
        }
    }
}
