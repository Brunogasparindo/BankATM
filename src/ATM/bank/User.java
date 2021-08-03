package ATM.bank;

import ATM.Main;
import javafx.util.converter.ByteStringConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class User {
    /*
     * The first name of the user
     */
    private final String firstName;

    /*
     * The last name of the user
     */
    private final String lastName;

    /*
    * The bank related to this user
    * */
    private final Bank bank;

    /*
     * The ID number of the user
     */
    private final String uuid; // unique user id

    /*
     * The MD5 hash of the user's pin number
     */
    private String pinHash;

    /*
     * The list of accounts for this user
     */
    private final ArrayList<Account> accounts;

    /*
     * Create a new user
     * @param firstName  the user's first name
     * @param lastName   the user's last name
     * @param pin        the user's account pin number
     * @param theBank    the Bank object that the user is a customer of
     * */
    public User(String firstName, String lastName, String pin, Bank theBank)
    {
        // set user's name
        this.firstName = firstName;
        this.lastName = lastName;
        this.bank = theBank;

        // store the pin's MD5 hash, rather than the original value (for security reasons)
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            this.pinHash = Arrays.toString(md.digest(pin.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        // get a new, unique universal ID for the user
        this.uuid = theBank.getNewUserUUID();

        // create empty list of accounts
        this.accounts = new ArrayList<>();

        // print log message
        System.out.printf("New user %s, %s with ID %s created.\n", lastName, firstName, this.uuid);
    }

    /*
     * Create a new user
     * @param firstName  the user's first name
     * @param lastName   the user's last name
     * @param theBank    the Bank object that the user is a customer of
     * @param uuid       the user uuid
     * @param pinHash    the Pin Hash
     * */
    public User(String fName, String lName, String bankName, String uuid, String pinHash) {
        this.firstName = fName;
        this.lastName = lName;
        this.bank = Main.getBank(bankName);
        this.uuid = uuid;

        this.pinHash = pinHash;

        // create empty list of accounts
        this.accounts = new ArrayList<>();
    }

    /*
     * Get first name
     * @return   first name
     * */
    public String getFirstName() {
        return firstName;
    }

    /*
     * Get last name
     * @return   first name
     * */
    public String getLastName() {
        return lastName;
    }

    /*
    * Get than bank name*/
    public String getBankName() {
        return bank.getName();
    }

    /*
     * Add an account fo the user
     * @param anAcct     the account to add
     * */
    public void addAccount(Account anAcct)
    {
        this.accounts.add(anAcct);
    }

    /*
     * Return the user's UUID
     * @return the uuid
     * */
    public String getUUID()
    {
        return uuid;
    }

    /*
    * Return accounts list object
    * @return   accounts
    * */

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    /*
     * Check whether a given pin matches the true User pin
     * @param aPin   the pin to check
     * @return       whether the pin is valid or not*/
    public boolean validatePin(String aPin)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return this.pinHash.equals(Arrays.toString(md.digest(aPin.getBytes())));
        } catch (NoSuchAlgorithmException e) {
            System.err.println("error, caught NoSuchAlgorithmException");
            e.printStackTrace();
            System.exit(1);
        }

        return false;
    }

    /*
     * Print summaries for the accounts of this user
     * */
    public void printAccountsSummary()
    {
        System.out.printf("\n\n%s's accounts summary\n", this.firstName);
        int refNum = 0;
        for (Account account : this.accounts)
        {
            refNum++;
            System.out.printf("  %d) %s\n" ,refNum ,account.getSummaryLine());
        }
        System.out.println();
    }

    /*
     * Get the number of accounts of the user
     * @return       the number of accounts
     * */
    public int numAccounts()
    {
        return this.accounts.size();
    }

    /*
     * Print the account's transaction history
     * @param accIndex        the index of the account to use
     * */
    public void printAcctTransHistory(int accIndex)
    {
        this.accounts.get(accIndex).printTransHistory();
    }

    /*
     * Get account balance
     * @param accountIndex       the account index
     * @return                   account balance for a given account index
     * */
    public double getAccountBalance(int accountIndex)
    {
        return this.accounts.get(accountIndex).getBalance();
    }

    /*
     * Get the UUID of a particular account
     * @param accIndex       the index of the account to use
     * @return               the UUID of the account
     * */
    public String getAccUUID(int accIndex)
    {
        return this.accounts.get(accIndex).getUUID();
    }

    /*
     * Add a transaction to a particular account
     * @param accIndex       the index of the account
     * @param amount         the amount of the transaction
     * @param memo           the memo of the transaction
     * */
    public void addAccountTransaction(int accIndex, double amount, String memo) throws IOException {
        this.accounts.get(accIndex).addTransaction(amount, memo);
    }

    /*
    * Provide text for the DB storage
    * */
    public String getBankDBText() throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return this.firstName + ";" + this.lastName + ";" + this.bank.getName() + ";" + this.uuid +
               ";" + this.pinHash; // String.getBytes()
    }
}
