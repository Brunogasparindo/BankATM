package ATM.bank;

import ATM.Controller;

import java.io.IOException;
import java.util.ArrayList;

public class Account {
    /*
     * The name of the account
     */
    private final String accountName;

    /*
     * The account ID number
     */
    private final String uuid;

    /*
     * The User object that owns this account
     */
    private final User holder;

    /*
    * The bank name
    * */
    private final String bankName;

    /*
     * The list of transactions for this account
     */
    private final ArrayList<Transaction> transactions;

    /*
     * Create a new Account (through the ATM)
     * @param name       the name of the account
     * @param holder     the User object that holds this account
     * @param theBank    the Bank that issues the account
     * */
    public Account(String name, User holder, Bank theBank)
    {
        // set the account name and holder
        this.accountName = name;
        this.holder = holder;
        this.bankName = theBank.getName();

        // get new account UUID
        this.uuid = theBank.getNewAccountUUID();

        // init transactions
        this.transactions = new ArrayList<>();
    }

    /*
     * Create a new Account (through the DB)
     * @param name       the name of the account
     * @param holder     the User object that holds this account
     * @param theBank    the Bank that issues the account
     * */
    public Account(String name, String uuid, String bankName, User holder)
    {
        // set the account name and holder
        this.accountName = name;
        this.uuid = uuid;
        this.bankName = bankName;
        this.holder = holder;


        // init transactions
        this.transactions = new ArrayList<>();
    }

    /*
     * Get the bank name
     * @return the accountName
     * */

    public String getBankNameAcc() {
        return bankName;
    }

    /*
     * Get the account name
     * @return the accountName
     * */
    public String getAccountName() {
        return accountName;
    }

    /*
     * Get the account ID
     * @return the uuid
     * */
    public String getUUID() {
        return uuid;
    }

    /*
    * Get holder
    * @return   Account Holder
    * */

    public User getHolder() {
        return holder;
    }

    /*
     * Get summary line for the account
     * @return   the string summary
     * */
    public String getSummaryLine()
    {
        // get the account's balance
        double balance = this.getBalance();

        // format the summary line depending on whether the balance is negative
        if (balance >= 0)
        {
            return String.format("%s : €%.02f : %s", this.uuid, balance, this.accountName);
        } else {
            return String.format("%s : €(%.02f) : %s", this.uuid, balance, this.accountName);
        }
    }

    /*
     * Get the balance of this account by adding the amounts of the transactions
     * @return       the balance value
     * */
    public double getBalance()
    {
        double balance = 0;
        for (Transaction transaction : this.transactions)
        {
            balance += transaction.getAmount();
        }
        return balance;
    }

    /*
     * Print the transaction history of the account
     * */
    public void printTransHistory()
    {
        System.out.printf("\nTransaction history for account %s\n", this.uuid);
        for (int i = this.transactions.size()-1; i >= 0; i--)
        {
            System.out.println(this.transactions.get(i).getSummaryLine());
        }
        System.out.println();
    }

    /*
     * Add a new transaction to this account
     * @param amount     the amount transacted
     * @param memo       the transaction memo
     * */
    public void addTransaction(double amount, String memo) throws IOException {
        // create a new transaction and add it to our list
        Transaction transaction = new Transaction(amount, memo, this);
        this.transactions.add(transaction);
    }

    /*
    * Get the transactions of the Account
    * @return   transactions Array List*/
    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /*
    * Provide text for the DB storage
    * @return   String that will be stored in the txt DB file
    * */
    public String getAccDBText()
    {
        return this.accountName + ";" + this.uuid + ";" + this.bankName + ";" + holder.getUUID();
    }
}
