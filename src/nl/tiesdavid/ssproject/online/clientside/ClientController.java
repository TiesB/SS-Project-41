/**
 * Created by Ties on 19-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import javafx.util.Pair;
import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.NonexistingPlayerException;
import nl.tiesdavid.ssproject.game.exceptions.UnparsableDataException;
import nl.tiesdavid.ssproject.online.Protocol;
import nl.tiesdavid.ssproject.online.clientside.ai.AIPlayer;
import nl.tiesdavid.ssproject.online.clientside.ui.GUIController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class ClientController extends Observable implements Observer {
    private static final boolean USE_AI = false;
    private static final boolean USE_GUI = false;

    private static final String[] FEATURES = new String[] {Protocol.CHAT_FEATURE};

    // Control
    private CommunicationController commOps;
    private AIPlayer aiPlayer;
    private GUIController guiController;
    private String[] serverFeatures;
    private Map<String, ArrayList<String>> playersInServer;

    // Game
    private String username;
    private ClientGame currentGame;
    private TreeSet<Pair<String, Integer>> previousScore;

    public ClientController() {
        this.playersInServer = new HashMap<>();
        this.previousScore = new TreeSet<>();
        if (USE_AI) {
            aiPlayer = new AIPlayer(this);
            //TODO
            addObserver(aiPlayer);
        } else if (USE_GUI) {
            guiController = new GUIController(this);
            guiController.start();
            addObserver(guiController);
        } else {

        }
    }

    private void init() {
        if (commOps != null) {
            sendHelloCommand();
        }
    }

    public void parseUIStartupResult(ArrayList<String> result) {
        if (result.size() < 3) {
            return;
        }

        int serverPort = Integer.parseInt(result.get(2));

        connect(result.get(0), result.get(1), serverPort);

        init();
    }

    public void connect(String newUsername, String serverIP, int serverPort) {
        InetAddress serverIPAddress;
        Socket socket;

        try {
            serverIPAddress = InetAddress.getByName(serverIP);
        } catch (UnknownHostException e) {
            //TODO
            return;
        }

        try {
            socket = new Socket(serverIPAddress, serverPort);
        } catch (IOException e) {
            //TODO
            return;
        }

        this.username = newUsername;

        try {
            setSocket(socket);
        } catch (IOException e) {
            //TODO
            return;
        }
    }

    public void setSocket(Socket socket) throws IOException {
        this.commOps = new CommunicationController(this, socket);
    }

    public String getUsername() {
        return username;
    }

    public ClientGame getCurrentGame() {
        return currentGame;
    }

    public TreeSet<Pair<String, Integer>> getPreviousScore() {
        return previousScore;
    }

    public CommunicationController getCommOps() {
        return commOps;
    }

    public AIPlayer getAiPlayer() {
        return aiPlayer;
    }

    public GUIController getGuiController() {
        return guiController;
    }

    public void showError(Exception e) {
        setChanged();
        notifyObservers(e);
    }

    // Sending commands
    private void sendHelloCommand() {
        String message = Protocol.CLIENT_HELLO_COMMAND + username;
        for (String option : FEATURES) {
            message += " " + option;
        }
        commOps.sendMessage(message);
    }

    public void sendJoinCommand(int amount) {
        String message = Protocol.CLIENT_WAIT_FOR_GAME_COMMAND
                + " " + Integer.toString(amount);
        commOps.sendMessage(message);
    }

    public void sendGeneralChatCommand(String message) {
        String messageToServer = Protocol.CLIENT_GENERAL_CHAT_COMMAND
                + " " + message;
        commOps.sendMessage(messageToServer);
    }

    public void sendPrivateChatCommand(String recipient, String message) {
        String messageToServer = Protocol.CLIENT_PRIVATE_CHAT_COMMAND
                + " " + recipient + " " + message;
        commOps.sendMessage(messageToServer);
    }

    public void sendPlaceCommand(ArrayList<Tile> tiles) {
        String message = Protocol.CLIENT_PLACE_COMMAND;

        for (Tile tile : tiles) {
            message += " " + tile.toProtocolForm();
        }

        commOps.sendMessage(message);
    }

    public void sendTradeCommand(ArrayList<Tile> tiles) {
        String message = Protocol.CLIENT_TRADE_COMMAND;

        for (Tile tile : tiles) {
            message += " " + tile.toProtocolForm();
        }

        commOps.sendMessage(message);
    }

    // Receiving commands
    private void receiveWelcomeCommand(String[] messageParts) {
        serverFeatures = Arrays.copyOfRange(messageParts, 1, messageParts.length);
    }

    private void receivePlayersCommand(String[] messageParts) {
        if (messageParts.length >= 2) {
            for (int i = 1; i < messageParts.length; i++) {
                playersInServer.put(messageParts[i], new ArrayList<>());
            }
        }
    }

    private void receiveJoinCommand(String[] messageParts) {
        //TODO: Onduidelijke specificatie.
    }

    private void receiveDisconnectCommand(String[] messageParts) {
        if (messageParts.length < 2) {
            return;
        }
        playersInServer.remove(messageParts[1]);
    }

    private void receiveStartGameCommand(String[] messageParts) {
        currentGame = new ClientGame();
        for (int i = 1; i < messageParts.length; i++) {
            if (!messageParts[i].equals(username)) {
                currentGame.addPlayer(messageParts[i]);
            }
        }
    }

    private void receiveNewStonesCommand(String[] messageParts) {
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
        int score = 0;
        try {
            score = Integer.parseInt(messageParts[2]);
        } catch (NumberFormatException e) {
            return;
        }

        try {
            currentGame.raiseScore(player, score);
        } catch (NonexistingPlayerException e) {
            return;
        }

        int amount = 0;

        for (int i = 3; i < messageParts.length; i = i + 2) {
            String tileString = messageParts[i];
            String locationString = messageParts[i + 1];
            Tile tile;
            try {
                tile = Tile.fromProtocolString(tileString, locationString);
            } catch (UnparsableDataException e) {
                System.out.println(e.getMessage());
                return;
            }
            currentGame.placeTile(tile);
        }
    }

    private void receiveEndGameCommand(String[] messageParts) {
        if (currentGame != null) {
            previousScore = currentGame.getPlayersWithScores();
            currentGame = null;
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
                case Protocol.SERVER_PLAYERS_COMMAND:
                    receivePlayersCommand(parts);
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
                case Protocol.SERVER_NEW_STONES_COMMAND:
                    receiveNewStonesCommand(parts);
                    break;
                case Protocol.SERVER_PLACED_COMMAND:
                    receivePlacedCommand(parts);
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

        setChanged(); // Forward messages to UI.
        notifyObservers(arg);
    }
}
