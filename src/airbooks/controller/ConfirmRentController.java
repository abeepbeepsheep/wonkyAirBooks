package airbooks.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ConfirmRentController {
    @FXML
    private TextField lockerNoLabel;
    @FXML
    private TextField passwordLabel;
    @FXML
    private TextField postalCodeLabel;

    @FXML
    private void aboutAction(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/airbooks/fxml/about.fxml"));
        Stage window = new Stage();
        window.setScene(new Scene(root));
        window.setTitle("About");
        window.initModality(Modality.WINDOW_MODAL);
        window.initOwner(((Hyperlink)e.getSource()).getScene().getWindow());
        window.showAndWait();
    }

    public void init(int lockerNo, String password, String postal) {
        lockerNoLabel.setText(String.valueOf(lockerNo));
        passwordLabel.setText(password);
        postalCodeLabel.setText(postal);
    }
}
