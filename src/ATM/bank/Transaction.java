package ATM.bank;

import ATM.bank.Account;

import java.util.Date;

public class Transaction {
    /*
     * The amount of this transaction
     */
    private final double amount;

    /*
     * The time and date of this transaction
     */
    private final String timestamp;

    /*
     * A memo for this transaction
     */
    private String memo;

    /*
     * The account in which the transaction was performed
     */
    private final Account inAccount;

    /*
     * Create a new transaction
     * @param amount     the amount transacted
     * @param inAccount  the account the transaction belongs to
     * */
    public Transaction(double amount, Account inAccount)
    {
        this.amount = amount;
        this.inAccount = inAccount;
        this.timestamp = new Date().toString();
        this.memo = "";
    }

    /*
     * Create a new transaction
     * @param amount     the amount transacted
     * @param memo       the memo for the transaction
     * @param inAccount  the account the transaction belongs to
     * */
    public Transaction(double amount, String memo, Account inAccount)
    {
        // call the two-arguments constructor first
        this(amount, inAccount);

        // set the memo
        this.memo = memo;
    }

    /*
    * Create a new transaction
     * @param amount     the amount transacted
     * @param timeStamp  the date of the transaction
     * @param memo       the memo for the transaction
     * @param inAccount  the account the transaction belongs to
     * @param
    * */
    public Transaction(double amount, String date, String memo, Account inAccount)
    {
        this.amount = amount;
        this.timestamp = date;
        this.memo = memo;
        this.inAccount = inAccount;
    }

    /*
     * Get amount of the transaction
     * @return   amount
     * */
    public double getAmount() {
        return amount;
    }

    /*
     * Get a string summarizing the transaction
     * @return   the summary string
     * */
    public String getSummaryLine()
    {
        if (this.amount >= 0)
        {
            return String.format("%s : € %.02f : %s", this.timestamp.toString(), this.amount, this.memo);
        } else {
            return String.format("%s : € (%.02f) : %s", this.timestamp.toString(), -1*this.amount, this.memo);
        }
    }

    /*
     * Provide text for the DB storage
     * */
    public String getTransactionTextDB()
    {
        return this.inAccount.getBankNameAcc() + ";" + inAccount.getHolder().getUUID() + ";" +
                this.inAccount.getUUID() + ";" + this.amount + ";" + this.timestamp + ";" +
                this.memo;
    }
}
