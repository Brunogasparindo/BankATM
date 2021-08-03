package ATM;

import ATM.bank.Account;
import ATM.bank.Bank;
import ATM.bank.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AccountDialog {
    /*
    * Name of the new Account
    * */
    @FXML
    private TextField accType;

    /*
    * Create a new Account for the logged in User
    * @return   created Account
    * */
    public Account processNewAcc()
    {
        String accountType = accType.getText();
        User aUser = Controller.currentUser;
        Bank theBank = Controller.authBank;

        // add an account for our user
        Account newAcc = new Account(accountType, aUser, theBank);
        aUser.addAccount(newAcc);
        theBank.addAccount(newAcc);

        return newAcc;
    }
}