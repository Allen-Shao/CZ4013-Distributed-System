package bankingsys.server.model;

/**
 * Class that stores bank account information
 */
public class BankAccount {
    private int accountNumber;
    private String name;
    private String password;
    private Currency currencyType;
    private float balance;

    /**
     * Create a new bank account
     * @param accountNumber Account number
     * @param name Account name
     * @param password Account password
     * @param currencyType Account currency type
     * @param balance Account initial balance
     */
    public BankAccount(int accountNumber, String name, String password, Currency currencyType, float balance) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.password = password;
        this.currencyType = currencyType;
        this.balance = balance;
    }

    /**
     * Enum class for representing currencies
     */
    public enum Currency {
        SGD, USD, CNY, JPY, HKD
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Currency getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(Currency currencyType) {
        this.currencyType = currencyType;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void modifyBalance(float difference) {
        this.balance += difference;
    }
}
