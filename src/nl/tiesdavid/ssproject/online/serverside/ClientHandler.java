/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.exceptions.ExistingNameException;
import nl.tiesdavid.ssproject.game.exceptions.NonexistingPlayerException;
import nl.tiesdavid.ssproject.game.exceptions.UnacceptableNameException;
import nl.tiesdavid.ssproject.game.exceptions.UnsupportedOptionException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler extends Thread {
    public static final int UNACCEPTABLE_NAME_ERROR = -69;
    public static final int NONEXISTING_PLAYER_ERROR = -321;
    public static final int SERVER_UNSUPPORTED_COMMAND_ERROR = -492;
    public static final int PLAYER_UNSUPPORTED_COMMAND_ERROR = -429;
    public static final String HELLO_COMMAND = "hello";
    public static final String GENERAL_CHAT_COMMAND = "chat";
    public static final String PRIVATE_CHAT_COMMAND = "chatpm";
    public static final String CHAT_OPTION = "chat";
    public static final String CHALLENGE_OPTION = "challenge";

    private final Lobby lobby;
    private OnlineGame currentGame;

    private final InetAddress inetAddress;

    private ArrayList<String> options;

    private String name;
    private boolean disconnected;

    private final BufferedReader in;
    private final BufferedWriter out;

    public ClientHandler(Socket socket, Lobby lobby) throws IOException {
        this.lobby = lobby;

        this.inetAddress = socket.getInetAddress();

        this.options = new ArrayList<>();

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
            currentGame.disconnectPlayer(null); //TODO
        }
        lobby.disconnectClient(this);
        disconnected = true;
    }

    private void initClient(String[] messageParts) {
        try {
            setPlayerName(messageParts[1]);
            parseOptions(Arrays.copyOfRange(messageParts, 2, messageParts.length));
            lobby.connectClient(this);
        } catch (UnacceptableNameException e) {
            sendErrorMessage(UNACCEPTABLE_NAME_ERROR);
        } catch (ExistingNameException e) {
            sendErrorMessage(Lobby.NAME_ALREADY_EXISTS_ERROR);
        }
    }

    private void distributeGeneralChatMessage(String[] messageParts) {
        if (messageParts.length < 2) {
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

    private void parseOptions(String[] options) {
        this.options.addAll(Arrays.asList(options));
    }

    private void handleMessage(String message) {
        String[] messageParts = message.split(" ");
        String command = messageParts[0].toLowerCase();
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
        }
    }

    private void sendErrorMessage(int error) {
        //TODO: Use variable error message.
        sendMessageToClient("error " + Integer.toString(error));
    }

    private void setPlayerName(String name) throws UnacceptableNameException {
        if (name.contains(" ") || name.contains("/") || name.contains("\\")) {
            throw new UnacceptableNameException(name);
        }
        this.name = name;
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

    public boolean hasOption(String option) {
        return this.options.contains(option);
    }

    public boolean isInGame() {
        return currentGame != null;
    }

    public OnlineGame getCurrentGame() {
        return this.currentGame;
    }

    @Override
    public void run() {
        try {
            String receivedMessage = in.readLine();
            while (receivedMessage != null && !disconnected) {
                System.out.println("Command received: " + receivedMessage + "\n\tFrom: " + inetAddress);
                handleMessage(receivedMessage);
                receivedMessage = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
