#include <iostream>
#include <vector>
#include <unordered_set>
#include <unordered_map>
#include <algorithm>
#include <memory>

using namespace std;


enum class Category {
    PRIMARY,
    PROMOTIONS,
    SOCIAL,
    UPDATES,
    SPAM
};

class Email {

public:
    string id;
    string from;
    vector<string> to;
    string subject;
    string body;
    unordered_map<string,string> headers;

    Email(string id,
          string from,
          vector<string> to,
          string subject,
          string body) {

        this->id = id;
        this->from = from;
        this->to = to;
        this->subject = subject;
        this->body = body;
    }
};


string toLower(string s) {

    transform(s.begin(), s.end(), s.begin(), ::tolower);

    return s;
}


class EmailClassifier {

public:

    virtual Category classify(const Email& email) = 0;

    virtual ~EmailClassifier() {}
};


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


class RuleBasedClassifier : public EmailClassifier {

private:

    vector<shared_ptr<Rule>> rules;

public:

    RuleBasedClassifier(vector<shared_ptr<Rule>> rules) {

        this->rules = rules;
    }

    Category classify(const Email& email) override {

        for(auto &rule : rules) {

            if(rule->matches(email)) {

                return rule->getCategory();
            }
        }

        return Category::PRIMARY;
    }
};


class MLModel {

public:

    Category predict(const Email& email) {

        if(email.from.find("linkedin") != string::npos) {

            return Category::SOCIAL;
        }

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


class HybridEmailClassifier : public EmailClassifier {

private:

    EmailClassifier* ruleClassifier;
    EmailClassifier* mlClassifier;

public:

    HybridEmailClassifier(
        EmailClassifier* ruleClassifier,
        EmailClassifier* mlClassifier) {

        this->ruleClassifier = ruleClassifier;
        this->mlClassifier = mlClassifier;
    }

    Category classify(const Email& email) override {

        Category ruleResult =
            ruleClassifier->classify(email);

        if(ruleResult != Category::PRIMARY) {

            return ruleResult;
        }

        return mlClassifier->classify(email);
    }
};


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


int main() {

    Email email(
        "1",
        "offers@amazon.com",
        {"user@outlook.com"},
        "Big Sale Today",
        "Huge discount on electronics"
    );

    vector<shared_ptr<Rule>> rules;

    rules.push_back(make_shared<PromotionKeywordRule>());
    rules.push_back(make_shared<SpamSenderRule>());

    RuleBasedClassifier ruleClassifier(rules);

    MLBasedClassifier mlClassifier;

    HybridEmailClassifier hybrid(
        &ruleClassifier,
        &mlClassifier
    );

    EmailClassificationService service(hybrid);

    Category category =
        service.classifyEmail(email);

    cout << "Email classified as "
         << (int)category
         << endl;

    return 0;
}