package ATM;

import ATM.bank.Account;
import ATM.bank.Bank;
import ATM.bank.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class NewUserController {
    /*
    * First Name text field
    * */
    @FXML
    private TextField fName;

    /*
    * Last Name text field
    * */
    @FXML
    private TextField lName;

    /*
    * Created pin
    * */
    @FXML
    private TextField newPin;

    /*
    * Create a new User
    * @return   created User
    * */
    public User processResult()
    {
        String firstName = fName.getText();
        String lastName = lName.getText();
        String pinNew = newPin.getText();

        if (firstName.equals("") || lastName.equals("") || pinNew.equals(""))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("At least one field empty");
            alert.setHeaderText(null);
            alert.setContentText("Please insert your first and last name and create your PIN.");
            alert.showAndWait();
            return null;
        }

        Bank bank = Controller.authBank; // CHANGE according to a list of banks!!! \\

        // add a user, which also creates a savings account
        User user = bank.addUser(firstName, lastName, pinNew);


        // add a start saving account for our user
        Account newAcc = new Account("savings starter package", user, bank);
        user.addAccount(newAcc);
        bank.addAccount(newAcc);

        return user;
    }
}
