/*
Strategy (interface)
   ↑
ConcreteStrategyA
ConcreteStrategyB
Context (uses strategy)

*/


#include <iostream>
#include <memory>
using namespace std;

class PaymentStrategy {
public:
    virtual void pay(int amount) = 0;
    virtual ~PaymentStrategy() = default;
};

class CardPayment : public PaymentStrategy {
public:
    void pay(int amount) override {
        cout << "Paid " << amount << " using Card\n";
    }
};

class UpiPayment : public PaymentStrategy {
public:
    void pay(int amount) override {
        cout << "Paid " << amount << " using UPI\n";
    }
};

class PaymentService {
private:
    shared_ptr<PaymentStrategy> strategy;

public:
    PaymentService(shared_ptr<PaymentStrategy> strategy) {
        this->strategy = strategy;
    }

    void process(int amount) {
        strategy->pay(amount);
    }
};

int main() {
    auto strategy = make_shared<CardPayment>();
    PaymentService service(strategy);
    service.process(500);
}