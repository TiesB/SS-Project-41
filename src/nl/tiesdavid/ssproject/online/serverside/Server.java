package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.exceptions.NotEnoughPlayersException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * A sloppily programmed recipe server. 
 *
 */
public class Server extends Thread {
    class StartCommando extends Thread {
        private Game game;

        StartCommando(Game game) {
            this.game = game;
        }

        @Override
        public void run() {
            Scanner adminIn = new Scanner(System.in);
            boolean running = true;
            System.out.println("When ready, type \"START\" to begin the game.");
            while (running) {
                if (adminIn.next().equals("START")) {
                    try {
                        game.play();
                        running = false;
                    } catch (NotEnoughPlayersException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    private int port;
    
    public Server(int port) {
        this.port = port;
    }
    
    public void run() {
        try {
            System.out.println("Port: " + port);
            ServerSocket ssock = new ServerSocket(port, 0, InetAddress.getByName("127.0.0.1"));

            BufferedReader adminIn = new BufferedReader(new InputStreamReader(System.in));

            Game game = new Game();
        	// If you want external hosts (i.e., not this computer) to connect to 
        	// this server you can use the following line instead.
        	// ServerSocket ssock = new ServerSocket(port);

            new StartCommando(game).start();

            System.out.println("Waiting for players to connect...");
            while (true) {
                Socket sock = ssock.accept();
                System.out.println("Client connected!");
                ClientHandler handler = new ClientHandler(sock, game);
                handler.start();
                try {
                    Thread.sleep(100);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private static final String USAGE = "Expected parameter: <port>";
     
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(0);
        }
        
        String portString = args[0];
        int port = 0;
        
        // parse portnumber
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            System.out.println(USAGE);
            System.out.println("ERROR: port " + portString + " is not an integer");
            System.exit(0);
        }
        
        // And start the server
        Server s = new Server(port);
        System.out.println("Server starting...");
        s.start();
    }
}
