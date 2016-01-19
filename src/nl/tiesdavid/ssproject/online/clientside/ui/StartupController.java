package nl.tiesdavid.ssproject.online.clientside.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class StartupController {
    public TextField textField;

    public void hois(ActionEvent e) {
        System.out.println(textField.getCharacters());
    }
}
