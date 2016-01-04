package nl.tiesdavid.ssproject.online.serverside;

import nl.tiesdavid.ssproject.game.Game;
import nl.tiesdavid.ssproject.game.exceptions.MoveException;

import java.io.*;
import java.net.Socket;


public class ClientHandler extends Thread {
    final static String INIT_COMMAND = "INIT";
    final static String SHOW_BOARD_COMMAND = "SHOW_BOARD";
    final static String MAKE_MOVE_COMMAND = "MAKE_MOVE";
    final static String EXIT_COMMAND = "EXIT";

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

    public void startMove() {
        try {
            showBoard(out);
            out.write(MAKE_MOVE_COMMAND);
            player.sendDeck(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    	} else if (msg.startsWith(INIT_COMMAND)) {
            this.player = new OnlinePlayer("Online: " + msg.split(" ")[1], game, this);
            game.addPlayer(this.player);
            this.player.sendDeck(out);
            showBoard(out);
        } else if (msg.equals(EXIT_COMMAND)){
    		System.out.println("EXITING.");
            shutdown();
    		//TODO
    	} else {
            try {
                int result = player.handleCommand(msg);
                out.write(result + System.lineSeparator());
            } catch (MoveException e) {
                out.write(e.getMessage());
            }
            out.flush();
            out.write("--EOT--");
            out.flush();
    	}
    }

    private void showBoard(Writer out) throws IOException {
        game.getBoard().deepCopy().printBoard(out);
    }
    
    private void shutdown() {
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
