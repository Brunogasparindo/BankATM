package ATM;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class DestinationUserDialog {
    /*
    * Destination User ID
    * */
    @FXML
    private TextField destinationUserID;

    /*
    * Dialog process result
    * @return   UUID input in the Dialog Pane
    * */
    public String processUserID()
    {
        return destinationUserID.getText();
    }
}
