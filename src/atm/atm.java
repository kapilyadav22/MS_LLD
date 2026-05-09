import java.util.*;

/*
    ATM Machine LLD (Interview Version)

    Concepts Covered:
    - State Design Pattern
    - Cash Withdrawal
    - Balance Inquiry
    - Cash Inventory
    - Clean OOP Design

    Main Flow:
    Idle -> Card Inserted -> Authenticated -> Transaction
*/

public class ATMSystem {
    public static void main(String[] args) {
        BankingService bank = new BankingService();
        BankAccount acc = new BankAccount("ACC123", 10000);
        bank.addAccount(acc);

        Card card = new Card("1234-5678", 1234, "ACC123");

        CashInventory inventory = new CashInventory();
        inventory.addNotes(500, 10);
        inventory.addNotes(200, 20);
        inventory.addNotes(100, 30);

        ATM atm = new ATM(bank, inventory);
        atm.insertCard(card);
        atm.enterPin(1234);
        atm.showBalance();
        atm.withdrawCash(3700);
        atm.ejectCard();
    }
}

enum ATMStatus {
    ACTIVE,
    OUT_OF_SERVICE
}

class Card {
    private final String cardNumber;
    private final int pin;
    private final String accountNumber;

    public Card(String cardNumber, int pin, String accountNumber) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.accountNumber = accountNumber;
    }

    public boolean validatePin(int enteredPin) {
        return pin == enteredPin;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}

class BankAccount {
    private final String accountNumber;
    private int balance;

    public BankAccount(String accountNumber, int balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public synchronized boolean withdraw(int amount) {
        if (balance < amount) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public int getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}

class BankingService {
    private final Map<String, BankAccount> accounts = new HashMap<>();

    public void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public boolean withdraw(String accNo, int amount) {
        return accounts.get(accNo).withdraw(amount);
    }

    public int getBalance(String accNo) {
        return accounts.get(accNo).getBalance();
    }
}

class CashInventory {
    // Higher denomination first
    private final TreeMap<Integer, Integer> notes = new TreeMap<>(Collections.reverseOrder());

    public void addNotes(int denomination, int count) {
        notes.put(denomination, notes.getOrDefault(denomination, 0) + count);
    }

    public boolean hasSufficientCash(int amount) {
        int remaining = amount;
        for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
            int denomination = entry.getKey();
            int availableNotes = entry.getValue();
            int required = remaining / denomination;
            int used = Math.min(required, availableNotes);
            remaining -= used * denomination;
        }
        return remaining == 0;
    }

    public void dispenseCash(int amount) {
        int remaining = amount;
        Map<Integer, Integer> dispensed = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : notes.entrySet()) {
            int denomination = entry.getKey();
            int availableNotes = entry.getValue();
            int required = remaining / denomination;
            int used = Math.min(required, availableNotes);
            if (used > 0) {
                notes.put(denomination, availableNotes - used);
                dispensed.put(denomination, used);
                remaining -= used * denomination;
            }
        }

        System.out.println("Dispensed:");
        for (Map.Entry<Integer, Integer> entry : dispensed.entrySet()) {
            System.out.println(entry.getKey() + " x " + entry.getValue());
        }
    }
}

interface ATMState {
    void insertCard(ATM atm, Card card);
    void enterPin(ATM atm, int pin);
    void showBalance(ATM atm);
    void withdrawCash(ATM atm, int amount);
    void ejectCard(ATM atm);
}

class ATM {
    private ATMState currentState;
    private Card currentCard;
    private final BankingService bankService;
    private final CashInventory cashInventory;

    public ATM(BankingService bankService, CashInventory cashInventory) {
        this.bankService = bankService;
        this.cashInventory = cashInventory;
        currentState = new IdleState();
    }

    public void setState(ATMState state) {
        this.currentState = state;
    }

    public void setCurrentCard(Card card) {
        this.currentCard = card;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public BankingService getBankService() {
        return bankService;
    }

    public CashInventory getCashInventory() {
        return cashInventory;
    }

    // Delegation to states
    public void insertCard(Card card) {
        currentState.insertCard(this, card);
    }

    public void enterPin(int pin) {
        currentState.enterPin(this, pin);
    }

    public void showBalance() {
        currentState.showBalance(this);
    }

    public void withdrawCash(int amount) {
        currentState.withdrawCash(this, amount);
    }

    public void ejectCard() {
        currentState.ejectCard(this);
    }
}

class IdleState implements ATMState {
    @Override
    public void insertCard(ATM atm, Card card) {
        atm.setCurrentCard(card);
        System.out.println("Card Inserted");
        atm.setState(new HasCardState());
    }

    @Override
    public void enterPin(ATM atm, int pin) {
        System.out.println("Insert card first");
    }

    @Override
    public void showBalance(ATM atm) {
        System.out.println("Authenticate first");
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        System.out.println("Authenticate first");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("No card inserted");
    }
}

class HasCardState implements ATMState {
    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("Card already inserted");
    }

    @Override
    public void enterPin(ATM atm, int pin) {
        Card card = atm.getCurrentCard();
        if (card.validatePin(pin)) {
            System.out.println("PIN validated");
            atm.setState(new AuthenticatedState());
        } else {
            System.out.println("Invalid PIN");
        }
    }

    @Override
    public void showBalance(ATM atm) {
        System.out.println("Authenticate first");
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        System.out.println("Authenticate first");
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setCurrentCard(null);
        atm.setState(new IdleState());
        System.out.println("Card Ejected");
    }
}

class AuthenticatedState implements ATMState {
    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("Card already inserted");
    }

    @Override
    public void enterPin(ATM atm, int pin) {
        System.out.println("Already authenticated");
    }

    @Override
    public void showBalance(ATM atm) {
        String accNo = atm.getCurrentCard().getAccountNumber();
        int balance = atm.getBankService().getBalance(accNo);
        System.out.println("Balance: " + balance);
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        String accNo = atm.getCurrentCard().getAccountNumber();
        if (!atm.getCashInventory().hasSufficientCash(amount)) {
            System.out.println("ATM has insufficient cash");
            return;
        }

        boolean success = atm.getBankService().withdraw(accNo, amount);
        if (!success) {
            System.out.println("Insufficient bank balance");
            return;
        }

        atm.getCashInventory().dispenseCash(amount);
        System.out.println("Please collect cash");
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setCurrentCard(null);
        atm.setState(new IdleState());
        System.out.println("Card Ejected");
    }
}
