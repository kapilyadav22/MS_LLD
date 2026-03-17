/*
Always use shared_ptr/unique_ptr
Avoid raw pointers
Use interfaces with pure virtual functions
Use factory functions
Keep main() simple
*/


/*
1.) Use header file :
#include<bits/stdc++.h>
using namespace std;
*/


/*
2.) Interface Template
class PaymentStrategy {
public:
    virtual void pay(double amount) = 0;
    virtual ~PaymentStrategy() = default;
};
*/

/*
3. Implementation Classes : use override
class CreditCardPayment : public PaymentStrategy {
    public:
    void pay(double amount) override {
        cout << "Paid " << amount << " using credit card\n";
    }
};

class UPIPayment : public PaymentStrategy {
    public:
    void pay(double amount) override {
        cout << "Paid " << amount << " using UPI\n";
    }
};
*/



/*
4. Core Domain Class

Example service class.

class PaymentService {
private:
    shared_ptr<PaymentStrategy> strategy;

public:
    PaymentService(shared_ptr<PaymentStrategy> strategy) {
        this->strategy = strategy;
    }

    void processPayment(double amount) {
        strategy->pay(amount);
    }
};

Notice: shared_ptr<Interface> - avoids pointer problems.
*/



/*
5. Factory Pattern Template

Useful for many LLD problems.

class PaymentFactory {
public:
    static shared_ptr<PaymentStrategy> create(string type) {
        if (type == "CARD")
            return make_shared<CreditCardPayment>();
        else if (type == "UPI")
            return make_shared<UPIPayment>();

        return nullptr;
    }
};
*/



/*
6. Manager / Controller Layer
class PaymentManager {
private:
    unordered_map<string, shared_ptr<PaymentService>> services;

public:
    void addService(string name, shared_ptr<PaymentService> service) {
        services[name] = service;
    }

    void process(string name, double amount) {
        services[name]->processPayment(amount);
    }
};
*/



/*
Clean Main Function

int main() {
    auto strategy = PaymentFactory::create("CARD");
    auto paymentService = make_shared<PaymentService>(strategy);

    PaymentManager manager;
    manager.addService("default", paymentService);
    manager.process("default", 500);

    return 0;
}
*/


/*
#include <memory>
It enables:
    shared_ptr
    make_shared
*/


/*
Use auto mainly for:
    Iterators
    Smart pointers
    Lambdas
    Range loops

Example:
    auto strategy = PaymentFactory::create("CARD");
    auto paymentService = make_shared<PaymentService>(strategy);
*/

/*
for(auto& x : v) {
    x = 10;
}

Rule:
    auto → copy
    auto& → reference
    const auto& → read-only reference
*/


| Situation             | Typical choice |
| --------------------- | -------------- |
| Object local to scope | stack          |
| Single ownership      | unique_ptr     |
| Shared ownership      | shared_ptr     |
| Non-owning dependency | reference      |

