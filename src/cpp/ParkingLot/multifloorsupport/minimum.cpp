#include <bits/stdc++.h>
using namespace std;

/* ================= ENUM ================= */

enum class VehicleType {
    BIKE,
    CAR,
    TRUCK
};

/* ================= VEHICLE ================= */

class Vehicle {
private:
    string number;
    VehicleType type;

public:
    Vehicle(string num, VehicleType t)
        : number(num), type(t) {}

    VehicleType getType() const {
        return type;
    }
};

/* ================= PARKING SPOT ================= */

class ParkingSpot {
private:
    int id;
    VehicleType type;
    bool occupied;
    unique_ptr<Vehicle> vehicle;

public:
    ParkingSpot(int id, VehicleType t)
        : id(id), type(t), occupied(false) {}

    bool canFit(VehicleType t) const {
        return !occupied && t == type;
    }

    bool park(unique_ptr<Vehicle> v) {
        if (!v || !canFit(v->getType())) return false;

        vehicle = move(v);   // transfer ownership
        occupied = true;
        return true;
    }

    void unpark() {
        vehicle.reset();
        occupied = false;
    }
};

/* ================= FLOOR ================= */

class Floor {
private:
    vector<ParkingSpot> spots;

public:
    Floor(int n) {
        for (int i = 0; i < n; i++) {
            spots.emplace_back(i, VehicleType::CAR); // simple
        }
    }

    ParkingSpot* getFreeSpot(VehicleType t) {
        for (auto& spot : spots) {
            if (spot.canFit(t)) {
                return &spot;  // non-owning
            }
        }
        return nullptr;
    }
};

/* ================= PARKING LOT ================= */

class ParkingLot {
private:
    vector<Floor> floors;

public:
    ParkingLot(int numFloors, int spotsPerFloor) {
        for (int i = 0; i < numFloors; i++) {
            floors.emplace_back(spotsPerFloor);
        }
    }

    bool park(unique_ptr<Vehicle> v) {
        if (!v) return false;

        for (auto& floor : floors) {
            ParkingSpot* spot = floor.getFreeSpot(v->getType());

            if (spot) {
                return spot->park(move(v));
            }
        }

        return false;
    }
};

/* ================= MAIN ================= */

int main() {

    ParkingLot lot(2, 5);

    auto car1 = make_unique<Vehicle>("CAR123", VehicleType::CAR);
    auto car2 = make_unique<Vehicle>("CAR456", VehicleType::CAR);

    cout << lot.park(move(car1)) << endl;  // 1
    cout << lot.park(move(car2)) << endl;  // 1

    return 0;
}


/*
int main() {

    // Create parking lot with 2 floors, 5 slots each
    ParkingLot lot(2, 5);

    // Create vehicles
    auto car1 = make_unique<Vehicle>("CAR123", VehicleType::CAR, VehicleSize::MEDIUM);
    auto bike1 = make_unique<Vehicle>("BIKE123", VehicleType::BIKE, VehicleSize::SMALL);
    auto truck1 = make_unique<Vehicle>("TRUCK123", VehicleType::TRUCK, VehicleSize::LARGE);

    // Park vehicles
    string t1 = lot.park(move(car1));
    string t2 = lot.park(move(bike1));
    string t3 = lot.park(move(truck1));

    // Simulate some delay (optional)
    this_thread::sleep_for(chrono::seconds(2));

    // Unpark vehicles
    lot.unpark(t1);
    lot.unpark(t2);
    lot.unpark(t3);

    return 0;
}

*/