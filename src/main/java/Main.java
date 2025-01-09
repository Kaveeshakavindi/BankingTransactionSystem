import java.util.*;

public class Main {
    public static void main(String[] args) {
        //creates bank account (1,2,3) with their initial balances
        BankAccount account1 = new BankAccount(1, 1000);
        BankAccount account2 = new BankAccount(2, 2000);
        BankAccount account3 = new BankAccount(3, 1500);

        //creates TransactionSystem object and initialze it with a list of bank accounts
        TransactionSystem system = new TransactionSystem(Arrays.asList(account1, account2, account3));

        //thread that handle money transferring from fromAccount to toAccount
        Thread t1 = new Thread(() -> system.transfer(1, 2, 100));
        Thread t2 = new Thread(() -> system.transfer(2, 3, 200));
        Thread t3 = new Thread(() -> system.transfer(3, 1, 50));

        //as mentioned in the spec, thread 4 reads the balance of account 1 and account 3 concurrently
        //thread 4 reads balances without blocking ongoing transactions
        Thread t4 = new Thread(() -> {
            System.out.println("Account 1 Balance: $" + account1.getBalance());
            System.out.println("Account 3 Balance: $" + account3.getBalance());
        });

        //t4 read balances without interfering with transactions
        t4.start();

        try {
            //t1.start() followed by t1.join() makes sure:
            //thread 1 completes its transaction before thread 2 can access account 2
            t1.start();
            t1.join();

            //t2.start() followed by t2.join() makes sure:
            //thread 2 completes its transaction before thread 3 can access account 3
            t2.start();
            t2.join();

            //Thread 3 does not start its transaction until both Thread 1 and Thread 2 are done.
            t3.start();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Print final balances
        System.out.println("Final Balances:");
        system.printAccountBalances();
    }
}