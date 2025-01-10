import java.util.*;

class TransactionSystem {
    //stores BankAccount objects with the account ID as key
    private final Map<Integer, BankAccount> accounts;

    public TransactionSystem(List<BankAccount> accountList) {
        accounts = new HashMap<>();

        //adds each bank account to accounts map
        for (BankAccount account : accountList) {
            accounts.put(account.getId(), account);
        }
    }

    public boolean transfer(int fromAccountId, int toAccountId, double amount) {
        BankAccount fromAccount = accounts.get(fromAccountId);
        BankAccount toAccount = accounts.get(toAccountId);

        //determines which account to lock to avoid deadlock
        BankAccount firstLock = fromAccountId < toAccountId ? fromAccount : toAccount;
        BankAccount secondLock = fromAccountId < toAccountId ? toAccount : fromAccount;
        //locks both accounts in a consistent order
        firstLock.lock();
        secondLock.lock();

        try {
            //insufficient funds in from account handle
            if (fromAccount.getBalance() < amount) {
                System.out.println("Insufficient funds in Account " + fromAccountId);
                return false;
            }
            //withdraw from fromAccount
            fromAccount.withdraw(amount);

            //deposit into deposit account
            toAccount.deposit(amount);

            //success message for better demonstration
            System.out.println("Transferred $" + amount + " from Account " + fromAccountId + " to Account " + toAccountId);
            return true;
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
            reverseTransaction(fromAccountId, toAccountId, amount);
            return false;
        } finally {
            //unlock the accounts in reverse order :
            //given the scenario where thread 1 locks account 1 and then account 2
            //then thread2 lock account 2 and then account 1
            //If both threads hold their first lock and are waiting for the other lock, a deadlock occurs
            //Unlocking in reverse order ensures that any partial locking
            // (e.g., Thread A holds Account 1 and Thread B holds Account 2)
            // is safely released without waiting indefinitely.
            secondLock.unlock();
            firstLock.unlock();
        }
    }

    // safely reverse a transaction if an error occurs during the transfer
    public void reverseTransaction(int fromAccountId, int toAccountId, double amount) {
        transfer(toAccountId, fromAccountId, amount);
    }
    //created this method for better demonstration purposes
    //gets the final account balances
    public void printAccountBalances() {
        for (BankAccount account : accounts.values()) {
            System.out.println("Account " + account.getId() + ": Balance $" + account.getBalance());
        }
    }
}