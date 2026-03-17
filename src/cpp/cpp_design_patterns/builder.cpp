#include <iostream>
using namespace std;

class House {
public:
    int doors;
    int windows;
};

class HouseBuilder {
private:
    House house;

public:
    HouseBuilder& setDoors(int d) {
        house.doors = d;
        return *this;
    }

    HouseBuilder& setWindows(int w) {
        house.windows = w;
        return *this;
    }

    House build() {
        return house;
    }
};

int main() {

    House house = HouseBuilder()
                    .setDoors(2)
                    .setWindows(4)
                    .build();

    cout << house.doors;
}


/*
HTTP request
Complex object
SQL query builder

When to use
    object has many optional fields

Examples:
    HTTP request
    Game character builder
    SQL query builder
*/
