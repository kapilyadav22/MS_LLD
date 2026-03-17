#include <iostream>
#include <memory>

using namespace std;

class Engine {
public:
    void start() {
        cout << "Engine started\n";
    }
};

class Car {

private:
    unique_ptr<Engine> engine;

public:
    Car() {
        engine = make_unique<Engine>();
    }

    void drive() {
        engine->start();
    }
};

int main() {
    Car car;
    car.drive();
}