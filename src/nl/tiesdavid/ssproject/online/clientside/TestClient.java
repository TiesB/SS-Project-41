/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

//TODO: Remove.
public class TestClient extends Thread {
    private InetAddress inetAddress;
    private int port;

    public TestClient(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    @Override
    public void run() {
        ClientController clientController = new ClientController();

        try {
            Socket socket = new Socket(inetAddress, port);
            clientController.setSocket(socket);
        } catch (IOException e) {
            clientController.showError(e);
        }
    }

    public static void main(String[] args) {
        try {
            new TestClient(InetAddress.getByName("127.0.0.1"), 3339).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
