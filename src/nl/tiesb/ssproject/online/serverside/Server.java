/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesb.ssproject.online.serverside;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server extends Thread {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        ServerSocket serverSocket = null;
        while (serverSocket == null) {
            try {
                int port = 0;
                while (port == 0) {
                    System.out.print("Enter the port number the server sould run on: ");
                    int input = scanner.nextInt();
                    if (input <= 0) {
                        System.out.println("Enter a value above 0.");
                    } else {
                        port = input;
                    }
                }

                serverSocket =
                        new ServerSocket(port);

                Lobby lobby = new Lobby();

                System.out.println("Waiting for players to connect...");

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected from "
                            + socket.getInetAddress().toString() + "!");
                    new ClientHandler(socket, lobby).start();
                }
            } catch (BindException e) {
                System.out.println("This port is already in use.");
            } catch (IllegalArgumentException e) {
                System.out.println("This is an invalid port.");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
