/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesb.ssproject.online.clientside;

public class Client extends Thread {

    public Client() {
    }

    @Override
    public void run() {
        new ClientController();
    }

    public static void main(String[] args) {
        new Client().start();
    }
}
