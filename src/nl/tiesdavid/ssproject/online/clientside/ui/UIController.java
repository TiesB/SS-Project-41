/**
 * Created by Ties on 20-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ui;

import nl.tiesdavid.ssproject.online.clientside.ClientController;
import nl.tiesdavid.ssproject.online.clientside.ui.guiviews.StartupDialog;

import java.util.Observable;
import java.util.Observer;

public class UIController extends Thread implements Observer {
    private ClientController clientController;

    public UIController(ClientController clientController) {
        this.clientController = clientController;
    }

    private void startStartup() {
        StartupDialog startupDialog = new StartupDialog();
        startupDialog.startUI(clientController);
        System.out.println("Lola");
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void run() {
        startStartup();
    }
}
