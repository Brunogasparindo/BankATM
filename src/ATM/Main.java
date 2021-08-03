package ATM;

import ATM.bank.Account;
import ATM.bank.Bank;
import ATM.bank.Transaction;
import ATM.bank.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main extends Application {

    /*
    * Store a list of banks available in the ATM terminal
    * */
    public static ArrayList<Bank> banks = new ArrayList<>();

    /*
    * Select a Bank for the ATM
    * @return   the selected Bank object
    * */
    public static Bank getBank(String name)
    {
        for (Bank bank : banks)
        {
            if (bank.getName().equals(name))
            {
                return bank;
            }
        }
        return null;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("AtmInterface.fxml"));
        primaryStage.setTitle("ATM");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    // MAIN \\
    public static void main(String[] args) throws IOException {
        loadDB();
        launch(args);
    }

    /*
    * Load DB for this ATM
    * */
    public static void loadDB() throws IOException
    {
        String banksListFile = "BanksDB.txt";
        String userDBFile = "UserDB.txt";

        /*
        * Load Banks
        * */
        String locPath = new java.io.File(".").getCanonicalPath() + "\\src\\DataBase\\" + banksListFile;
        try (Scanner scanner = new Scanner(Files.newBufferedReader(Path.of(locPath)))) {
            scanner.useDelimiter(";");
            while (scanner.hasNextLine()) {
                String bankName = scanner.nextLine();
                Bank bank = new Bank(bankName.substring(0, bankName.length()-1));
                banks.add(bank);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        /*
        * Load Users
        * */
        locPath = new java.io.File(".").getCanonicalPath() + "\\src\\DataBase\\" + userDBFile;
        try (Scanner scanner = new Scanner(Files.newBufferedReader(Path.of(locPath)))) {
            while (scanner.hasNextLine()) {
                List<String> userInfo = Arrays.asList(scanner.nextLine().split(";"));
                User user = new User(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4));
                Bank bank = getBank(user.getBankName());
                if (bank != null)
                {
                    bank.addUserDB(user);
                }
                Controller.users.add(user);
                loadAcc(user);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /*
    * Load Accounts DB for a User
    * */
    public static void loadAcc(User user) throws IOException
    {
        String loadAccFile = "AccountDB.txt";
        String locPath = new java.io.File(".").getCanonicalPath() + "\\src\\DataBase\\" + loadAccFile;
        /*
         * Load Users
         * */
        try (Scanner scanner = new Scanner(Files.newBufferedReader(Path.of(locPath)))) {
            while (scanner.hasNextLine()) {
                List<String> accInfo = Arrays.asList(scanner.nextLine().split(";"));
                if (user.getUUID().equals(accInfo.get(3)) && user.getBankName().equals(accInfo.get(2)))
                {
                    Account acc = new Account(accInfo.get(0), accInfo.get(1), accInfo.get(2), user);
                    user.addAccount(acc);
                    loadTransactions(acc);
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    /*
     * Load Transactions DB for an Account
     * */
    public static void loadTransactions(Account acc) throws IOException
    {
        String loadAccFile = "TransactionDB.txt";
        String locPath = new java.io.File(".").getCanonicalPath() + "\\src\\DataBase\\" + loadAccFile;

        /*
         * Load Transactions
         * */
        try (Scanner scanner = new Scanner(Files.newBufferedReader(Path.of(locPath)))) {
            while (scanner.hasNextLine()) {
                List<String> transactionInfo = Arrays.asList(scanner.nextLine().split(";"));
                if (acc.getBankNameAcc().equals(transactionInfo.get(0)) &&
                    acc.getHolder().getUUID().equals(transactionInfo.get(1)) &&
                    acc.getUUID().equals(transactionInfo.get(2)))
                {
                    Transaction transaction = new Transaction(Double.parseDouble(transactionInfo.get(3)),
                                                transactionInfo.get(4), transactionInfo.get(5), acc);
                    acc.addTransaction(transaction.getAmount(), transactionInfo.get(5));
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
