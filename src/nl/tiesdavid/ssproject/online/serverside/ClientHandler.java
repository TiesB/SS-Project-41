/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.*;
import nl.tiesdavid.ssproject.online.Protocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHandler extends Thread {
    private static class Reader extends Thread {
        private ClientHandler clientHandler;
        private InetAddress inetAddress;
        private BufferedReader in;

        private boolean running;

        public Reader(ClientHandler clientHandler, InetAddress inetAddress, BufferedReader in) {
            this.clientHandler = clientHandler;
            this.inetAddress = inetAddress;
            this.in = in;

            this.running = true;
        }

        @Override
        public void run() {
            try {
                String line = in.readLine();
                while (line != null) {
                    if (!line.equals("")) {
                        System.out.println("Command received: " + line
                                + " from: " + inetAddress);
                        clientHandler.handleMessage(line);
                    }
                    line = in.readLine();
                }
            } catch (IOException | NullPointerException e) {
                System.out.println("Lost connection with "
                        + clientHandler.getPlayerName() + " @ " + inetAddress);
            } finally {
                clientHandler.disconnect();
            }
        }
    }

    public static final boolean DEBUG = true;

    public static final int NONEXISTING_PLAYER_ERROR = 0;
    public static final int PLAYER_UNSUPPORTED_COMMAND_ERROR = -429;

    private final Lobby lobby;
    private OnlineGame currentGame;

    private final InetAddress inetAddress;

    private ArrayList<String> playerFeatures;

    private String name;
    private boolean initialized;

    private final BufferedReader in;
    private final BufferedWriter out;

    public ClientHandler(Socket socket, Lobby lobby) throws IOException {
        this.lobby = lobby;

        this.inetAddress = socket.getInetAddress();

        this.playerFeatures = new ArrayList<>();

        this.initialized = false;

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void assignGame(OnlineGame game) {
        this.currentGame = game;
    }

    public void disconnectFromGame() {
        printDebugMessage("Disconnected from game.");
        this.currentGame = null;
    }

    public void sendMessageToClient(String messageToBeSend) {
        try {
            if (DEBUG) {
                System.out.println("Sending message to " + name + ": " + messageToBeSend);
            }
            out.write(messageToBeSend);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            if (currentGame != null) {
                disconnect();
            }
            currentGame = null;
        }
    }

    private void disconnect() {
        if (currentGame != null) {
            currentGame.disconnectClient(this); //TODO
            disconnectFromGame();
        }
        lobby.disconnectClient(this);
    }

    private void initClient(String[] messageParts) {
        if (initialized || messageParts.length < 2) {
            sendWrongCommandMessage();
            return;
        }

        try {
            setPlayerName(messageParts[1]);
            parseOptions(Arrays.copyOfRange(messageParts, 2, messageParts.length));
            lobby.connectClient(this);

            initialized = true;
        } catch (UnacceptableNameException e) {
            sendErrorMessage(Protocol.UNACCEPTABLE_NAME_ERROR);
        } catch (ExistingNameException e) {
            sendErrorMessage(Protocol.NAME_ALREADY_EXISTS_ERROR);
        }
    }

    private void distributeGeneralChatMessage(String[] messageParts) {
        if (messageParts.length < 2) {
            sendWrongCommandMessage();
            return;
        }

        String message = "";

        for (int i = 1; i < messageParts.length - 1; i++) {
            message += messageParts[i] + " ";
        }
        message += messageParts[messageParts.length - 1];

        lobby.sendGeneralChatMessage(this, message);
    }

    private void distributePrivateChatMessage(String[] messageParts) {
        if (messageParts.length < 3) {
            sendWrongCommandMessage();
            return;
        }

        String receiver = messageParts[1];

        String message = "";

        for (int i = 2; i < messageParts.length - 1; i++) {
            message += messageParts[i] + " ";
        }
        message += messageParts[messageParts.length - 1];

        try {
            lobby.sendPrivateChatMessage(this, receiver, message);
        } catch (UnsupportedOptionException e) {
            e.printStackTrace();
            sendErrorMessage(PLAYER_UNSUPPORTED_COMMAND_ERROR);
        } catch (NonexistingPlayerException e) {
            e.printStackTrace();
            sendErrorMessage(NONEXISTING_PLAYER_ERROR);
        }
    }

    private void createChallenge(String[] messageParts) {
        if (messageParts.length < 2) {
            sendWrongCommandMessage();
            return;
        }

        String[] invitedPlayers = Arrays.copyOfRange(messageParts, 1, messageParts.length);
        try {
            lobby.createChallenge(this, invitedPlayers);
        } catch (UnsupportedOptionException | NonexistingPlayerException e) {
            e.printStackTrace();
            sendWrongCommandMessage();
        }
    }

    private void acceptChallenge(String[] messageParts) {
        if (messageParts.length < 2) {
            sendWrongCommandMessage();
            return;
        }

        try {
            int challengeId = Integer.parseInt(messageParts[1]);
            lobby.acceptChallenge(this, challengeId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendWrongCommandMessage();
        }
    }

    private void declineChallenge(String[] messageParts) {
        if (messageParts.length < 2) {
            sendWrongCommandMessage();
            return;
        }

        try {
            int challengeId = Integer.parseInt(messageParts[1]);
            lobby.declineChallenge(this, challengeId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendWrongCommandMessage();
        }
    }

    private void startChallenge(String[] messageParts) {
        if (messageParts.length < 2) {
            sendWrongCommandMessage();
            return;
        }

        try {
            int challengeId = Integer.parseInt(messageParts[1]);
            lobby.startChallenge(this, challengeId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendErrorMessage(Protocol.WRONG_COMMAND_ERROR);
        }
    }

    private void waitForGame(String[] messageParts) {
        if (currentGame != null || messageParts.length < 2) {
            sendWrongCommandMessage();
            return;
        }

        try {
            int requestedNo = Integer.parseInt(messageParts[1]);

            List<Integer> allowedNos = Arrays.asList(2, 3, 4);

            if (!allowedNos.contains(requestedNo)) {
                sendWrongCommandMessage();
                return;
            }

            lobby.waitForGame(this, requestedNo);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sendWrongCommandMessage();
        }
    }

    private void placeTiles(String[] messageParts) {
        if (messageParts.length % 2 != 1 || messageParts.length < 3
                || currentGame == null) {
            sendWrongCommandMessage();
            return;
        }

        printDebugMessage("Placing tiles...");

        ArrayList<Tile> tiles = new ArrayList<>();
        
        for (int i = 1; i < messageParts.length; i = i + 2) {
            String tileString = messageParts[i];
            String locationString = messageParts[i + 1];
            try {
                printDebugMessage("Going to parse tile with: " + tileString + " @ " + locationString);
                Tile tile = Tile.fromProtocolString(tileString, locationString);
                printDebugMessage("Tile: " + tile.toLongString());
                tiles.add(tile);
            } catch (UnparsableDataException e) {
                printDebugMessage("Unparsable data: " + tileString + " @ " + locationString);
                sendWrongCommandMessage();
                return;
            }
        }

        try {
            currentGame.place(this, tiles); //TODO
        } catch (MoveException e) {
            printDebugMessage(e.getMessage());
            sendWrongCommandMessage();
        }
    }

    private void tradeTiles(String[] messageParts) {
        if (messageParts.length < 2
                || currentGame == null) {
            sendWrongCommandMessage();
            return;
        }

        ArrayList<Tile> tiles = new ArrayList<>();

        for (int i = 1; i < messageParts.length; i++) {
            String tileString = messageParts[i];
            try {
                tiles.add(Tile.fromProtocolString(tileString));
            } catch (UnparsableDataException e) {
                sendWrongCommandMessage();
                return;
            }
        }

        if (DEBUG) {
            System.out.println("[DEBUG] Trying to trade tiles: " + tiles);
        }

        try {
            currentGame.trade(this, tiles);
        } catch (MoveException e) {
            if (DEBUG) {
                System.out.println("[DEBUG] Actual deck: " +getCurrentGame().getPlayer(this).getDeck());
                System.out.println("[DEBUG] " + e.getMessage());
            }
            if (e instanceof NoTilesLeftInBagException) {
                currentGame.shutdown();
            }
            sendWrongCommandMessage();
        }
    }

    private void dealTiles(ArrayList<Tile> tiles) {

    }

    private void handleMessage(String message) {
        String[] messageParts = message.split(" ");
        String command = messageParts[0].toLowerCase();

        if (!initialized && !command.equals(Protocol.CLIENT_HELLO_COMMAND)) {
            sendWrongCommandMessage();
            return;
        }

        switch (command) {
            case Protocol.CLIENT_HELLO_COMMAND:
                initClient(messageParts);
                break;
            case Protocol.CLIENT_GENERAL_CHAT_COMMAND:
                distributeGeneralChatMessage(messageParts);
                break;
            case Protocol.CLIENT_PRIVATE_CHAT_COMMAND:
                distributePrivateChatMessage(messageParts);
                break;
            case Protocol.CLIENT_CREATE_CHALLENGE_COMMAND:
                createChallenge(messageParts);
                break;
            case Protocol.CLIENT_ACCEPT_CHALLENGE_COMMAND:
                acceptChallenge(messageParts);
                break;
            case Protocol.CLIENT_DECLINE_CHALLENGE_COMMAND:
                declineChallenge(messageParts);
                break;
            case Protocol.CLIENT_START_CHALLENGE_COMMAND:
                startChallenge(messageParts);
                break;
            case Protocol.CLIENT_WAIT_FOR_GAME_COMMAND:
                waitForGame(messageParts);
                break;
            case Protocol.CLIENT_PLACE_COMMAND:
                placeTiles(messageParts);
                break;
            case Protocol.CLIENT_TRADE_COMMAND:
                tradeTiles(messageParts);
                break;
        }
        System.out.println(getPlayerName() + ": Finished handling command: " + message);
    }

    public void sendWrongCommandMessage() {
        sendErrorMessage(Protocol.WRONG_COMMAND_ERROR);
    }

    private void sendErrorMessage(int error) {
        sendMessageToClient(Protocol.SERVER_ERROR_COMMAND + " " + Integer.toString(error));
    }

    private void setPlayerName(String newName) throws UnacceptableNameException {
        if (newName.contains(" ") || newName.contains("/") || newName.contains("\\")
                || newName.toLowerCase().equals("you")) {
            throw new UnacceptableNameException(newName);
        }
        this.name = newName;
    }

    public String getPlayerName() {
        return this.name;
    }

    public String getOptionsString() {
        String string = "";

        if (this.playerFeatures.size() > 0) {
            for (int i = 0; i < this.playerFeatures.size() - 1; i++) {
                string += this.playerFeatures.get(i) + " ";
            }
            string += this.playerFeatures.get(playerFeatures.size() - 1);
        }

        return string;
    }

    private void parseOptions(String[] newOptions) {
        this.playerFeatures.addAll(Arrays.asList(newOptions));
    }

    public boolean hasOption(String option) {
        return this.playerFeatures.contains(option);
    }

    public boolean isInGame() {
        return currentGame != null;
    }

    public OnlineGame getCurrentGame() {
        return this.currentGame;
    }

    private void printDebugMessage(String message) {
        if (DEBUG) {
            System.out.println(getPlayerName() + ": " + message);
        }
    }

    @Override
    public void run() {
        new Reader(this, inetAddress, in).start();  //Using this so the reader will continue,
                                                    //even when a command is being processed.
    }
}
