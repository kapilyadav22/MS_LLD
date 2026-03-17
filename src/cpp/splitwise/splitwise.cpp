#include <bits/stdc++.h>
using namespace std;

class User {
public:
    string id;
    string name;

    User(string id, string name) : id(id), name(name) {}
};

class Split {
public:
    shared_ptr<User> user;
    double amount;

    Split(shared_ptr<User> user) : user(user), amount(0) {}
};

class SplitStrategy {
public:
    virtual void calculateSplit(double amount, vector<shared_ptr<Split>>& splits) = 0;
    virtual ~SplitStrategy() {}
};

class EqualSplit : public SplitStrategy {
public:
    void calculateSplit(double amount, vector<shared_ptr<Split>>& splits) override {

        double share = amount / splits.size();

        for (auto &s : splits) {
            s->amount = share;
        }
    }
};

class ExactSplit : public SplitStrategy {
public:
    void calculateSplit(double amount, vector<shared_ptr<Split>>& splits) override {

        double total = 0;

        for (auto &s : splits)
            total += s->amount;

        if (total != amount)
            throw runtime_error("Invalid split");
    }
};

class Expense {
public:
    string id;
    double amount;
    shared_ptr<User> paidBy;
    vector<shared_ptr<Split>> splits;

    Expense(string id,
            double amount,
            shared_ptr<User> paidBy,
            vector<shared_ptr<Split>> splits)
        : id(id), amount(amount), paidBy(paidBy), splits(splits) {}
};

class ExpenseManager {

    unordered_map<string, unordered_map<string, double>> balanceSheet;

public:

    void addExpense(
        shared_ptr<Expense> expense,
        shared_ptr<SplitStrategy> strategy
    ) {

        strategy->calculateSplit(expense->amount, expense->splits);

        string payer = expense->paidBy->id;

        for (auto &split : expense->splits) {

            string user = split->user->id;

            if (user == payer)
                continue;

            balanceSheet[user][payer] += split->amount;
            balanceSheet[payer][user] -= split->amount;
        }
    }

    void showBalances() {

        for (auto &p : balanceSheet) {

            for (auto &q : p.second) {

                if (q.second > 0) {
                    cout << p.first
                         << " owes "
                         << q.first
                         << " : "
                         << q.second
                         << endl;
                }
            }
        }
    }
};

class Splitwise {

    unordered_map<string, shared_ptr<User>> users;
    shared_ptr<ExpenseManager> expenseManager;

public:

    Splitwise() {
        expenseManager = make_shared<ExpenseManager>();
    }

    void addUser(string id, string name) {
        users[id] = make_shared<User>(id, name);
    }

    shared_ptr<User> getUser(string id) {
        return users[id];
    }

    shared_ptr<ExpenseManager> getExpenseManager() {
        return expenseManager;
    }
};

int main() {

    Splitwise app;

    app.addUser("u1", "Alice");
    app.addUser("u2", "Bob");
    app.addUser("u3", "Charlie");

    auto u1 = app.getUser("u1");
    auto u2 = app.getUser("u2");
    auto u3 = app.getUser("u3");

    vector<shared_ptr<Split>> splits;

    splits.push_back(make_shared<Split>(u1));
    splits.push_back(make_shared<Split>(u2));
    splits.push_back(make_shared<Split>(u3));

    auto expense = make_shared<Expense>(
        "e1",
        3000,
        u1,
        splits
    );

    auto strategy = make_shared<EqualSplit>();

    app.getExpenseManager()->addExpense(expense, strategy);

    app.getExpenseManager()->showBalances();
}


/*

1. Supported Commands (Hackerrank Style)
ADD_USER <id> <name>

EXPENSE <paidBy> <amount> <numUsers> <user1> <user2> ... <userN> <type> [values]

SHOW

SHOW <user>
Example Input 1:
    ADD_USER u1 Alice
    ADD_USER u2 Bob
    ADD_USER u3 Charlie
    EXPENSE u1 3000 3 u1 u2 u3 EQUAL
    SHOW


Expected Output:
    u2 owes u1 : 1000
    u3 owes u1 : 1000

Example Input 2
    EXPENSE u2 1250 2 u2 u3 EXACT 370 880
    SHOW

Output
    u2 owes u1 : 1000
    u3 owes u1 : 1000
    u3 owes u2 : 880


2. Design (Same LLD Architecture)

Classes used
    User
    Split
    SplitStrategy

    EqualSplit
    ExactSplit
    PercentSplit

    Expense
    ExpenseManager
    SplitwiseApp
*/