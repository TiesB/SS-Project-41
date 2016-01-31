/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesb.ssproject.online.serverside;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Thread {
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
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
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        int port = 0;
        Scanner scanner = new Scanner(System.in);

        while (port == 0) {
            System.out.print("Enter the port number the server sould run on: ");
            int input = scanner.nextInt();
            if (input <= 0) {
                System.out.println("Enter a value above 0.");
            } else {
                port = input;
            }
        }

        System.out.println("Server starting...");
        new Server(port).start();
    }
}
