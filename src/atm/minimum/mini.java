import java.util.*;

/*
this is a minimum version and used state design pattern only,
so kept the cash dispenser as simple.

but we can use chain of responsibility for cash dispenser,
 */

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
    private int totalCash;

    public void addCash(int amount) {
        totalCash += amount;
    }

    public boolean hasSufficientCash(int amount) {
        return totalCash >= amount;
    }

    public void dispenseCash(int amount) {
        totalCash -= amount;
        System.out.println("Dispensed : " + amount);
    }
}

interface ATMState {
    void insertCard(ATM atm, Card card);
    void enterPin(ATM atm, int pin);
    void showBalance(ATM atm);
    void withdrawCash(ATM atm, int amount);
    void ejectCard(ATM atm);
}


class IdleState implements ATMState {
    @Override
    public void insertCard(ATM atm, Card card) {
        atm.setCurrentCard(card);
        atm.setState(new HasCardState());
        System.out.println("Card Inserted");
    }

    @Override
    public void enterPin(ATM atm, int pin) {
        System.out.println("Invalid operation");
    }

    @Override
    public void showBalance(ATM atm) {}

    @Override
    public void withdrawCash(ATM atm, int amount) {}

    @Override
    public void ejectCard(ATM atm) {}
}

class HasCardState implements ATMState {
    @Override
    public void insertCard(ATM atm, Card card) {}

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
    public void showBalance(ATM atm) {}

    @Override
    public void withdrawCash(ATM atm, int amount) {}

    @Override
    public void ejectCard(ATM atm) {
        atm.setCurrentCard(null);
        atm.setState(new IdleState());
        System.out.println("Card Ejected");
    }
}

class AuthenticatedState implements ATMState {
    @Override
    public void insertCard(ATM atm, Card card) {}

    @Override
    public void enterPin(ATM atm, int pin) {
        System.out.println("Invalid operation");

    }

    @Override
    public void showBalance(ATM atm) {
        String accNo = atm.getCurrentCard().getAccountNumber();
        int balance = atm.getBankService().getBalance(accNo);
        System.out.println("Balance : " + balance);
    }

    @Override
    public void withdrawCash(ATM atm, int amount) {
        if (!atm.getCashInventory().hasSufficientCash(amount)) {
            System.out.println("ATM has insufficient cash");
            return;
        }

        String accNo = atm.getCurrentCard().getAccountNumber();
        boolean success = atm.getBankService().withdraw(accNo, amount);
        if (!success) {
            System.out.println("Insufficient balance");
            return;
        }

        atm.getCashInventory().dispenseCash(amount);
    }

    @Override
    public void ejectCard(ATM atm) {
        atm.setCurrentCard(null);
        atm.setState(new IdleState());
        System.out.println("Card Ejected");
    }
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
        currentState = state;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card card) {
        currentCard = card;
    }

    public BankingService getBankService() {
        return bankService;
    }

    public CashInventory getCashInventory() {
        return cashInventory;
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


public class ATMSystem {
    public static void main(String[] args) {
        BankingService bankService = new BankingService();
        BankAccount account = new BankAccount("ACC1", 10000);
        bankService.addAccount(account);

        Card card = new Card("1234", 1234, "ACC1");

        CashInventory inventory = new CashInventory();
        inventory.addCash(10000);

        ATM atm = new ATM(bankService, inventory);
        atm.insertCard(card);
        atm.enterPin(1234);
        atm.showBalance();
        atm.withdrawCash(2000);
        atm.ejectCard();
    }
}