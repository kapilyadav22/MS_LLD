import java.util.*;

class Item {
    String name;
    int price;

    Item(String name, int price) {
        this.name = name;
        this.price = price;
    }
}

class Inventory {

    private Map<Integer, ItemShelf> items = new HashMap<>();

    static class ItemShelf {
        Item item;
        int qty;

        ItemShelf(Item item, int qty) {
            this.item = item;
            this.qty = qty;
        }
    }

    void addItem(int slot, String name, int price, int qty) {
        items.put(slot, new Pair(new Item(name, price), qty));
        System.out.println("Item added to slot " + slot);
    }

    boolean hasItem(int slot) {
        return items.containsKey(slot) && items.get(slot).qty > 0;
    }

    Item getItem(int slot) {
        return items.get(slot).item;
    }

    void reduce(int slot) {
        items.get(slot).qty--;
    }

    void show() {
        for (int slot : items.keySet()) {
            ItemShelf shelf = items.get(slot);
            System.out.println(
                    "Slot " + slot +
                            " " + shelf.item.getName() +
                            " Price:" + shelf.item.getPrice() +
                            " Qty:" + shelf.qty
            );
        }
    }
}

/* ================= STATE ================= */

interface State {
    void insert(VendingMachine vm, int amount);
    void select(VendingMachine vm, int slot);
    void dispense(VendingMachine vm);
    void cancel(VendingMachine vm);
}

/* ================= MACHINE ================= */

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

    void insert(int amt) {
        state.insert(this, amt);
    }

    void select(int slot) {
        state.select(this, slot);
    }

    void dispense() {
        state.dispense(this);
    }

    void cancel() {
        state.cancel(this);
    }
}

/* ================= STATES ================= */

class IdleState implements State {

    public void insert(VendingMachine vm, int amt) {
        vm.addBalance(amt);
        vm.setState(new HasMoneyState());
    }

    public void select(VendingMachine vm, int slot) {
        System.out.println("Insert money first");
    }

    public void dispense(VendingMachine vm) {
        System.out.println("No item selected");
    }

    public void cancel(VendingMachine vm) {
        System.out.println("Nothing to cancel");
    }
}

class HasMoneyState implements State {

    public void insert(VendingMachine vm, int amt) {
        vm.addBalance(amt);
    }

    public void select(VendingMachine vm, int slot) {

        Inventory inv = vm.getInventory();

        if (!inv.hasItem(slot)) {
            System.out.println("Item unavailable");
            return;
        }

        Item item = inv.getItem(slot);

        if (vm.getBalance() < item.price) {
            System.out.println("Insufficient balance");
            return;
        }

        vm.setSlot(slot);
        System.out.println("Item selected: " + item.name);

        vm.setState(new DispenseState());
    }

    public void dispense(VendingMachine vm) {
        System.out.println("Select item first");
    }

    public void cancel(VendingMachine vm) {
        System.out.println("Refund: " + vm.getBalance());
        vm.resetBalance();
        vm.setState(new IdleState());
    }
}

class DispenseState implements State {

    public void insert(VendingMachine vm, int amt) {
        System.out.println("Transaction processing");
    }

    public void select(VendingMachine vm, int slot) {
        System.out.println("Already selected");
    }

    public void dispense(VendingMachine vm) {

        int slot = vm.getSlot();
        Inventory inv = vm.getInventory();

        Item item = inv.getItem(slot);

        inv.reduce(slot);

        System.out.println("Dispensed: " + item.name);

        int change = vm.getBalance() - item.price;

        if (change > 0)
            System.out.println("Change returned: " + change);

        vm.resetBalance();
        vm.setState(new IdleState());
    }

    public void cancel(VendingMachine vm) {
        System.out.println("Cannot cancel now");
    }
}


/* ================= DRIVER ================= */

public class Main {

    public static void main(String[] args) {

        VendingMachine vm = new VendingMachine();

        // Add items
        vm.getInventory().addItem(1, "Coke", 10, 5);
        vm.getInventory().addItem(2, "Chips", 15, 3);

        // Show inventory
        vm.getInventory().show();

        System.out.println("---- Transaction 1 ----");

        vm.insert(10);
        vm.select(1);
        vm.dispense();

        System.out.println("---- Transaction 2 ----");

        vm.insert(5);
        vm.insert(10);
        vm.select(2);
        vm.dispense();

        System.out.println("---- Cancel Case ----");

        vm.insert(20);
        vm.cancel();

        System.out.println("---- Edge Case (Insufficient Balance) ----");

        vm.insert(5);
        vm.select(2);  // price 15
    }
}

/*

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        VendingMachine vm = new VendingMachine();

        while (sc.hasNextLine()) {

            String line = sc.nextLine();

            if (line.equals("EXIT"))
                break;

            String[] parts = line.split(" ");
            String cmd = parts[0];

            if (cmd.equals("ADD_ITEM")) {

                int slot = Integer.parseInt(parts[1]);
                String name = parts[2];
                int price = Integer.parseInt(parts[3]);
                int qty = Integer.parseInt(parts[4]);

                vm.getInventory().addItem(slot, name, price, qty);
            }

            else if (cmd.equals("INSERT")) {
                int amt = Integer.parseInt(parts[1]);
                vm.insert(amt);
            }

            else if (cmd.equals("SELECT")) {
                int slot = Integer.parseInt(parts[1]);
                vm.select(slot);
            }

            else if (cmd.equals("DISPENSE")) {
                vm.dispense();
            }

            else if (cmd.equals("CANCEL")) {
                vm.cancel();
            }

            else if (cmd.equals("SHOW")) {
                vm.getInventory().show();
            }

            else {
                System.out.println("Invalid command");
            }
        }

        sc.close();
    }
}

 */