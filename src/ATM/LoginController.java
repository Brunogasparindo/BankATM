package ATM;

import ATM.bank.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class LoginController {
    /*
    * User ID text field
    * */
    @FXML
    private TextField userId;

    /*
    * pin text field
    * */
    @FXML
    private TextField pin;

    /*
    * Dialog Pane process result
    * @return   User from the @Controller.authBank variable
    * */
    public User processResult()
    {
        String userID = userId.getText();
        String pinNumber = pin.getText();

        if (userID.equals("") || pinNumber.equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("At least one field empty");
            alert.setHeaderText(null);
            alert.setContentText("Please insert your user ID and your PIN.");
            alert.showAndWait();
            return null;
        }

        return Controller.authBank.userLogin(userID, pinNumber);
    }
}
