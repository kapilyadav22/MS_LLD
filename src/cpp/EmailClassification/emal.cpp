#include <bits/stdc++.h>
using namespace std;

enum class Category {
    PRIMARY,
    PROMOTIONS,
    SOCIAL,
    UPDATES,
    SPAM
};

string categoryToString(Category c) {
    switch(c) {
        case Category::PRIMARY: return "PRIMARY";
        case Category::PROMOTIONS: return "PROMOTIONS";
        case Category::SOCIAL: return "SOCIAL";
        case Category::UPDATES: return "UPDATES";
        case Category::SPAM: return "SPAM";
    }
    return "UNKNOWN";
}

class Email {
public:
    string id, from, subject, body;
    vector<string> to;

    Email(string id, string from, vector<string> to,
          string subject, string body)
        : id(id), from(from), to(to),
          subject(subject), body(body) {}
};

string toLower(string s) {
    transform(s.begin(), s.end(), s.begin(), ::tolower);
    return s;
}

// ================= STRATEGY =================
class EmailClassifier {
public:
    virtual Category classify(const Email& email) = 0;
    virtual ~EmailClassifier() {}
};

// ================= RULE =================
class Rule {
public:
    virtual bool matches(const Email& email) = 0;
    virtual Category getCategory() = 0;
    virtual ~Rule() {}
};

class PromotionKeywordRule : public Rule {
public:
    bool matches(const Email& email) override {
        string subject = toLower(email.subject);
        string body = toLower(email.body);

        return subject.find("sale") != string::npos ||
               body.find("discount") != string::npos;
    }

    Category getCategory() override {
        return Category::PROMOTIONS;
    }
};

class SpamSenderRule : public Rule {
private:
    unordered_set<string> blacklist {
        "spam@bad.com",
        "fraud@scam.com"
    };

public:
    bool matches(const Email& email) override {
        return blacklist.count(email.from);
    }

    Category getCategory() override {
        return Category::SPAM;
    }
};

// ================= RULE BASED =================
class RuleBasedClassifier : public EmailClassifier {
private:
    vector<Rule*> rules;

public:
    RuleBasedClassifier(const vector<Rule*>& rules)
        : rules(rules) {}

    Category classify(const Email& email) override {
        for (auto rule : rules) {
            if (rule->matches(email))
                return rule->getCategory();
        }
        return Category::PRIMARY;
    }
};

// ================= ML =================
class MLModel {
public:
    Category predict(const Email& email) {
        if (email.from.find("linkedin") != string::npos)
            return Category::SOCIAL;
        return Category::PRIMARY;
    }
};

class MLBasedClassifier : public EmailClassifier {
private:
    MLModel model;

public:
    Category classify(const Email& email) override {
        return model.predict(email);
    }
};

// ================= HYBRID =================
class HybridEmailClassifier : public EmailClassifier {
private:
    EmailClassifier* ruleClassifier;
    EmailClassifier* mlClassifier;

public:
    HybridEmailClassifier(EmailClassifier* r, EmailClassifier* m)
        : ruleClassifier(r), mlClassifier(m) {}

    Category classify(const Email& email) override {
        Category ruleResult = ruleClassifier->classify(email);
        if (ruleResult != Category::PRIMARY)
            return ruleResult;
        return mlClassifier->classify(email);
    }
};

// ================= SERVICE =================
class EmailClassificationService {
private:
    EmailClassifier& classifier;

public:
    EmailClassificationService(EmailClassifier& classifier)
        : classifier(classifier) {}

    Category classifyEmail(const Email& email) {
        return classifier.classify(email);
    }
};

// ================= MAIN =================
int main() {

    int t;
    cin >> t;

    while (t--) {

        string id, from, subject, body;
        int n;

        cin >> id >> from >> n;

        vector<string> to(n);
        for (int i = 0; i < n; i++)
            cin >> to[i];

        cin >> subject >> body;

        Email email(id, from, to, subject, body);

        // RULES (STACK)
        PromotionKeywordRule promoRule;
        SpamSenderRule spamRule;

        vector<Rule*> rules = { &promoRule, &spamRule };

        RuleBasedClassifier ruleClassifier(rules);
        MLBasedClassifier mlClassifier;

        HybridEmailClassifier hybrid(&ruleClassifier, &mlClassifier);
        EmailClassificationService service(hybrid);

        cout << categoryToString(service.classifyEmail(email)) << "\n";
    }

    return 0;
}


/*
📥 Input Format (Clean cin-based)
    t
    id from n
    to1 to2 ...
    subject body

📤 Example Input
    2
    1 offers@amazon.com 1
    user@gmail.com
    Sale discount
    2 spam@bad.com 1
    user@gmail.com
    Hello world

Output
    PROMOTIONS
    SPAM
*/