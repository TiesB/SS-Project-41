/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.exceptions.ExistingNameException;
import nl.tiesdavid.ssproject.game.exceptions.UnacceptableNameException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler extends Thread {
    public static final int UNACCEPTABLE_NAME_ERROR = -69;
    public static final String HELLO_COMMAND = "hello";

    private final Lobby lobby;

    private final InetAddress inetAddress;

    private String[] options;

    private String name;
    private boolean disconnected;

    private final BufferedReader in;
    private final BufferedWriter out;

    private OnlineGame currentGame;

    public ClientHandler(Socket socket, Lobby lobby) throws IOException {
        this.lobby = lobby;

        this.inetAddress = socket.getInetAddress();

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
            disconnect();
        }
    }

    private void disconnect() {
        currentGame.disconnectPlayer(null); //TODO
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

    private void parseOptions(String[] options) {
        this.options = options;
    }

    private void handleMessage(String message) {
        String[] messageParts = message.split(" ");
        String command = messageParts[0];
        if (command.equals(HELLO_COMMAND)) {
            initClient(messageParts);
        }
    }

    private void sendErrorMessage(int error) {
        sendMessageToClient("error" + Integer.toString(error));
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

    public String[] getOptionsString() {
        String string = "";

        if (this.options.length > 0) {
            for (int i = 0; i < this.options.length - 1; i++) {
                string += this.options[i];
            }
            string += this.options[options.length - 1];
        }

        return string;
    }

    public boolean isInGame() {
        return currentGame != null;
    }

    @Override
    public void run() {
        String receivedMessage;
        try {
            receivedMessage = in.readLine();
            while (receivedMessage != null && !disconnected) {
                System.out.println("Command received: " + receivedMessage + "\n\tFrom: " + inetAddress);
                handleMessage(receivedMessage);
            }
        } catch (IOException e) {
            disconnect();
        }
    }
}
