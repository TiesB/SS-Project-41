/**
 * Created by Ties on 19-1-2016.
 */
package nl.tiesb.ssproject.online.clientside;

import nl.tiesb.ssproject.online.clientside.ui.ChatController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.ReentrantLock;

public class CommunicationController extends Observable {
    public static final String CLIENT_CONNECTED = "connected";

    private static class Reader extends Thread {
        private final CommunicationController controller;
        private final BufferedReader in;

        public Reader(CommunicationController controller, BufferedReader in) {
            this.controller = controller;
            this.in = in;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String line = in.readLine();
                    if (line != null) {
                        if (!line.equals("")) {
                            if (ClientController.DEBUG) {
                                System.out.println("[DEBUG] Observers: " +
                                        controller.countObservers());
                                System.out.println("[DEBUG] Received: " + line);
                            }
                            controller.setChanged();
                            controller.notifyObservers(line);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Lost connection with the server.");
                    break;
                }
            }
            System.out.println("Exiting the program...");
            System.exit(0);
        }
    }

    private BufferedWriter out;

    private ReentrantLock lock;

    public CommunicationController(ClientController clientController, Socket socket)
            throws IOException {
        addObserver(clientController);

        this.lock = new ReentrantLock();

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        new Reader(this, in).start();

        ArrayList<Observer> clientControllerObservers = clientController.getObservers();
        ArrayList<Observer> remainingObservers = new ArrayList<>();
        for (Observer observer : clientControllerObservers) {
            if (!(observer instanceof ChatController)) {
                remainingObservers.add(observer);
            } else {
                addObserver(observer);
            }
        }
        remainingObservers.forEach(this::addObserver);

        clientController.setCommOps(this);
        setChanged();
        notifyObservers(CLIENT_CONNECTED);
    }

    public void sendMessage(String message) {
        lock.lock();
        if (ClientController.DEBUG) {
            System.out.println("[DEBUG] Sending: " + message);
        }
        try {
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            System.out.println("Error in sending message (" + message + "): " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
