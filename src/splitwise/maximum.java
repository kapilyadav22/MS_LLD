import java.util.*;

enum SplitType {
    EQUAL,
    EXACT,
    PERCENT
}

class User {
    private final String id;
    private final String name;

    User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }
}

class Split {
    private final User user;
    private double amount;

    Split(User user) {
        this.user = user;
    }

    User getUser() {
        return user;
    }

    double getAmount() {
        return amount;
    }

    void setAmount(double amount) {
        this.amount = amount;
    }
}

interface SplitStrategy {
    void calculateSplit(double amount, List<Split> splits);
}

class EqualSplit implements SplitStrategy {
    @Override
    public void calculateSplit(double amount, List<Split> splits) {
        double share = amount / splits.size();
        for (Split split : splits) {
            split.setAmount(share);
        }
    }
}

class ExactSplit implements SplitStrategy {
    @Override
    public void calculateSplit(double amount, List<Split> splits) {
        double total = 0;
        for (Split split : splits) {
            total += split.getAmount();
        }

        if (Math.abs(total - amount) > 0.001) {
            throw new RuntimeException("Invalid EXACT split");
        }
    }
}

class PercentSplit implements SplitStrategy {
    @Override
    public void calculateSplit(double amount, List<Split> splits) {
        double totalPercent = 0;
        for (Split split : splits) {
            totalPercent += split.getAmount();
        }

        if (Math.abs(totalPercent - 100.0) > 0.001) {
            throw new RuntimeException("Percent should sum to 100");
        }

        for (Split split : splits) {
            double userAmount = (split.getAmount() * amount) / 100.0;
            split.setAmount(userAmount);
        }
    }
}

class SplitStrategyFactory {
    static SplitStrategy getStrategy(SplitType type) {
        switch (type) {
            case EQUAL:
                return new EqualSplit();
            case EXACT:
                return new ExactSplit();
            case PERCENT:
                return new PercentSplit();
            default:
                throw new RuntimeException("Invalid split type");
        }
    }
}

class Expense {
    private final String id;
    private final double amount;
    private final User paidBy;
    private final List<Split> splits;

    Expense(String id, double amount, User paidBy, List<Split> splits) {
        this.id = id;
        this.amount = amount;
        this.paidBy = paidBy;
        this.splits = splits;
    }

    String getId() {
        return id;
    }

    double getAmount() {
        return amount;
    }

    User getPaidBy() {
        return paidBy;
    }

    List<Split> getSplits() {
        return splits;
    }
}

class ExpenseManager {
    // user1 -> user2 -> amount
    private final Map<String, Map<String, Double>> balanceSheet = new HashMap<>();

    public void addExpense(Expense expense, SplitStrategy strategy) {
        strategy.calculateSplit(expense.getAmount(), expense.getSplits());
        String payer = expense.getPaidBy().getId();

        for (Split split : expense.getSplits()) {
            String user = split.getUser().getId();
            if (user.equals(payer)) {
                continue;
            }

            double amount = split.getAmount();

            balanceSheet.computeIfAbsent(user, k -> new HashMap<>())
                    .merge(payer, amount, Double::sum);

            balanceSheet.computeIfAbsent(payer, k -> new HashMap<>())
                    .merge(user, -amount, Double::sum);
        }
    }

    public void showAllBalances() {
        boolean empty = true;

        for (String user1 : balanceSheet.keySet()) {
            for (String user2 : balanceSheet.get(user1).keySet()) {
                double amount = balanceSheet.get(user1).get(user2);
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

    public void showBalance(String user) {
        boolean empty = true;

        if (balanceSheet.containsKey(user)) {
            for (Map.Entry<String, Double> entry : balanceSheet.get(user).entrySet()) {
                if (entry.getValue() > 0) {
                    System.out.printf("%s owes %s : %.2f\n", user, entry.getKey(), entry.getValue());
                    empty = false;
                }
            }
        }

        for (String otherUser : balanceSheet.keySet()) {
            if (balanceSheet.get(otherUser).containsKey(user)) {
                double amount = balanceSheet.get(otherUser).get(user);
                if (amount > 0) {
                    System.out.printf("%s owes %s : %.2f\n", otherUser, user, amount);
                    empty = false;
                }
            }
        }

        if (empty) {
            System.out.println("No balances");
        }
    }
}

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

    public void addExpense(
            String paidById,
            double amount,
            List<String> userIds,
            SplitType type,
            List<Double> values
    ) {
        User payer = getUser(paidById);
        if (payer == null) {
            throw new RuntimeException("Invalid payer");
        }

        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i++) {
            User user = getUser(userIds.get(i));
            if (user == null) {
                continue;
            }

            Split split = new Split(user);
            if (type == SplitType.EXACT || type == SplitType.PERCENT) {
                split.setAmount(values.get(i));
            }
            splits.add(split);
        }

        Expense expense = new Expense("e" + (++expenseCounter), amount, payer, splits);
        SplitStrategy strategy = SplitStrategyFactory.getStrategy(type);
        expenseManager.addExpense(expense, strategy);
    }

    public void showAllBalances() {
        expenseManager.showAllBalances();
    }

    public void showBalance(String user) {
        expenseManager.showBalance(user);
    }
}

public class Main {
    public static void main(String[] args) {
        SplitwiseService service = new SplitwiseService();

        // Add Users
        service.addUser("u1", "Alice");
        service.addUser("u2", "Bob");
        service.addUser("u3", "Charlie");

        // ---------------------------
        // Expense 1 : Equal Split
        // ---------------------------
        service.addExpense(
                "u1",
                3000,
                Arrays.asList("u1", "u2", "u3"),
                SplitType.EQUAL,
                new ArrayList<>()
        );

        System.out.println("After Expense 1:");
        service.showAllBalances();

        // ---------------------------
        // Expense 2 : Exact Split
        // ---------------------------
        service.addExpense(
                "u2",
                1250,
                Arrays.asList("u2", "u3"),
                SplitType.EXACT,
                Arrays.asList(370.0, 880.0)
        );

        System.out.println("\nAfter Expense 2:");
        service.showAllBalances();

        // ---------------------------
        // Expense 3 : Percent Split
        // ---------------------------
        service.addExpense(
                "u3",
                1200,
                Arrays.asList("u1", "u2", "u3"),
                SplitType.PERCENT,
                Arrays.asList(40.0, 20.0, 40.0)
        );

        System.out.println("\nAfter Expense 3:");
        service.showAllBalances();

        System.out.println("\nBalance for u2:");
        service.showBalance("u2");
    }
}

/*
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        SplitwiseService service = new SplitwiseService();

        while (sc.hasNext()) {

            String command = sc.next();

            if (command.equals("ADD_USER")) {
                String id = sc.next();
                String name = sc.next();
                service.addUser(id, name);
            }

            else if (command.equals("EXPENSE")) {

                String paidBy = sc.next();
                double amount = sc.nextDouble();
                int n = sc.nextInt();

                List<String> userIds = new ArrayList<>();
                for (int i = 0; i < n; i++)
                    userIds.add(sc.next());

                String type = sc.next();

                List<Double> values = new ArrayList<>();

                if (type.equals("EXACT") || type.equals("PERCENT")) {
                    for (int i = 0; i < n; i++)
                        values.add(sc.nextDouble());
                }

                service.addExpense(paidBy, amount, userIds, type, values);
            }

            else if (command.equals("SHOW")) {

                if (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();

                    if (line.isEmpty())
                        service.showAll();
                    else
                        service.showUser(line);
                } else {
                    service.showAll();
                }
            }
        }

        sc.close();
    }
}

 */
