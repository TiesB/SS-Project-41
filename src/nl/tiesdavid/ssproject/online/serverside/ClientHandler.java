package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Game;

import java.io.*;
import java.net.Socket;


public class ClientHandler extends Thread {
    private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;
    private Game game;
    private OnlinePlayer player;
    
    public ClientHandler(Socket sock, Game game) throws IOException {
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        this.sock = sock;
        this.game = game;
        this.player = new OnlinePlayer("Online: " + sock.getInetAddress(), game);
    }
    
    public void run() {
        String msg;
        try {
            msg = in.readLine();
            while (msg != null) {
                System.out.println("Command received: " + msg + " from: " + sock.getInetAddress());
                handleCommand(msg, out);
                out.newLine();
                out.flush();
                msg = in.readLine();                
            }
            shutdown();
        } catch (IOException e) {
            // For now, ignore and let thread stop.
        }

    }
    
    final static String SHOW_BOARD_COMMAND = "SHOW_BOARD";
    final static String GET_COMMAND = "GET";
    /**
     * Handle server commands
     * @param msg command from client
     * @param out Writer to to write the result to.
     * @throws IOException 
     */
    private void handleCommand(String msg, Writer out) throws IOException {
    	if (msg.equals(SHOW_BOARD_COMMAND)) {
    		System.out.println("Showing board.");
    		showBoard(out);
    	} else if (msg.startsWith(GET_COMMAND + " ")){
    		System.out.println("Showing recipe.");
    		String recipeName = msg.substring(GET_COMMAND.length() + 1);
    		showRecipe(recipeName, out);
    	} else {
    		out.write("ERROR: unknown command.");
    	}
    }

    private void showBoard(Writer out) throws IOException {
        game.getBoard().printBoard(out);
    }

    /**
     * List available recipes.
     * @param out
     * @throws IOException
     */
    private void listRecipes(Writer out) throws IOException {
    	File[] files = new File("recipes").listFiles();
    	for (File file : files) {
			out.write(file.getName() + System.lineSeparator());
		}
    }
    
    /**
     * Retrieve a recipe and output to client.
     * @param recipeName
     * @param out
     * @throws IOException 
     */
    private void showRecipe(String recipeName, Writer out) throws IOException {
    	String recipeFilename = "recipes" + File.separator + recipeName;
    	System.out.println("Sending " + recipeFilename);
    	BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(recipeFilename));
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}

    	try {
    		String line = br.readLine();

    		while (line != null) {
				out.write(line + System.lineSeparator());
    			line = br.readLine();
    		}
    	} finally {
    		br.close();
    	}
    	// This silly server uses a special string to signal it is done
    	// sending lines.
    	out.write("--EOT--");
    }
    
    private void shutdown() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
