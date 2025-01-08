import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class BankAccount {
    private final int id;
    private double balance;
    private final ReentrantReadWriteLock lock;

    public BankAccount(int id, double initialBalance) {
        this.id = id;
        this.balance = initialBalance;
        this.lock = new ReentrantReadWriteLock(true);
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        lock.readLock().lock();
        try {
            return balance;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void deposit(double amount) {
        lock.writeLock().lock();
        try {
            if (amount < 0) {
                throw new IllegalArgumentException("Cannot deposit negative amount");
            }
            balance += amount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void withdraw(double amount) {
        lock.writeLock().lock();
        try {
            if (amount < 0) {
                throw new IllegalArgumentException("Cannot withdraw negative amount");
            }
            if (balance < amount) {
                throw new IllegalStateException("Insufficient funds");
            }
            balance -= amount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected ReentrantReadWriteLock getLock() {
        return lock;
    }

    public void lockForReading() {
        lock.readLock().lock();
    }

    public void unlockReading() {
        lock.readLock().unlock();
    }

    public void lockForWriting() {
        lock.writeLock().lock();
    }

    public void unlockWriting() {
        lock.writeLock().unlock();
    }

    public boolean tryLockForWriting(long timeout, TimeUnit unit) throws InterruptedException {
        return lock.writeLock().tryLock(timeout, unit);
    }
}

