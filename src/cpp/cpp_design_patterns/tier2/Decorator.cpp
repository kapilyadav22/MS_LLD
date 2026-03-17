/*
Component
  ↑
Decorator
  ↑
Concrete Decorators
*/

#include <iostream>
#include <memory>
using namespace std;

class Coffee {
public:
    virtual int cost() = 0;
};

class BasicCoffee : public Coffee {
public:
    int cost() override {
        return 100;
    }
};

class MilkDecorator : public Coffee {
    shared_ptr<Coffee> coffee;

public:
    MilkDecorator(shared_ptr<Coffee> coffee) {
        this->coffee = coffee;
    }

    int cost() override {
        return coffee->cost() + 20;
    }
};

int main() {
    shared_ptr<Coffee> coffee = make_shared<BasicCoffee>();
    coffee = make_shared<MilkDecorator>(coffee);
    cout << coffee->cost();
}