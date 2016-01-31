/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesb.ssproject.online.clientside;

public class AIClient extends Thread {
    @Override
    public void run() {
        new ClientController(true);
    }

    public static void main(String[] args) {
        new AIClient().start();
    }
}
