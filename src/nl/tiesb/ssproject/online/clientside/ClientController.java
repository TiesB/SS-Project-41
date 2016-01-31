/**
 * Created by Ties on 19-1-2016.
 */
package nl.tiesb.ssproject.online.clientside;

import javafx.util.Pair;
import nl.tiesb.ssproject.game.Player;
import nl.tiesb.ssproject.game.Tile;
import nl.tiesb.ssproject.game.exceptions.NonexistingPlayerException;
import nl.tiesb.ssproject.game.exceptions.UnparsableDataException;
import nl.tiesb.ssproject.online.Protocol;
import nl.tiesb.ssproject.online.clientside.ai.AIPlayer;
import nl.tiesb.ssproject.online.clientside.ui.ChatController;
import nl.tiesb.ssproject.online.clientside.ui.TUIController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class ClientController implements Observer {
    public static final boolean DEBUG = false;

    private static final String[] FEATURES = new String[] {Protocol.CHAT_FEATURE};

    // Control
    private final boolean useAI;
    private CommunicationController commOps;
    private final ArrayList<Observer> observers;
    private final ArrayList<String> serverFeatures;

    // UI
    ChatController chatController;

    // Game
    private String username;
    private ClientGame currentGame;
    private ArrayList<Tile> tilesToBeTraded;
    private ArrayList<Pair<String, Integer>> previousScore;

    public ClientController(boolean useAI) {
        this.useAI = useAI;
        this.observers = new ArrayList<>();
        this.serverFeatures = new ArrayList<>();
        this.tilesToBeTraded = new ArrayList<>();
        this.previousScore = new ArrayList<>();
        startChat();
        startUI();
    }

    private void startChat() {
        chatController = new ChatController(this);
        chatController.start();
        addObserver(chatController); // TODO: 27-1-2016 Look at serverFeatures
    }

    private void startUI() {
        if (useAI) {
            AIPlayer aiPlayer = new AIPlayer(this);
            aiPlayer.start();
            addObserver(aiPlayer);
        } else {
            TUIController tuiController = new TUIController(this);
            tuiController.start();
            addObserver(tuiController);
        }

        // TODO: 24-1-2016 Implement ChatController
    }

    public void close() {
        System.exit(0);
    }

    public void parseGeneralStartupResult(Observer observer,
                                           String serverIP, int serverPort) {
        try {
            connect(serverIP, serverPort);
        } catch (IOException e) {
            deleteObserver(observer);
            startUI();
        }
    }

    public void setUsername(String newUsername) {
        username = newUsername;
        sendHelloCommand();
    }

    private void connect(String serverIP, int serverPort) throws IOException {
        InetAddress serverIPAddress;
        Socket socket;

        serverIPAddress = InetAddress.getByName(serverIP);

        socket = new Socket(serverIPAddress, serverPort);

        setSocket(socket);
    }

    private void setSocket(Socket socket) throws IOException {
        try {
            commOps = new CommunicationController(this, socket);
        } catch (IOException e) {
            e.printStackTrace();
            this.commOps = null;
            throw e;
        }
    }

    public String getUsername() {
        return username;
    }

    public ClientGame getCurrentGame() {
        return currentGame;
    }

    public ArrayList<Pair<String, Integer>> getPreviousScore() {
        return previousScore;
    }

    public boolean isConnected() {
        return commOps != null;
    }

    public void addObserver(Observer observer) {
        if (commOps == null) {
            observers.add(observer);
        } else {
            commOps.addObserver(observer);
        }
    }

    public ArrayList<Observer> getObservers() {
        return observers;
    }

    public void deleteObserver(Observer observer) {
        if (commOps != null) {
            commOps.deleteObserver(observer);
        } else {
            observers.remove(observer);
        }
    }

    public void setCommOps(CommunicationController commOps) {
        this.commOps = commOps;
    }

    // Sending commands
    private synchronized void sendMessage(String message) {
        if (commOps != null) {
            commOps.sendMessage(message);
        }
    }

    private void sendHelloCommand() {
        String message = Protocol.CLIENT_HELLO_COMMAND + " " + username;
        for (String option : FEATURES) {
            message += " " + option;
        }
        sendMessage(message);
    }

    public void sendJoinCommand(int amount) {
        String message = Protocol.CLIENT_WAIT_FOR_GAME_COMMAND
                + " " + Integer.toString(amount);
        sendMessage(message);
    }

    public void sendGeneralChatCommand(String message) {
        if (DEBUG) {
            System.out.println("[DEBUG] Sending general chat message...");
        }

        String messageToServer = Protocol.CLIENT_GENERAL_CHAT_COMMAND
                + " " + message;
        sendMessage(messageToServer);
    }

    public void sendPrivateChatCommand(String recipient, String message) {
        if (DEBUG) {
            System.out.println("[DEBUG] Sending private chat message...");
        }

        String messageToServer = Protocol.CLIENT_PRIVATE_CHAT_COMMAND
                + " " + recipient + " " + message;
        sendMessage(messageToServer);
    }

    public void sendPlaceCommand(ArrayList<Tile> tiles) {
        String message = Protocol.CLIENT_PLACE_COMMAND;

        for (Tile tile : tiles) {
            if (!tile.hasXY()) {
                return;
            }
            message += " " + tile.toProtocolForm();
        }

        sendMessage(message);
    }

    public void sendTradeCommand(ArrayList<Tile> tiles) {
        String message = Protocol.CLIENT_TRADE_COMMAND;

        for (Tile tile : tiles) {
            message += " " + tile.toShortProtocolForm();
            tilesToBeTraded.add(tile);
        }

        sendMessage(message);
    }

    // Receiving commands
    private void receiveWelcomeCommand(String[] messageParts) {
        for (int i = 1; i < messageParts.length; i++) {
            if (DEBUG) {
                System.out.println("[DEBUG] Added server feature: " + messageParts[i]);
            }
            serverFeatures.add(messageParts[i]);
        }
        if (serverFeatures.contains(Protocol.CHAT_FEATURE)) {
            startChat();
        }
    }

    private void receiveStartGameCommand(String[] messageParts) {
        if (DEBUG) {
            System.out.println("[DEBUG] Starting new game.");
        }

        ArrayList<String> players = new ArrayList<>();

        players.addAll(Arrays.asList(messageParts).subList(1, messageParts.length));

        if (players.contains(username)) {
            currentGame = new ClientGame();
            for (String player : players) {
                currentGame.addPlayer(player);
                currentGame.decreaseAmountOfTilesInBag(Player.DECK_SIZE);
            }
        }
    }

    private void receiveNewStonesCommand(String[] messageParts) {
        if (messageParts.length < 2) {
            if (DEBUG) {
                System.out.println("[DEBUG] No tiles received. Bag is probably empty.");
            }
        }

        if (currentGame != null) {
            for (int i = 1; i < messageParts.length; i++) {
                try {
                    Tile tile = Tile.fromProtocolString(messageParts[i]);
                    currentGame.addTileToDeck(tile);
                } catch (UnparsableDataException e) {
                    return;
                }
            }
        }
    }

    private void receivePlacedCommand(String[] messageParts) {
        if (messageParts.length < 5 || currentGame == null) {
            return;
        }
        String player = messageParts[1];

        int score;
        try {
            score = Integer.parseInt(messageParts[2]);
        } catch (NumberFormatException e) {
            return;
        }

        try {
            currentGame.raiseScore(player, score);
            if (DEBUG) {
                System.out.println(player + " score: " + currentGame.getScore(player));
            }
        } catch (NonexistingPlayerException e) {
            System.out.println(e.getMessage());
            return;
        }

        for (int i = 3; i < messageParts.length; i = i + 2) {
            String tileString = messageParts[i];
            String locationString = messageParts[i + 1];
            Tile tile;
            try {
                if (DEBUG) {
                    System.out.println("Parsing tile that is placed: " + tileString);
                }
                tile = Tile.fromProtocolString(tileString, locationString);
            } catch (UnparsableDataException e) {
                System.out.println(e.getMessage());
                return;
            }
            currentGame.placeTile(tile);
            if (player.equals(username)) {
                if (DEBUG) {
                    System.out.println("[DEBUG] Removing tile " + tile + " from " + player);
                    System.out.println("[DEBUG] " + username + " = " + player);
                }
                currentGame.removeTileFromDeck(tile);
            }
        }
    }

    private void receiveTradedCommand(String[] messageParts) {
        if (messageParts.length < 3) {
            return;
        }

        String player = messageParts[1];
        if (player.equals(username)) {
            currentGame.removeTilesFromDeck(tilesToBeTraded);
            tilesToBeTraded.clear();
        }
    }

    private void receiveEndGameCommand(String[] messageParts) {
        if (DEBUG) {
            System.out.println("[DEBUG] Game ended.");
        }

        if (currentGame != null) {
            previousScore = currentGame.getPlayersWithScores();
            currentGame = new ClientGame();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String[] parts = ((String) arg).split(" ");
            String command = parts[0];
            switch (command) {
                case Protocol.SERVER_WELCOME_COMMAND:
                    receiveWelcomeCommand(parts);
                    break;
                case Protocol.SERVER_START_GAME_COMMAND:
                    receiveStartGameCommand(parts);
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
                /**
                 * When it's any other than one of these commands,
                 * it is only useful for the UI.
                 *
                 * The UI gets the messages after the ClientController on purpose.
                 * This way the ClientController can organize the data, before
                 * the UI reads it.
                 */
            }
        }
    }
}
