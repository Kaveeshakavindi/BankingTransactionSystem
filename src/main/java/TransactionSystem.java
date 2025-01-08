import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

class TransactionSystem {
    private final Map<Integer, BankAccount> accounts;
    private static final long LOCK_TIMEOUT_SECONDS = 5;

    public TransactionSystem(List<BankAccount> accountList) {
        this.accounts = new ConcurrentHashMap<>();
        for (BankAccount account : accountList) {
            accounts.put(account.getId(), account);
        }
    }

    private void acquireLocks(BankAccount first, BankAccount second) throws InterruptedException {
        boolean firstLockAcquired = false;
        boolean secondLockAcquired = false;

        try {
            // Try to acquire both write locks with timeout using new methods
            firstLockAcquired = first.tryLockForWriting(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!firstLockAcquired) {
                throw new InterruptedException("Timeout acquiring first lock");
            }

            secondLockAcquired = second.tryLockForWriting(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!secondLockAcquired) {
                throw new InterruptedException("Timeout acquiring second lock");
            }
        } catch (InterruptedException e) {
            if (firstLockAcquired) {
                first.unlockWriting();
            }
            throw e;
        }
    }

    private void releaseLocks(BankAccount first, BankAccount second) {
        second.unlockWriting();
        first.unlockWriting();
    }

    public boolean transfer(int fromAccountId, int toAccountId, double amount) {
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        BankAccount fromAccount = accounts.get(fromAccountId);
        BankAccount toAccount = accounts.get(toAccountId);

        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("Invalid account ID");
        }

        // Always acquire locks in order of account ID to prevent deadlock
        BankAccount firstLock = fromAccount.getId() < toAccount.getId() ? fromAccount : toAccount;
        BankAccount secondLock = fromAccount.getId() < toAccount.getId() ? toAccount : fromAccount;

        try {
            acquireLocks(firstLock, secondLock);

            if (fromAccount.getBalance() < amount) {
                return false;
            }

            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            return true;

        } catch (InterruptedException e) {
            return false;
        } finally {
            releaseLocks(firstLock, secondLock);
        }
    }

    public void reverseTransaction(int fromAccountId, int toAccountId, double amount) {
        transfer(toAccountId, fromAccountId, amount);
    }

    public void printAccountBalances() {
        accounts.values().forEach(account -> {
            account.lockForReading();
            try {
                System.out.printf("Account %d: %.2f%n", account.getId(), account.getBalance());
            } finally {
                account.unlockReading();
            }
        });
    }
}