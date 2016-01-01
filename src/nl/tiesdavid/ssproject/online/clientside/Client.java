package nl.tiesdavid.ssproject.online.clientside;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A client for a sloppily programmed recipe server.
 *
 */
public class Client {
    private static final String USAGE = "usage: <address> <port>";

    /**
     * @param args
     */
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
        System.out.println("Choose your name: ");
        String name = userIn.next();

        HumanClientPlayer player = new HumanClientPlayer(name);
        
        String command = "SHOW_BOARD";


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
            
            int rNum = -1;
            
            out.write(command);
            out.newLine();
            out.flush();
            List<String> stringList = new ArrayList<String>();
            String line = in.readLine();
            while (line != null && !line.equals("")) {
            	stringList.add(line);
            	line = in.readLine();
            }
            do {
                System.out.println("Current state of the board:");
            	for (String lineToPrint : stringList) {
            		System.out.printf("%s" + System.lineSeparator(), lineToPrint);
				}
	            System.out.print("Enter recipe number (or 0 to exit): ");
	            System.out.flush();

                command = player.getCommand();

	            if (!command.equals("EXIT")) {
		            out.write(command);
		            out.newLine();
		            out.flush();
		            System.out.println("System response: ");
		            System.out.println("------");
		            line = in.readLine();
		            while (line != null && !line.equals("--EOT--")) {
		            	// The server uses a special string ("--EOT--") to mark the end of a recipe.
		            	player.parseResponse(line);
		            	line = in.readLine();
		            }
		            System.out.println("------");
	            } else {
	            	System.out.println("Invalid recipe number, try again.");
	            }
            } while (!command.equals("EXIT"));
            System.out.println("Exiting.");
            userIn.close();
        } catch (IOException e) {
            System.out.println("ERROR: unable to communicate to server");
            e.printStackTrace();
        }   
    }
}
