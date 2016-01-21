/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Tile;
import nl.tiesdavid.ssproject.game.exceptions.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

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

        public void close() {
            this.running = false;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    String line = in.readLine();
                    if (!line.equals("")) {
                        System.out.println("Command received: " + line
                                + "\n\tFrom: " + inetAddress);
                        clientHandler.handleMessage(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); //TODO: Disconnect client.
            }
        }
    }

    private static final boolean DEBUG = true;

    public static final int UNACCEPTABLE_NAME_ERROR = 2;
    public static final int NONEXISTING_PLAYER_ERROR = 0;
    public static final int SERVER_UNSUPPORTED_COMMAND_ERROR = -492;
    public static final int PLAYER_UNSUPPORTED_COMMAND_ERROR = -429;
    public static final int WRONG_COMMAND_ERROR = 0;
    public static final String ERROR_COMMAND = "error";

    public static final String HELLO_COMMAND = "hello";
    public static final String GENERAL_CHAT_COMMAND = "chat";
    public static final String PRIVATE_CHAT_COMMAND = "chatpm";
    public static final String CREATE_CHALLENGE_COMMAND = "challenge";
    public static final String ACCEPT_CHALLENGE_COMMAND = "accept";
    public static final String START_CHALLENGE_COMMAND = "setUp";
    public static final String DECLINE_CHALLENGE_COMMAND = "decline";
    public static final String WAIT_FOR_GAME_COMMAND = "join";
    public static final String PLACE_COMMAND = "place";
    public static final String TRADE_COMMAND = "trade";

    public static final String CHAT_OPTION = "chat";
    public static final String CHALLENGE_OPTION = "challenge";

    private final Lobby lobby;
    private OnlineGame currentGame;

    private final InetAddress inetAddress;

    private ArrayList<String> options;

    private String name;
    private boolean initialized;
    private boolean disconnected;

    private final BufferedReader in;
    private final BufferedWriter out;

    public ClientHandler(Socket socket, Lobby lobby) throws IOException {
        this.lobby = lobby;

        this.inetAddress = socket.getInetAddress();

        this.options = new ArrayList<>();

        this.initialized = false;
        this.disconnected = false;

        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void assignGame(OnlineGame game) {
        this.currentGame = game;
    }

    public void disconnectFromGame() {
        this.currentGame = null;
    }

    public void sendMessageToClient(String messageToBeSend) {
        try {
            out.write(messageToBeSend);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() {
        if (currentGame != null) {
            currentGame.disconnectClient(this); //TODO
        }
        lobby.disconnectClient(this);
        disconnected = true;
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
            sendErrorMessage(UNACCEPTABLE_NAME_ERROR);
        } catch (ExistingNameException e) {
            sendErrorMessage(Lobby.NAME_ALREADY_EXISTS_ERROR);
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
        } catch (UnsupportedOptionException e) {
            e.printStackTrace();
            sendErrorMessage(PLAYER_UNSUPPORTED_COMMAND_ERROR);
        } catch (NonexistingPlayerException e) {
            e.printStackTrace();
            sendErrorMessage(NONEXISTING_PLAYER_ERROR);
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
            sendErrorMessage(WRONG_COMMAND_ERROR);
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
        printDebugMessage("Placing tiles...");
        if (messageParts.length % 2 != 1 || messageParts.length < 3
                || currentGame == null) {
            sendWrongCommandMessage();
            return;
        }

        ArrayList<Tile> tiles = new ArrayList<>();
        
        for (int i = 1; i < messageParts.length; i = i + 2) {
            String tileString = messageParts[i];
            String locationString = messageParts[i + 1];
            try {
                Tile tile = Tile.fromProtocolString(tileString, locationString);
                printDebugMessage("Tile: " + tile.toLongString());
                tiles.add(tile);
            } catch (UnparsableDataException e) {
                sendWrongCommandMessage();
                return;
            }
        }

        try {
            ArrayList<Tile> tilesToBeDealed = currentGame.place(this, tiles); //TODO
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

        try {
            ArrayList<Tile> tilesToBeDealed = currentGame.trade(this, tiles); //TODO
        } catch (MoveException e) {
            sendWrongCommandMessage();
        }
    }

    private void handleMessage(String message) {
        String[] messageParts = message.split(" ");
        String command = messageParts[0].toLowerCase();

        if (!initialized && !command.equals(HELLO_COMMAND)) {
            sendWrongCommandMessage();
            return;
        }

        switch (command) {
            case HELLO_COMMAND:
                initClient(messageParts);
                break;
            case GENERAL_CHAT_COMMAND:
                distributeGeneralChatMessage(messageParts);
                break;
            case PRIVATE_CHAT_COMMAND:
                distributePrivateChatMessage(messageParts);
                break;
            case CREATE_CHALLENGE_COMMAND:
                createChallenge(messageParts);
                break;
            case ACCEPT_CHALLENGE_COMMAND:
                acceptChallenge(messageParts);
                break;
            case DECLINE_CHALLENGE_COMMAND:
                declineChallenge(messageParts);
                break;
            case START_CHALLENGE_COMMAND:
                startChallenge(messageParts);
                break;
            case WAIT_FOR_GAME_COMMAND:
                waitForGame(messageParts);
                break;
            case PLACE_COMMAND:
                placeTiles(messageParts);
                break;
            case TRADE_COMMAND:
                tradeTiles(messageParts);
                break;
        }
        System.out.println(getPlayerName() + ": Finished handling command: " + message);
    }

    public void sendWrongCommandMessage() {
        sendErrorMessage(WRONG_COMMAND_ERROR);
    }

    private void sendErrorMessage(int error) {
        //TODO: Use variable error message.
        sendMessageToClient(ERROR_COMMAND + " " + Integer.toString(error));
    }

    private void setPlayerName(String newName) throws UnacceptableNameException {
        if (newName.contains(" ") || newName.contains("/") || newName.contains("\\")) {
            throw new UnacceptableNameException(newName);
        }
        this.name = newName;
    }

    public String getPlayerName() {
        return this.name;
    }

    public String getOptionsString() {
        String string = "";

        if (this.options.size() > 0) {
            for (int i = 0; i < this.options.size() - 1; i++) {
                string += this.options.get(i) + " ";
            }
            string += this.options.get(options.size() - 1);
        }

        return string;
    }

    private void parseOptions(String[] newOptions) {
        this.options.addAll(Arrays.asList(newOptions));
    }

    public boolean hasOption(String option) {
        return this.options.contains(option);
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
