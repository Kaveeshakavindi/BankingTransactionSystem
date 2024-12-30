import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionSystem {
    public Map<Integer, BankAccount> accounts;

    public TransactionSystem(List<BankAccount> accountList) {
        this.accounts = new HashMap<>();
        for(BankAccount account : accountList) {
            accounts.put(account.getId(), account);
        }
    }

    public boolean transfer(int fromAccountId, int toAccountId, double amount){
        BankAccount fromAccount = this.accounts.get(fromAccountId);
        BankAccount toAccount = this.accounts.get(toAccountId);
        return true;
    }

    public void reverseTransaction(int fromAccountId, int toAccountId, double amount){}

    public void printAccountBalances(){}
}
