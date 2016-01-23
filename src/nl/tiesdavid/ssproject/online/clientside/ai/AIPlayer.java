/**
 * Created by Ties on 22-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ai;

import nl.tiesdavid.ssproject.online.clientside.ClientController;

import java.util.Observable;
import java.util.Observer;

public class AIPlayer implements Observer {
    private ClientController clientController;

    public AIPlayer(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String line = (String) arg;


        }
    }
}
