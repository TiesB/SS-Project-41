/**
 * Created by Ties on 19-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.ReentrantLock;

public class CommunicationController extends Observable {
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
                                System.out.println("[DEBUG] Observers: " + controller.countObservers());
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
        for (Observer observer : clientControllerObservers) {
            addObserver(observer);
        }
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
