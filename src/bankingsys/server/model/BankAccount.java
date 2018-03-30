package bankingsys.server.model;

/**
 * Created by koallen on 29/3/18.
 */
public class BankAccount {
    private int accountNumber;
    private String name;
    private String password;

    private float balance;

    public BankAccount(int accountNumer, String name, String password, float balance) {
        this.accountNumber = accountNumer;
        this.name = name;
        this.password = password;
        this.balance = balance;
    }

    public enum Currency {
        SGD, USD, CNY, JPY, HKD
    }

    public int getAccountNumer() {
        return accountNumber;
    }

    public void setAccountNumer(int accountNumber) {
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
