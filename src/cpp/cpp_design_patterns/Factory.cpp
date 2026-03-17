#include <iostream>
#include <memory>
using namespace std;

class Vehicle {
public:
    virtual void drive() = 0;
    virtual ~Vehicle() = default;
};

class Car : public Vehicle {
public:
    void drive() override {
        cout << "Driving Car\n";
    }
};

class Bike : public Vehicle {
public:
    void drive() override {
        cout << "Driving Bike\n";
    }
};

class VehicleFactory {
public:
    static shared_ptr<Vehicle> create(string type) {
        if(type == "CAR")
            return make_shared<Car>();

        if(type == "BIKE")
            return make_shared<Bike>();

        return nullptr;
    }
};

int main() {

    auto v = VehicleFactory::create("CAR");
    v->drive();
}