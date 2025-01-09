import java.util.concurrent.locks.*;

class BankAccount {
    private final int id;
    private double balance;
    //each bank account its own reentrant lock
    //this ensures that only one thread can access the account at a time
    private final ReentrantLock lock = new ReentrantLock();

    public BankAccount(int id, double initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }
    //allows multiple threads to read simultaneously
    public int getId() {
        return id;//no lock needed for this simple return
    }

    public double getBalance() {
        lock(); //acquire the read lock
        try {
            return balance;
        } finally {
            unlock(); //release the read lock
        }
    }
    //write operation: exclusive access for deposit
    public void deposit(double amount) {
        lock();
        try {
            balance += amount;
        } finally {
            unlock();
        }
    }

    public void withdraw(double amount) throws IllegalArgumentException {
        lock();
        try {
            if (balance < amount) {
                throw new IllegalArgumentException("Insufficient funds");
            }
            balance -= amount;
        } finally {
            unlock();
        }
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}