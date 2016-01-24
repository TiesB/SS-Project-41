/**
 * Created by Ties on 20-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside.ui;

import nl.tiesdavid.ssproject.online.clientside.ClientController;
import nl.tiesdavid.ssproject.online.clientside.ui.guiviews.StartupDialog;

import java.util.Observable;
import java.util.Observer;

public class GUIController extends Thread implements Observer {
    private ClientController clientController;

    public GUIController(ClientController clientController) {
        this.clientController = clientController;
    }

    private void startStartup() {
        StartupDialog startupDialog = new StartupDialog();
        startupDialog.startUI(clientController, this);
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
