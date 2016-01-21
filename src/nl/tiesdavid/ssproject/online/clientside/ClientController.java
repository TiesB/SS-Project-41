/**
 * Created by Ties on 19-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import nl.tiesdavid.ssproject.online.clientside.ui.UIController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ClientController extends Observable implements Observer {
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

    private static final String[] OPTIONS = new String[] {CHAT_OPTION, CHALLENGE_OPTION};

    // Control
    private CommunicationController commOps;
    private UIController uiController;

    // Game
    private String username;

    public ClientController() {
        uiController = new UIController(this);
        uiController.start();
        addObserver(uiController);
    }

    private void init() {
        if (commOps != null) {
            String message = HELLO_COMMAND + username;
            for (String option : OPTIONS) {
                message += " " + option;
            }
            commOps.sendMessage(message);
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

    public CommunicationController getCommOps() {
        return commOps;
    }

    public UIController getUiController() {
        return uiController;
    }

    public void showError(Exception e) {
        setChanged();
        notifyObservers(e);
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg); //TODO: Game logic and protocol logic goes here.
        //When something isn't interesting to the UI, it doesn't need to know.
    }
}
