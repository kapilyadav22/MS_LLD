/*
In C++, an initializer list is used to initialize class member variables directly at the time of object creation, before the constructor body runs.
It is more efficient than assigning values inside the constructor body.
*/

/*
Constructor(parameters) : member1(value1), member2(value2) {
    // constructor body
}
*/

#include <iostream>
#include <memory>
using namespace std;

class Engine {
public:
    Engine(int hp) {
        cout << "Engine created with hp " << hp << endl;
    }
};

class Vehicle {
protected:
    int speed;

public:
    Vehicle(int s) : speed(s) {}
};

class Car : public Vehicle {

private:
    const int id;
    Engine engine;
    shared_ptr<int> data;

public:
    Car(int id, int s, int hp)
        : Vehicle(s),          // base class
          id(id),              // const member
          engine(hp),          // object member
          data(make_shared<int>(10)) // shared_ptr
    {
        cout << "Car constructed\n";
    }
};


int main() {
    Car c(1, 120, 500);
}


/*
    Account created
    Account ID: 101
    Owner: Kapil
    Balance: 5000
*/