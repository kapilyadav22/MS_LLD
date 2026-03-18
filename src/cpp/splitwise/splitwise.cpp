#include <bits/stdc++.h>
using namespace std;

class User {
public:
    string id, name;
    User(string id, string name) : id(id), name(name) {}
};

class Split {
public:
    User* user;
    double amount;

    Split(User* user) : user(user), amount(0) {}
};

class SplitStrategy {
public:
    virtual void calculateSplit(double amount, vector<Split>& splits) = 0;
    virtual ~SplitStrategy() {}
};

class EqualSplit : public SplitStrategy {
public:
    void calculateSplit(double amount, vector<Split>& splits) override {
        double share = amount / splits.size();
        for (auto &s : splits) s.amount = share;
    }
};

class ExactSplit : public SplitStrategy {
public:
    void calculateSplit(double amount, vector<Split>& splits) override {
        double total = 0;
        for (auto &s : splits) total += s.amount;

        if (abs(total - amount) > 1e-6)
            throw runtime_error("Exact split mismatch");
    }
};

class Expense {
public:
    string id;
    double amount;
    User* paidBy;
    vector<Split> splits;

    Expense(string id, double amount, User* paidBy, vector<Split> splits)
        : id(id), amount(amount), paidBy(paidBy), splits(splits) {}
};

class ExpenseManager {
    unordered_map<string, unordered_map<string, double>> balanceSheet;

public:
    void addExpense(Expense& expense, SplitStrategy& strategy) {

        strategy.calculateSplit(expense.amount, expense.splits);
        string payer = expense.paidBy->id;

        for (auto &split : expense.splits) {
            string user = split.user->id;
            if (user == payer) continue;

            balanceSheet[user][payer] += split.amount;
            balanceSheet[payer][user] -= split.amount;
        }
    }

    void showBalances() {
        bool empty = true;

        for (auto &p : balanceSheet) {
            for (auto &q : p.second) {
                if (q.second > 0) {
                    cout << p.first << " owes "
                         << q.first << " : "
                         << q.second << "\n";
                    empty = false;
                }
            }
        }

        if (empty) cout << "No balances\n";
    }

    void showBalances(const string& user) {
        bool empty = true;

        if (balanceSheet.count(user)) {
            for (auto &q : balanceSheet[user]) {
                if (q.second > 0) {
                    cout << user << " owes "
                         << q.first << " : "
                         << q.second << "\n";
                    empty = false;
                }
            }
        }

        for (auto &p : balanceSheet) {
            if (p.second.count(user) && p.second[user] > 0) {
                cout << p.first << " owes "
                     << user << " : "
                     << p.second[user] << "\n";
                empty = false;
            }
        }

        if (empty) cout << "No balances\n";
    }
};

class SplitwiseService {
    unordered_map<string, User> users;
    ExpenseManager expenseManager;
    int expenseId = 1;

public:
    void addUser(string id, string name) {
        users.emplace(id, User(id, name));
    }

    User* getUser(const string& id) {
        auto it = users.find(id);
        return (it != users.end()) ? &it->second : nullptr;
    }

    void addExpense(string paidBy, double amount,
                    vector<string>& userIds,
                    string type,
                    vector<double>& values) {

        User* payer = getUser(paidBy);
        if (!payer) {
            cout << "User not found\n";
            return;
        }

        vector<Split> splits;
        for (auto &id : userIds) {
            User* u = getUser(id);
            if (!u) {
                cout << "User not found\n";
                return;
            }
            splits.emplace_back(u);
        }

        unique_ptr<SplitStrategy> strategy;

        if (type == "EQUAL") {
            strategy = make_unique<EqualSplit>();
        }
        else if (type == "EXACT") {
            if (values.size() != splits.size()) {
                cout << "Invalid values\n";
                return;
            }
            for (int i = 0; i < splits.size(); i++)
                splits[i].amount = values[i];

            strategy = make_unique<ExactSplit>();
        }
        else {
            cout << "Invalid type\n";
            return;
        }

        string id = "e" + to_string(expenseId++);
        Expense expense(id, amount, payer, splits);

        expenseManager.addExpense(expense, *strategy);
    }

    void showBalances() {
        expenseManager.showBalances();
    }

    void showBalances(string user) {
        if (!getUser(user)) {
            cout << "User not found\n";
            return;
        }
        expenseManager.showBalances(user);
    }
};

int main() {

    SplitwiseService service;
    string command;

    while (cin >> command) {

        if (command == "EXIT") break;

        else if (command == "ADD_USER") {
            string id, name;
            cin >> id >> name;
            service.addUser(id, name);
        }

        else if (command == "EXPENSE") {
            string paidBy, type;
            double amount;
            int n;

            cin >> paidBy >> amount >> n;

            vector<string> users(n);
            for (int i = 0; i < n; i++)
                cin >> users[i];

            cin >> type;

            vector<double> values;
            if (type == "EXACT") {
                values.resize(n);
                for (int i = 0; i < n; i++)
                    cin >> values[i];
            }

            service.addExpense(paidBy, amount, users, type, values);
        }

        else if (command == "SHOW") {
            string user;

            if (cin.peek() == '\n') {
                service.showBalances();
            } else {
                cin >> user;
                service.showBalances(user);
            }
        }
    }

    return 0;
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
    EXIT


Expected Output:
    u2 owes u1 : 1000
    u3 owes u1 : 1000

Example Input 2
    EXPENSE u2 1250 2 u2 u3 EXACT 370 880
    SHOW u1

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