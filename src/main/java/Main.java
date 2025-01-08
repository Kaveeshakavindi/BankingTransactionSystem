import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<BankAccount> accounts = List.of(
                new BankAccount(1, 1000),
                new BankAccount(2, 1000),
                new BankAccount(3, 1000)
        );
        TransactionSystem system = new TransactionSystem(accounts);
        Thread thread1 = new Thread(() -> {
            system.transfer(1, 2, 100);
        });
        Thread thread2 = new Thread(() -> {
            system.transfer(2, 3, 200);
        });
        Thread thread3 = new Thread(() -> {
            system.transfer(3, 1, 50);
        });
        Thread thread4 = new Thread(() -> {
            BankAccount acc1 = accounts.get(0);
            BankAccount acc3 = accounts.get(2);
            acc1.lockForReading();
            acc3.lockForReading();
            try {
                System.out.println("Account 1 balance: " + acc1.getBalance());
                System.out.println("Account 3 balance: " + acc3.getBalance());
            } finally {
                acc1.unlockReading();
                acc3.unlockReading();
            }
        });
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        system.printAccountBalances();
    }
}