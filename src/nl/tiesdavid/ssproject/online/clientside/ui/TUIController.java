/**
 * Created by Ties on 23-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ui;

import javafx.util.Pair;
import nl.tiesdavid.ssproject.online.Protocol;
import nl.tiesdavid.ssproject.online.clientside.ClientController;

import java.util.*;

public class TUIController implements Observer {
    private ClientController clientController;
    private Map<String, ArrayList<String>> playersWithFeatures;

    public TUIController(ClientController clientController) {
        this.clientController = clientController;
        this.playersWithFeatures = new HashMap<>();
    }

    private void takeTurn() {
        //TODO
    }

    private void printMessage(boolean notification, String message) {
        System.out.println((notification ? " --- " : "")
                + message
                + (notification ? " --- " : ""));
    }

    private void printPlayersOnServer() {
        String message = "Current players on server: ";
        int i = 0;
        for (String player : playersWithFeatures.keySet()) {
            message += (i == 0 ? "" : ',') + player;
            i++;
        }
        printMessage(true, message);
    }

    private void printBoard() {
        //TODO
    }

    private void showError(Exception e) {
        System.out.println(e.getMessage());
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
                    && !part.toLowerCase().equals(Protocol.CHALLENGE_FEATURE)) {
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
            printMessage(true, currentPlayer + " has joined the server.");
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
        printMessage(true, player + " has disconnected from the server.");
    }

    private void receiveStartGameCommand(String[] messageParts) {
        String message = "Starting game with: ";
        for (int i = 1; i < messageParts.length; i++) {
            message += (i == 1 ? "" : ',') + messageParts[i];
        }
        printMessage(false, message);
    }

    private void receiveTurnCommand(String[] messageParts) {
        if (messageParts.length < 2) {
            return;
        }

        String player = messageParts[1];
        if (player.equals(clientController.getUsername())) {
            takeTurn();
        } else {
            printMessage(false, "It's " + player + "'"
                    + (player.toCharArray()[player.length() - 1] == 's' ? "" : 's') + " turn!");
        }
    }

    private void receiveNewStonesCommand(String[] messageParts) {
        //TODO
    }

    private void receivePlacedCommand(String[] messageParts) {
        if (messageParts.length < 4) {
            return;
        }

        String player = messageParts[1];
        int count = (messageParts.length - 2) / 2;
        printMessage(false, player + " has placed " + Integer.toString(count) + " tiles.");

        printBoard();
    }

    private void receiveTradedCommand(String[] messageParts) {
        if (messageParts.length < 3) {
            return;
        }

        printMessage(false, messageParts[1] + " has traded " + messageParts[2] + " tiles.");
    }

    private void receiveEndGameCommand(String[] messageParts) {
        Pair<String, Integer> winner = clientController.getPreviousScore()
                .descendingIterator().next();
        printMessage(false, winner.getKey() + " has won with " + Integer.toString(winner.getValue()) + " points!");
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
