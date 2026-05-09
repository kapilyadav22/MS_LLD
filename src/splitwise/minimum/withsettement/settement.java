package splitwise.minimum.withsettement;

import java.util.*;

// ---------------- USER ----------------
class User {
    String id;
    String name;

    User(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

// ---------------- SPLIT ----------------
class Split {
    User user;
    double amount;

    Split(User user, double amount) {
        this.user = user;
        this.amount = amount;
    }

    Split(User user) {
        this(user, 0);
    }
}

// ---------------- STRATEGY ----------------
interface SplitStrategy {
    void calculateSplit(double amount, List<Split> splits);
}

// ---------------- EQUAL SPLIT ----------------
class EqualSplit implements SplitStrategy {
    @Override
    public void calculateSplit(double amount, List<Split> splits) {
        double share = amount / splits.size();
        for (Split split : splits) {
            split.amount = share;
        }
    }
}

// ---------------- EXACT SPLIT ----------------
class ExactSplit implements SplitStrategy {
    @Override
    public void calculateSplit(double amount, List<Split> splits) {
        double total = 0;
        for (Split split : splits) {
            total += split.amount;
        }

        if (Math.abs(total - amount) > 0.001) {
            throw new RuntimeException("Invalid exact split");
        }
    }
}

// ---------------- PERCENT SPLIT ----------------
class PercentSplit implements SplitStrategy {
    @Override
    public void calculateSplit(double amount, List<Split> splits) {
        double totalPercent = 0;
        for (Split split : splits) {
            totalPercent += split.amount;
        }

        if (Math.abs(totalPercent - 100.0) > 0.001) {
            throw new RuntimeException("Percent should sum to 100");
        }

        for (Split split : splits) {
            split.amount = split.amount * amount / 100;
        }
    }
}

// ---------------- FACTORY ----------------
class SplitStrategyFactory {
    public static SplitStrategy getStrategy(String type) {
        switch (type) {
            case "EQUAL":
                return new EqualSplit();
            case "EXACT":
                return new ExactSplit();
            case "PERCENT":
                return new PercentSplit();
            default:
                throw new RuntimeException("Invalid split type");
        }
    }
}

// ---------------- EXPENSE ----------------
class Expense {
    String id;
    double amount;
    User paidBy;
    List<Split> splits;

    Expense(String id, double amount, User paidBy, List<Split> splits) {
        this.id = id;
        this.amount = amount;
        this.paidBy = paidBy;
        this.splits = splits;
    }
}

// ---------------- EXPENSE MANAGER ----------------
class ExpenseManager {
    // user1 -> user2 -> amount
    private final Map<String, Map<String, Double>> balanceSheet = new HashMap<>();

    public void addExpense(Expense expense, SplitStrategy strategy) {
        strategy.calculateSplit(expense.amount, expense.splits);
        String payer = expense.paidBy.id;

        for (Split split : expense.splits) {
            String user = split.user.id;
            if (user.equals(payer)) {
                continue;
            }

            double amount = split.amount;
            balanceSheet.computeIfAbsent(user, k -> new HashMap<>()).merge(payer, amount, Double::sum);
            balanceSheet.computeIfAbsent(payer, k -> new HashMap<>()).merge(user, -amount, Double::sum);
        }
    }

    // ---------------- SETTLEMENT ----------------
    public void settleBalance(String fromUser, String toUser, double amount) {
        Map<String, Double> balances = balanceSheet.get(fromUser);

        if (balances == null || !balances.containsKey(toUser)) {
            throw new RuntimeException("No balance exists");
        }

        double currentBalance = balances.get(toUser);
        if (currentBalance <= 0) {
            throw new RuntimeException(fromUser + " does not owe " + toUser);
        }

        if (amount > currentBalance) {
            throw new RuntimeException("Settlement exceeds owed amount");
        }

        double updatedBalance = currentBalance - amount;

        // update fromUser -> toUser
        if (updatedBalance == 0) {
            balances.remove(toUser);
        } else {
            balances.put(toUser, updatedBalance);
        }

        // update toUser -> fromUser
        Map<String, Double> reverseBalances = balanceSheet.get(toUser);
        if (updatedBalance == 0) {
            reverseBalances.remove(fromUser);
        } else {
            reverseBalances.put(fromUser, -updatedBalance);
        }
    }

    // ---------------- SHOW ALL ----------------
    public void showAllBalances() {
        boolean empty = true;

        for (String user1 : balanceSheet.keySet()) {
            Map<String, Double> balances = balanceSheet.get(user1);
            for (String user2 : balances.keySet()) {
                double amount = balances.get(user2);
                if (amount > 0) {
                    System.out.printf("%s owes %s : %.2f\n", user1, user2, amount);
                    empty = false;
                }
            }
        }

        if (empty) {
            System.out.println("No balances");
        }
    }

    // ---------------- SHOW USER BALANCE ----------------
    public void showBalance(String user) {
        Map<String, Double> balances = balanceSheet.get(user);

        if (balances == null || balances.isEmpty()) {
            System.out.println("No balances");
            return;
        }

        boolean empty = true;
        for (String otherUser : balances.keySet()) {
            double amount = balances.get(otherUser);
            if (amount > 0) {
                System.out.printf("%s owes %s : %.2f\n", user, otherUser, amount);
                empty = false;
            } else if (amount < 0) {
                System.out.printf("%s owes %s : %.2f\n", otherUser, user, -amount);
                empty = false;
            }
        }

        if (empty) {
            System.out.println("No balances");
        }
    }
}

// ---------------- SERVICE ----------------
class SplitwiseService {
    private final Map<String, User> users = new HashMap<>();
    private final ExpenseManager expenseManager = new ExpenseManager();
    private int expenseCounter = 0;

    public void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public void addExpense(String paidById, double amount, String type, List<Split> splits) {
        User payer = users.get(paidById);

        if (payer == null) {
            throw new RuntimeException("Payer not found");
        }

        Expense expense = new Expense("e" + (++expenseCounter), amount, payer, splits);
        SplitStrategy strategy = SplitStrategyFactory.getStrategy(type);
        expenseManager.addExpense(expense, strategy);
    }

    public void settleBalance(String fromUser, String toUser, double amount) {
        expenseManager.settleBalance(fromUser, toUser, amount);
    }

    public void showAllBalances() {
        expenseManager.showAllBalances();
    }

    public void showBalance(String user) {
        expenseManager.showBalance(user);
    }
}

// ---------------- MAIN ----------------
public class Main {
    public static void main(String[] args) {
        SplitwiseService service = new SplitwiseService();

        // ---------------- USERS ----------------
        service.addUser("u1", "Alice");
        service.addUser("u2", "Bob");
        service.addUser("u3", "Charlie");

        // ---------------- EQUAL SPLIT ----------------
        List<Split> equalSplits = Arrays.asList(
                new Split(service.getUser("u1")),
                new Split(service.getUser("u2")),
                new Split(service.getUser("u3"))
        );
        service.addExpense("u1", 3000, "EQUAL", equalSplits);

        System.out.println("After Expense 1:");
        service.showAllBalances();

        // ---------------- EXACT SPLIT ----------------
        List<Split> exactSplits = Arrays.asList(
                new Split(service.getUser("u2"), 370),
                new Split(service.getUser("u3"), 880)
        );
        service.addExpense("u2", 1250, "EXACT", exactSplits);

        System.out.println("\nAfter Expense 2:");
        service.showAllBalances();

        // ---------------- PERCENT SPLIT ----------------
        List<Split> percentSplits = Arrays.asList(
                new Split(service.getUser("u1"), 40),
                new Split(service.getUser("u2"), 20),
                new Split(service.getUser("u3"), 40)
        );
        service.addExpense("u3", 1200, "PERCENT", percentSplits);

        System.out.println("\nAfter Expense 3:");
        service.showAllBalances();

        // ---------------- SETTLEMENT ----------------
        System.out.println("\nBob settles 500 to Alice");
        service.settleBalance("u2", "u1", 500);
        service.showAllBalances();

        // ---------------- USER BALANCE ----------------
        System.out.println("\nBalance for u2:");
        service.showBalance("u2");
    }
}
