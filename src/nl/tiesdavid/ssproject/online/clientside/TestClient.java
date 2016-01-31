/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

public class TestClient extends Thread {

    public TestClient() {
    }

    @Override
    public void run() {
        new ClientController();
    }

    public static void main(String[] args) {
        new TestClient().start();
    }
}
