package atm.minimum.chainofresponsibility;

import java.util.*;

// ---------------- CARD ----------------
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

// ---------------- BANK ACCOUNT ----------------
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

// ---------------- BANKING SERVICE ----------------
class BankingService {
    private final Map<String, BankAccount> accounts = new HashMap<>();

    public void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public boolean withdraw(String accountNumber, int amount) {
        return accounts.get(accountNumber).withdraw(amount);
    }

    public int getBalance(String accountNumber) {
        return accounts.get(accountNumber).getBalance();
    }
}

// ---------------- CASH INVENTORY ----------------
class CashInventory {
    private int totalCash;

    public void addCash(int amount) {
        totalCash += amount;
    }

    public boolean hasSufficientCash(int amount) {
        return totalCash >= amount;
    }

    public void deductCash(int amount) {
        totalCash -= amount;
    }
}

// ---------------- CHAIN OF RESPONSIBILITY ----------------
interface CashDispenser {
    void setNextDispenser(CashDispenser dispenser);

    void dispense(int amount);
}

// ---------------- 2000 DISPENSER ----------------
class TwoThousandDispenser implements CashDispenser {
    private CashDispenser nextDispenser;

    @Override
    public void setNextDispenser(CashDispenser dispenser) {
        nextDispenser = dispenser;
    }

    @Override
    public void dispense(int amount) {
        int notes = amount / 2000;
        int remaining = amount % 2000;

        if (notes > 0) {
            System.out.println("Dispensing " + notes + " notes of 2000");
        }

        if (remaining > 0 && nextDispenser != null) {
            nextDispenser.dispense(remaining);
        }
    }
}

// ---------------- 500 DISPENSER ----------------
class FiveHundredDispenser implements CashDispenser {
    private CashDispenser nextDispenser;

    @Override
    public void setNextDispenser(CashDispenser dispenser) {
        nextDispenser = dispenser;
    }

    @Override
    public void dispense(int amount) {
        int notes = amount / 500;
        int remaining = amount % 500;

        if (notes > 0) {
            System.out.println("Dispensing " + notes + " notes of 500");
        }

        if (remaining > 0 && nextDispenser != null) {
            nextDispenser.dispense(remaining);
        }
    }
}

// ---------------- 100 DISPENSER ----------------
class OneHundredDispenser implements CashDispenser {
    private CashDispenser nextDispenser;

    @Override
    public void setNextDispenser(CashDispenser dispenser) {
        nextDispenser = dispenser;
    }

    @Override
    public void dispense(int amount) {
        int notes = amount / 100;
        int remaining = amount % 100;

        if (notes > 0) {
            System.out.println("Dispensing " + notes + " notes of 100");
        }

        if (remaining > 0) {
            System.out.println("Cannot dispense remaining amount : " + remaining);
        }
    }
}

// ---------------- ATM STATE ----------------
interface ATMState {
    void insertCard(ATM atm, Card card);

    void enterPin(ATM atm, int pin);

    void showBalance(ATM atm);

    void withdrawCash(ATM atm, int amount);

    void ejectCard(ATM atm);
}

// ---------------- IDLE STATE ----------------
class IdleState implements ATMState {
    @Override
    public void insertCard(ATM atm, Card card) {
        atm.setCurrentCard(card);
        atm.setState(new HasCardState());
        System.out.println("Card Inserted");
    }

    @Override
    public void enterPin(ATM atm, int pin) {
        System.out.println("Insert card first");
    }

    @Override
    public void showBalance(ATM atm) {
        System.out.println("Insert card first");
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        System.out.println("Insert card first");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("No card present");
    }
}

// ---------------- HAS CARD STATE ----------------
class HasCardState implements ATMState {
    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("Card already inserted");
    }

    @Override
    public void enterPin(ATM atm, int pin) {
        if (atm.getCurrentCard().validatePin(pin)) {
            atm.setState(new AuthenticatedState());
            System.out.println("PIN Validated");
        } else {
            System.out.println("Invalid PIN");
        }
    }

    @Override
    public void showBalance(ATM atm) {
        System.out.println("Please authenticate first");
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        System.out.println("Please authenticate first");
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setCurrentCard(null);
        atm.setState(new IdleState());
        System.out.println("Card Ejected");
    }
}

// ---------------- AUTHENTICATED STATE ----------------
class AuthenticatedState implements ATMState {
    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("Transaction already in progress");
    }

    @Override
    public void enterPin(ATM atm, int pin) {
        System.out.println("Already authenticated");
    }

    @Override
    public void showBalance(ATM atm) {
        String accountNumber = atm.getCurrentCard().getAccountNumber();
        int balance = atm.getBankingService().getBalance(accountNumber);
        System.out.println("Balance : " + balance);
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        if (!atm.getCashInventory().hasSufficientCash(amount)) {
            System.out.println("ATM has insufficient cash");
            return;
        }

        String accountNumber = atm.getCurrentCard().getAccountNumber();
        boolean success = atm.getBankingService().withdraw(accountNumber, amount);

        if (!success) {
            System.out.println("Insufficient balance");
            return;
        }

        atm.getCashInventory().deductCash(amount);

        atm.getCashDispenser().dispense(amount);

        System.out.println("Transaction Successful");
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setCurrentCard(null);
        atm.setState(new IdleState());
        System.out.println("Card Ejected");
    }
}

// ---------------- ATM ----------------
class ATM {
    private ATMState currentState;
    private Card currentCard;
    private final BankingService bankingService;
    private final CashInventory cashInventory;
    private final CashDispenser cashDispenser;

    public ATM(BankingService bankingService, CashInventory cashInventory) {
        this.bankingService = bankingService;
        this.cashInventory = cashInventory;
        currentState = new IdleState();
        CashDispenser d2000 = new TwoThousandDispenser();
        CashDispenser d500 = new FiveHundredDispenser();
        CashDispenser d100 = new OneHundredDispenser();

        d2000.setNextDispenser(d500);
        d500.setNextDispenser(d100);
        cashDispenser = d2000;
    }

    public void setState(ATMState state) {
        currentState = state;
    }

    public void setCurrentCard(Card card) {
        currentCard = card;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public BankingService getBankingService() {
        return bankingService;
    }

    public CashInventory getCashInventory() {
        return cashInventory;
    }

    public CashDispenser getCashDispenser() {
        return cashDispenser;
    }

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

// ---------------- MAIN ----------------
public class ATMSystem {
    public static void main(String[] args) {
        BankingService bankingService = new BankingService();
        BankAccount account = new BankAccount("ACC1", 10000);

        bankingService.addAccount(account);

        Card card = new Card("1234", 1234, "ACC1");
        CashInventory inventory = new CashInventory();

        inventory.addCash(20000);

        ATM atm = new ATM(bankingService, inventory);

        atm.insertCard(card);
        atm.enterPin(1234);
        atm.showBalance();
        atm.withdrawCash(3700);
        atm.ejectCard();
    }
}
