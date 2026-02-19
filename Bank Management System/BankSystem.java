interface BankOperations {
    void deposit(double amount);
    void withdraw(double amount) throws Exception;
    void checkBalance();
}

class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

class BankAccount implements BankOperations {

    private String name;
    private double balance;

    public BankAccount(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new InsufficientBalanceException("Invalid deposit amount");
        }
        balance = balance + amount;
        System.out.println("Deposit Successful");
    }

    public void withdraw(double amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Invalid withdrawal amount");
        }
        if (amount > balance) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }
        balance = balance - amount;
        System.out.println("Withdrawal Successful");
    }

    public void checkBalance() {
        System.out.println("Name: " + name);
        System.out.println("Balance: " + balance);
    }
}

public class BankSystem {
    public static void main(String[] args) {

        BankAccount acc = new BankAccount("Gagan", 5000);

        try {
            acc.deposit(1000);
            acc.withdraw(2000);
            acc.withdraw(-100);
            acc.withdraw(10000);
        } 
        catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        } 
        finally {
            acc.checkBalance();
        }
    }
}
