package ATM.bank;

import java.util.ArrayList;
import java.util.Random;

public class Bank {
    /*
    * Bank name
    * */
    private final String name;

    /*
    * List of Users from this bank
    * */
    private final ArrayList<User> users;

    /*
    * List of Accounts from this bank
    * */
    private final ArrayList<Account> accounts;


    /*
     * Create a new Bank object with empty lists of users and accounts
     * @param name   the name of the bank*/
    public Bank(String name)
    {
        this.name = name;
        this.users = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    /*
     * Get the name
     * @ return      name
     * */
    public String getName()
    {
        return this.name;
    }


    /*
     * Generate a new universally unique ID for a user
     * @return the uuid
     * */
    public String getNewUserUUID()
    {
        // inits
        StringBuilder uuid;
        Random rng = new Random();
        int len = 6;
        boolean nonUnique;

        // continue looping until we get a unique ID
        do
        {
            // generate the number
            uuid = new StringBuilder();
            for (int c = 0; c < len; c++)
            {
                uuid.append(((Integer) rng.nextInt(10)).toString());
            }

            // check to make sure it's unique
            nonUnique = false;
            for (User u : this.users)
            {
                if (uuid.toString().compareTo(u.getUUID()) == 0)
                {
                    nonUnique = true;
                    break;
                }
            }
        } while (nonUnique);

        return uuid.toString();
    }

    /*
     * Generate a new universally unique ID for an account
     * @return the uuid
     * */
    public String getNewAccountUUID()
    {
        // inits
        StringBuilder uuid;
        Random rng = new Random();
        int len = 10;
        boolean nonUnique;

        // continue looping until we get a unique ID
        do
        {
            // generate the number
            uuid = new StringBuilder();
            for (int c = 0; c < len; c++)
            {
                uuid.append(((Integer) rng.nextInt(10)).toString());
            }

            // check to make sure it's unique
            nonUnique = false;
            for (Account a : this.accounts)
            {
                if (uuid.toString().compareTo(a.getUUID()) == 0)
                {
                    nonUnique = true;
                    break;
                }
            }
        } while (nonUnique);

        return uuid.toString();
    }

    /*
     * Add an account
     * @param anAcct        the account to add
     * */
    public void addAccount(Account anAcct)
    {
        this.accounts.add(anAcct);
    }

    /*
     * Create a new user of the bank
     * @param firstName      the user's first name
     * @param lastName       the user's last name
     * @param pin            the user's pin
     * @return               the new User object
     * */
    public User addUser(String firstName, String lastName, String pin)
    {
        // create a new User object and add it to our list
        User newUser = new User(firstName, lastName, pin, this);
        this.users.add(newUser);

        return newUser;
    }

    public void addUserDB(User user)
    {
        this.users.add(user);
    }

    /*
     * User's login
     * @param userID         String of the user ID
     * @param pin            String of the user pin
     * @return               logged-in User object if user ID and pin matches
     * */
    public User userLogin(String userID, String pin)
    {
        // search through list of users
        for (User u : this.users)
        {
            // check if user ID is correct
            if (u.getUUID().compareTo(userID) == 0 && u.validatePin(pin))
            {
                return u;
            }
        }

        // if we haven't found the user or the pin is incorrect
        return null;
    }
}
