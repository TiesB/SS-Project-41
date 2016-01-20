/**
 * Created by Ties on 19-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import nl.tiesdavid.ssproject.online.clientside.ui.UIController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class ClientController extends Observable implements Observer {
    public static final int STARTUP = 0;
    public static final int LOBBY = 1;
    public static final int GAME = 2;

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

    public void connect(String username, String serverIP, int serverPort) {
        InetAddress serverIPAddress = null;
        Socket socket = null;

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

        this.username = username;

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
