import java.util.*;

class Item {
    private final String name;
    private final int price;

    Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    String getName() {
        return name;
    }

    int getPrice() {
        return price;
    }
}

class ItemShelf {
    Item item;
    int qty;

    ItemShelf(Item item, int qty) {
        this.item = item;
        this.qty = qty;
    }
}

class Inventory {
    private Map<Integer, ItemShelf> items = new HashMap<>();

    void addItem(int slot, String name, int price, int qty) {
        items.put(slot, new ItemShelf(new Item(name, price), qty));
        System.out.println("Item added to slot " + slot);
    }

    boolean containsSlot(int slot) {
        return items.containsKey(slot);
    }

    boolean isAvailable(int slot) {
        return items.get(slot).qty > 0;
    }

    Item getItem(int slot) {
        return items.get(slot).item;
    }

    void reduce(int slot) {
        items.get(slot).qty--;
    }
}

interface State {
    void insertCoin(VendingMachine vm, int amount);
    void selectProduct(VendingMachine vm, int slot);
    void dispense(VendingMachine vm);
}

class VendingMachine {
    private State state;
    private Inventory inventory;
    private int balance = 0;
    private int selectedSlot = -1;

    VendingMachine() {
        inventory = new Inventory();
        state = new IdleState();
    }

    void setState(State state) {
        this.state = state;
    }

    Inventory getInventory() {
        return inventory;
    }

    void addBalance(int amount) {
        balance += amount;
        System.out.println("Balance: " + balance);
    }

    int getBalance() {
        return balance;
    }

    void resetBalance() {
        balance = 0;
    }

    void setSlot(int slot) {
        selectedSlot = slot;
    }

    int getSlot() {
        return selectedSlot;
    }

    void resetSlot() {
        selectedSlot = -1;
    }

    void insertCoin(int amt) {
        state.insertCoin(this, amt);
    }

    void selectProduct(int slot) {
        state.selectProduct(this, slot);
    }

    void dispense() {
        state.dispense(this);
    }
}

class IdleState implements State {
    public void insertCoin(VendingMachine vm, int amt) {
        vm.addBalance(amt);
        vm.setState(new HasMoneyState());
    }
    public void selectProduct(VendingMachine vm, int slot) {
        System.out.println("Insert money first");
    }
    public void dispense(VendingMachine vm) {
        System.out.println("No item selected");
    }
}

class HasMoneyState implements State {
    public void insertCoin(VendingMachine vm, int amt) {
        vm.addBalance(amt);
    }

    public void selectProduct(VendingMachine vm, int slot) {
        Inventory inv = vm.getInventory();
        if (!inv.containsSlot(slot)) {
            System.out.println("Invalid slot");
            return;
        }
        if (!inv.isAvailable(slot)) {
            System.out.println("Item out of stock");
            return;
        }
        Item item = inv.getItem(slot);
        if (vm.getBalance() < item.getPrice()) {
            System.out.println("Insufficient balance");
            return;
        }
        vm.setSlot(slot);
        System.out.println("Item selected: " + item.getName());
        vm.setState(new DispenseState());
        vm.dispense();
    }

    public void dispense(VendingMachine vm) {
        System.out.println("Select item first");
    }
}

class DispenseState implements State {
    public void insertCoin(VendingMachine vm, int amt) {
        System.out.println("Transaction processing");
    }

    public void selectProduct(VendingMachine vm, int slot) {
        System.out.println("Already selected");
    }

    public void dispense(VendingMachine vm) {
        int slot = vm.getSlot();
        Inventory inv = vm.getInventory();
        Item item = inv.getItem(slot);
        inv.reduce(slot);

        System.out.println("Dispensed: " + item.getName());

        int change = vm.getBalance() - item.getPrice();
        if (change > 0) {
            System.out.println("Change returned: " + change);
        }
        vm.resetBalance();
        vm.resetSlot();
        vm.setState(new IdleState());
    }
}

public class Main {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        vm.getInventory().addItem(1, "Coke", 10, 5);
        vm.getInventory().addItem(2, "Chips", 15, 3);
        vm.getInventory().addItem(3, "Water", 20, 2);

        System.out.println("\n---- Transaction 1 ----");
        vm.insertCoin(10);
        vm.selectProduct(1);


        System.out.println("\n---- Transaction 2 ----");
        vm.insertCoin(5);
        vm.insertCoin(10);
        vm.selectProduct(2);
    }
}
