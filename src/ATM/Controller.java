package ATM;

import ATM.bank.Account;
import ATM.bank.Bank;
import ATM.bank.Transaction;
import ATM.bank.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Controller {
    /*
    * Login status at the ATM
    * */
    private boolean loggedIn = false;

    /*
    * Logged in Account
    * */
    private Account currentAccount;

    /*
    * Destination account in case of money transfer*/
    private Account receiverAccount;

    /*
    * Selected bank in the AT
    * */
    public static Bank authBank = null;

    /*
    * Logged in User*/
    public static User currentUser = null;

    /*
    * List of Users available to transfer money to
    * */
    public static List<User> users = new ArrayList<>();

    /*
    * Label with account information in the User Interface ATM
    * */
    @FXML
    private Label accountTextField;

    /*
    * Label with showing the bank name in the User Interface ATM
    * */
    @FXML
    private Label bankName;

    /*
    * User interface pane
    * Used mainly for the dialog pane when necessary: 'mainPane.getScene().getWindow()'
    * */
    @FXML
    private Pane mainPane;

    /*
    * Label with a welcome text for the User
    * */
    @FXML
    private Label welcomeText;

    /*
    * Label containing the balance of the selected account
    * */
    @FXML
    private Label balanceLabel;

    /*
    * ATM Scream where the user sees the transaction history of the selected account
    * */
    @FXML
    private ListView<String> transactionHistory;

    /*
    * Button 'Choose your bank'
    * Shows all options of bank to use in this ATM
    * Set the @authBank to the instance of the selected bank
    * Set the @BankName to the selected bank
    * */
    @FXML
    public void changeBankClick()
    {
        if (loggedIn)
        {
            onLogoutClick();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Previous user logged out");
            alert.setHeaderText(null);
            alert.setContentText("User logged out. Please login with your User ID or create a new User.");
            alert.showAndWait();
            changeBankClick();
        }

        List<String> arrayBanks = new ArrayList<>();
        for (Bank bank : Main.banks)
        {
            arrayBanks.add(bank.getName());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(arrayBanks.get(0), arrayBanks);
        dialog.setTitle("BANK SELECTION");
        dialog.setHeaderText("Select your bank");

        Optional<String> result = dialog.showAndWait();
        String name = "notValid";
        if (result.isPresent() && !result.get().equals(name))
        {
            name = result.get();
            Bank bank = Main.getBank(name);
            if (bank != null)
            {
                authBank = bank;
                bankName.setText(authBank.getName());
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Not bank selected. Please try again.");
                alert.showAndWait();
                changeBankClick();
            }
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Not bank selected. Please try again.");
            alert.showAndWait();
        }
    }

    /*
    * Button 'Login'
    * Set the @currentUser if UserId and Password matches
    * */
    @FXML
    public void onLoginClick()
    {
        if (authBank == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Bank selected");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a bank institution to continue.");
            alert.showAndWait();
            return;
        }

        /*
        * Opens login Dialog Pane
        * */
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Login");
        dialog.setHeaderText("Use your ID number to login");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("loginDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        // add btn 'OK' and 'cancel' to the Dialog Pane
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        // Process the result
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LoginController loginDialog = fxmlLoader.getController();
            User user = loginDialog.processResult();
            if (user == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login fail");
                alert.setHeaderText("Incorrect user ID/pin combination. Please check the selected bank Id and pin and try again.");

                alert.showAndWait();

                onLoginClick();
            } else {
                loggedIn = true;
                currentUser = user;
                if (currentUser.numAccounts() != 0)
                {
                    currentAccount = currentUser.getAccounts().get(0);
                }

                showTransactions();
                welcomeMessage(user);
            }
        }
    }

    /*
    * Button 'New User'
    * Create a new User and the set the @currentUser to this new User
    * */
    @FXML
    public void onNewUserClick() throws IOException {
        if (authBank == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Bank selected");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a bank institution to continue.");
            alert.showAndWait();
            return;
        }

        /*
         * Opens new user Dialog Pane
         * */
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("New User");
        dialog.setHeaderText("Please inform your first name, last name and set your pin");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("newUserDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        // Add btn OK and cancel to the Dialog Pane
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        // process the result
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NewUserController newUserDialog = fxmlLoader.getController();
            User user = newUserDialog.processResult();
            if (user == null) {
                onNewUserClick();
            } else {
                loggedIn = true;
                currentUser = user;
                currentAccount = user.getAccounts().get(0);
                users.add(user);
                updatedBankDB();
                updateAccountDB();
                showTransactions();
                welcomeMessage(user);
            }
        }
    }

    /*
    * Button 'Add Account'
    * Add a new Account to the logged @currentUser
    * */
    @FXML
    public void onAddAccClick() throws IOException {
        if (authBank == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Bank selected");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a bank institution to continue.");
            alert.showAndWait();
            return;
        }

        if (!loggedIn)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No User selected");
            alert.setHeaderText(null);
            alert.setContentText("Please log in to continue.");
            alert.showAndWait();
            return;
        }

        /*
        * Opens the account Dialog Pane
        * */
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("ACCOUNT");
        dialog.setHeaderText("Use 'OK' to add a new account.\n\n\n" +
                "Current account " + currentAccount.getAccountName() + ": " +
                currentAccount.getUUID() + ". \nBank:  " + authBank.getName() + "\n\n\n");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("accountDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        // Add btn to the Dialog Pane
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK); // ok to create
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        // process the result
        if (result.isPresent() && result.get() == ButtonType.OK) {
            AccountDialog accountDialog = fxmlLoader.getController();
            Account acc = accountDialog.processNewAcc();
            if (acc == null) {
                onAddAccClick();
            } else {
                currentAccount = acc;
                updateAccountDB();
                showTransactions();
            }
        }
    }

    /*
    * Button 'Change account'
    * set the @currentAccount to the new selected account
    * */
    @FXML
    public void onChangeAccClick()
    {
        if (authBank == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Bank selected");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a bank institution to continue.");
            alert.showAndWait();
            return;
        }

        if (!loggedIn)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No User selected");
            alert.setHeaderText(null);
            alert.setContentText("Please log in to continue.");
            alert.showAndWait();
            return;
        }

        // Get all accounts (uuid) related to this User
        List<String> uuidAccounts = new ArrayList<>();
        for (Account account : currentUser.getAccounts())
        {
            uuidAccounts.add(account.getUUID());
        }

        // Opens a new Choice Dialog (using the @uuidAccounts)
        ChoiceDialog<String> dialog = new ChoiceDialog<>(uuidAccounts.get(0), uuidAccounts);
        dialog.setTitle("ACCOUNT");
        dialog.setHeaderText("Select your account");
        Optional<String> result = dialog.showAndWait();

        // process the result
        String uuid = "notValid";
        if (result.isPresent() && !result.get().equals(uuid))
        {
            uuid = result.get();
            Account acc = getAccount(uuid, currentUser);
            if (acc != null)
            {
                currentAccount = acc;
                showTransactions();
            }
        } else {
            onChangeAccClick();
        }
    }

    /*
    * Button: 'Deposit'
    * Add a new transaction in the @currentAccount Account depending on the selected amount
    * */
    @FXML
    public void onDepositClick() throws IOException {
        if (authBank == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Bank selected");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a bank institution to continue.");
            alert.showAndWait();
            return;
        }

        if (!loggedIn)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No User selected");
            alert.setHeaderText(null);
            alert.setContentText("Please log in to continue.");
            alert.showAndWait();
            return;
        }

        if (currentAccount == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No account selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select your account to continue.");
            alert.showAndWait();
            return;
        }

        /*
        * Opens amountInput Dialog Pane
        * */
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Deposit");
        dialog.setHeaderText("Please confirm you are logged in the right bank account and select your deposit value");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("amountInput.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        for (ButtonType buttonType : dialog.getDialogPane().getButtonTypes())
        {
            Button button = (Button) dialog.getDialogPane().lookupButton(buttonType);
            button.setStyle("-fx-font-size: 24; -fx-padding: 20; -fx-height: 20; -fx-width: 40; -fx-font-weight: bold; -fx-");
        }

        Optional<ButtonType> result = dialog.showAndWait();

        // process result
        if (result.isPresent() && result.get() == ButtonType.OK) {
            AmountInput amountInput = fxmlLoader.getController();
            double amount = amountInput.processResult();
            if (amount == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Zero amount error.");
                alert.setHeaderText(null);
                alert.setContentText("Amount is equal to zero. Please select the amount you want to deposit.");
                alert.showAndWait();
                onDepositClick();
            } else {
                String memo = "Deposit from ATM id 74$3E2_2F1@G2";
                currentAccount.addTransaction(amount, memo);
                showTransactions();
                updateTransactionDB();
            }
        }
    }

    /*
     * Button: 'Withdraw'
     * Add a new transaction in the @currentAccount Account depending on the selected amount
     * */
    @FXML
    public void onWithdrawClick() throws IOException {
        if (authBank == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Bank selected");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a bank institution to continue.");
            alert.showAndWait();
            return;
        }

        if (!loggedIn)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No User selected");
            alert.setHeaderText(null);
            alert.setContentText("Please log in to continue.");
            alert.showAndWait();
            return;
        }

        if (currentAccount == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No account selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select your account to continue.");
            alert.showAndWait();
            return;
        }

        /*
         * Opens amountInput Dialog Pane
         * */
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Deposit");
        dialog.setHeaderText("Please confirm you are logged in the right bank account and select your deposit value");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("amountInput.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        for (ButtonType buttonType : dialog.getDialogPane().getButtonTypes())
        {
            Button button = (Button) dialog.getDialogPane().lookupButton(buttonType);
            button.setStyle("-fx-font-size: 24; -fx-padding: 20; -fx-height: 20; -fx-width: 40; -fx-font-weight: bold; -fx-");
        }

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            double initialBalance = currentAccount.getBalance();
            AmountInput amountInput = fxmlLoader.getController();
            double amount = amountInput.processResult();
            if (amount == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Zero amount error.");
                alert.setHeaderText(null);
                alert.setContentText("Amount is equal to zero. Please select the amount you want to deposit.");
                alert.showAndWait();
                onWithdrawClick();
            } else if (amount > initialBalance) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("OVERDRAFT");
                alert.setHeaderText(null);
                alert.setContentText("The balance amount doesn't cover the withdraw amount. " +
                        "Please select the withdraw amount amount again. Current balance: €" + initialBalance);
                alert.showAndWait();
                onWithdrawClick();
            }
            else {
                String memo = "Withdraw from ATM id 74$3E2_2F1@G2";
                currentAccount.addTransaction(amount * -1, memo);
                showTransactions();
                updateTransactionDB();
            }
        }
    }

    /*
    * Button 'Transfer'
    * Add a new transaction to the @currentAccount if the conditions are filled
    * Add a new transaction to the @receiverAccount if the conditions are filled
    * */
    @FXML
    public void onTransferClick() throws IOException {
        System.out.println("Transfer operation begins...");

        if (authBank == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Bank selected");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a bank institution to continue.");
            alert.showAndWait();
            return;
        }

        if (!loggedIn)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No User selected");
            alert.setHeaderText(null);
            alert.setContentText("Please log in to continue.");
            alert.showAndWait();
            return;
        }

        /*
        * Get User of the destination account
        * */
        User userAddressee = getUserAddressee();

        /*
        * Check if User was successfully selected
        * */
        if (userAddressee == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Destination account holder not identified");
            alert.setHeaderText(null);
            alert.setContentText("Something went wrong. We couldn't identify the destination account holder. " +
                    "\nPlease check if the UserID correct is and try again.");
            alert.showAndWait();
            return;
        }

        /*
        * Get all accounts related to the selected User
        * */
        List<String> uuidAccounts = new ArrayList<>();
        for (Account account : userAddressee.getAccounts())
        {
            uuidAccounts.add(account.getUUID());
        }

        /*
        * Opens a new Choice Dialog Pane
        * */
        ChoiceDialog<String> dialog = new ChoiceDialog<>(uuidAccounts.get(0), uuidAccounts);
        dialog.setTitle("ACCOUNT");
        dialog.setHeaderText("You are logged in with the Account: " + currentAccount.getUUID() +
                "\n\nSelect the destination account");

        Optional<String> result = dialog.showAndWait();

        /*
        * Get destination account from selected User
        * */
        String uuid = "notValid";
        if (result.isPresent() && !result.get().equals(uuid))
        {
            uuid = result.get();
            Account acc = getAccount(uuid, userAddressee);
            /*
            * Perform the both transactions if conditions are filled
            * */
            if (acc != null && currentAccount != null && !acc.getUUID().equals(currentAccount.getUUID()))
            {
                receiverAccount = acc;
                performTransferTransaction();
                showTransactions();
                updateTransactionDB();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid account combination");
                alert.setHeaderText(null);
                alert.setContentText("Please check the logged in account and the destination account.");
                alert.showAndWait();
                onTransferClick();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please check the accounts and try latter again.");
            alert.showAndWait();
            onTransferClick();
        }
    }

    /*
    * Button 'Logout'
    * reset variables and labels
    * */
    @FXML
    public void onLogoutClick()
    {
        loggedIn = false;
        authBank = null;
        currentUser = null;
        currentAccount = null;
        accountTextField.setText("Account number: ");
        bankName.setText("ATM");
        welcomeText.setText("Welcome to the ATM. Please select your bank an log in.");
        balanceLabel.setText("Balance: ");
        transactionHistory.getItems().clear();
    }

    /*
    * Button 'Print'
    * Creates a new txt file in the TransactionHistory folder containing all transactions related to the selected acc.
    * */
    @FXML
    public void print() throws IOException {
        if (authBank == null || currentUser == null || currentAccount == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select an valid account");
            alert.showAndWait();
            return;
        }

        /*
         * Export to txt file
         * */
        String file = "temporaryFile.txt";
        String filePath = new java.io.File(".").getCanonicalPath() + "\\src\\TransactionHistory\\" + file;

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath))) {
            fileWriter.write("Account: " + currentAccount.getAccountName() + " " + currentAccount.getUUID() + " " +
                    " Bank: " + authBank.getName());
            fileWriter.newLine();
            for (String str : transactionHistory.getItems())
            {
                fileWriter.write(str);
                fileWriter.newLine();
            }
            fileWriter.write("Balance: €");
            fileWriter.write(String.valueOf(currentAccount.getBalance()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    /*
    * Set the @bankName Label
    * Set the @welcomeText Label
    * */
    public void welcomeMessage(User user)
    {
        bankName.setText(user.getBankName());
        welcomeText.setText(user.getFirstName() + " " + user.getLastName() + ", welcome to the " + user.getBankName()
                + "\nYour UUID is: " + user.getUUID());
    }

    /*
    * Set the @accountTextField Label
    * Set the @transactionHistory ListView
    * Set the @balanceLabel Label
    * */
    public void showTransactions()
    {
        transactionHistory.getItems().clear();
        accountTextField.setText("Account number: " + currentAccount.getUUID() + " : " + currentAccount.getAccountName()
                + " " + authBank.getName());

        // Title
        String str = "Bank extract as of " + new Date().toString();
        transactionHistory.getItems().addAll(str);

        // List
        for (Transaction transaction : currentAccount.getTransactions())
        {
            String transactionStr = transaction.getSummaryLine();
            transactionHistory.getItems().addAll(transactionStr);
        }

        // balance
        String balance = String.valueOf(currentAccount.getBalance());
        balanceLabel.setText("Balance:  € " + balance);
    }

    /*
    * Get an account from a specific User
    * @return   Account for a given User
    * */
    private Account getAccount(String uuidStr, User user)
    {
        for (Account account : user.getAccounts())
        {
            if (account.getUUID().equals(uuidStr))
            {
                return account;
            }
        }
        return null;
    }

    /*
    * Add a transaction in the @currentAccount
    * Add a transaction in the @receiverAccount
    * */
    private void performTransferTransaction() throws IOException {
        if (currentAccount == null || receiverAccount == null)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Something went wrong. Please try again or search for local help.");
            alert.setHeaderText(null);
            alert.setContentText("Operation interrupted.");
            alert.showAndWait();
            return;
        }

        /*
        * Opens an amount input Dialog Pane
        * */
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Transfer amount");
        dialog.setHeaderText("Please confirm you are logged in the right bank account and select your transfer value" +
                "\nYour current balance is: " + currentAccount.getBalance());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("amountInput.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        for (ButtonType buttonType : dialog.getDialogPane().getButtonTypes())
        {
            Button button = (Button) dialog.getDialogPane().lookupButton(buttonType);
            button.setStyle("-fx-font-size: 24; -fx-padding: 20; -fx-height: 20; -fx-width: 40; -fx-font-weight: bold; -fx-");
        }

        Optional<ButtonType> result = dialog.showAndWait();

        // process the result
        if (result.isPresent() && result.get() == ButtonType.OK) {
            AmountInput amountInput = fxmlLoader.getController();
            double amount = amountInput.processResult();
            if (amount == 0)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Zero amount error.");
                alert.setHeaderText(null);
                alert.setContentText("Amount is equal to zero. Please select the amount you want to deposit.");
                alert.showAndWait();
                onTransferClick();
            } else if (amount > currentAccount.getBalance()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Overdraft operation error.");
                alert.setHeaderText(null);
                alert.setContentText("The amount you want to transfer exceeds the account balance amount." +
                        "\nYour balance is: " + currentAccount.getBalance() + "\nPlease try again.");
                alert.showAndWait();
                onTransferClick();
            } else {
                String memo = "Transfer from account " + currentAccount.getUUID() + ": " + currentUser.getBankName() +
                        " to account " + receiverAccount.getUUID() + ": " + currentUser.getBankName();
                currentAccount.addTransaction(amount * -1, memo);
                receiverAccount.addTransaction(amount, memo);
            }
        }
    }

    /*
    * Get the User for the transfer transaction
    * @return   the User holder of the destination account by transfer
    * */
    private User getUserAddressee()
    {
        /*
        * Opens a Dialog Pane for the UUID input
        * */
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("DESTINATION");
        dialog.setHeaderText("Please select the UserID of the destination account holder");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("destinationUser.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        // process the result
        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            DestinationUserDialog destinationUserDialog = fxmlLoader.getController();
            String userAddresseeID =  destinationUserDialog.processUserID();

            for (User user : users)
            {
                if (user.getUUID().equals(userAddresseeID))
                {
                    return user;
                }
            }
        }
        return null;
    }

    /*
    * Update the BankDB
    * */
    public void updatedBankDB() throws IOException {
        String file = "UserDB.txt";
        String locPath = new java.io.File(".").getCanonicalPath() + "\\src\\DataBase\\" + file;

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(locPath)))
        {
            for (User user : users)
            {
                fileWriter.write(user.getBankDBText());
                fileWriter.newLine();
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Update the AccountDB
     * */
    public void updateAccountDB() throws IOException {
        String accFile = "AccountDB.txt";
        String locPath = new java.io.File(".").getCanonicalPath() + "\\src\\DataBase\\" + accFile;

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(locPath)))
        {
            for (User user : users)
            {
                for (Account acc : user.getAccounts())
                {
                    fileWriter.write(acc.getAccDBText());
                    fileWriter.newLine();
                }
            }
        } catch (IOException  e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Update the TransactionDB
     * */
    public void updateTransactionDB() throws IOException {
        String transactionFile = "TransactionDB.txt";
        String locPath = new java.io.File(".").getCanonicalPath() + "\\src\\DataBase\\" + transactionFile;

        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(locPath)))
        {
            for (User user : users)
            {
                for (Account acc : user.getAccounts())
                {
                    for (Transaction transaction : acc.getTransactions())
                    {
                        fileWriter.write(transaction.getTransactionTextDB());
                        fileWriter.newLine();
                    }
                }
            }
        } catch (IOException  e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
