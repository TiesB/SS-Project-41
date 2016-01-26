/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.serverside;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            /* TODO
            Replace with:
                new ServerSocket(port);
            when actually going online.
             */
            ServerSocket serverSocket =
                    new ServerSocket(port);

            Lobby lobby = new Lobby();

            System.out.println("Waiting for players to connect...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected from "
                        + socket.getInetAddress().toString() + "!");
                new ClientHandler(socket, lobby).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String USAGE = "Arguments: <portnumber>";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(-1);
        }

        int port = 0;

        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println(USAGE);
            System.exit(-1);
        }

        System.out.println("Server starting...");
        new Server(port).start();
    }
}
