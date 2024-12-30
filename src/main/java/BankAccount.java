import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private double balance;
    private final int id;
    private final ReentrantLock lock = new ReentrantLock(true);

    public BankAccount(int id, double initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId() {
        return id;
    }
    //read A & read B & don't block deposit & don't block withdraw
    public double getBalance() {
        return balance;
    }
    //A -> B & C -> D : can run simultaneously
    //A -> B & B -> C : B waits until A completes
    public void deposit (double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }

    public void lock(){
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
