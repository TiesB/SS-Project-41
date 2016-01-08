package nl.tiesdavid.ssproject.online.clientside;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * A client for a sloppily programmed recipe server.
 *
 */
public class Client {
    private static final String USAGE = "usage: <address> <port>";

    private static void makeMove(HumanClientPlayer player, BufferedReader in, BufferedWriter out)
            throws IOException {
        String command = player.getCommand();
        if (!command.equals("")) {
            out.write(command);
            out.newLine();
            out.flush();
            String line = in.readLine();
            while (line != null && !line.startsWith("--EOT--")) {
                // The server uses a special string ("--EOT--") to mark the end of a transmission.
                int res = player.parseResponse(line);

                if (res != 0) {
                    makeMove(player, in, out);
                }

                line = in.readLine();
            }
            System.out.println("------");
        } else {
            System.out.println("Invalid command, try again.");
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(0);
        }
        
        InetAddress addr = null;
        int port = 0;
        Socket socket = null;

        // check args[1] - the IP-adress
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println(USAGE);
            System.out.println("ERROR: host " + args[0] + " unknown");
            System.exit(0);
        }

        // parse args[2] - the port
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println(USAGE);
            System.out.println("ERROR: port " + args[1] + " is not an integer");
            System.exit(0);
        }

        Scanner userIn = new Scanner(System.in);
        System.out.print("Choose your name: ");
        String name = userIn.next();

        HumanClientPlayer player = new HumanClientPlayer(name);
        
        String command = "INIT " + name;


        // try to open a Socket to the server
        try {
            socket = new Socket(addr, port);
        } catch (IOException e) {
            System.out.println("ERROR: could not create a socket on " + addr
                    + " and port " + port);
        }
        
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()));

            out.write(command);
            out.newLine();
            out.flush();
            String line = in.readLine();
            do {
                while (line != null && !line.equals("")) {
                    player.parseResponse(line);
                    line = in.readLine();
                }
                System.out.println("Waiting till it's our turn...");
                while (!line.startsWith("MAKE_MOVE")) {
                    try {
                        Thread.sleep(100);
                        line = in.readLine();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
                player.parseResponse(line);
                makeMove(player, in, out);
            } while (!command.equals("EXIT"));
            System.out.println("Exiting.");
            userIn.close();
        } catch (IOException e) {
            System.out.println("ERROR: unable to communicate to server");
            e.printStackTrace();
        }   
    }
}
