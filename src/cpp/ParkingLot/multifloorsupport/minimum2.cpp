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

    VehicleType getType() const { return type; }
    string getNumber() const { return number; }
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

        vehicle = move(v);   // ownership transfer
        occupied = true;
        return true;
    }

    void unpark() {
        vehicle.reset();
        occupied = false;
    }

    bool isFree() const { return !occupied; }
};

/* ================= FLOOR ================= */

class Floor {
private:
    int id;
    vector<ParkingSpot> spots;

public:
    Floor(int id, int n) : id(id) {
        for (int i = 0; i < n; i++) {
            spots.emplace_back(i, VehicleType::CAR);
        }
    }

    ParkingSpot* getFreeSpot(VehicleType t, int& idx) {
        for (int i = 0; i < spots.size(); i++) {
            if (spots[i].canFit(t)) {
                idx = i;
                return &spots[i];
            }
        }
        return nullptr;
    }

    void freeSpot(int idx) {
        spots[idx].unpark();
    }

    int getId() const { return id; }
};

/* ================= TICKET ================= */

class Ticket {
private:
    string id;
    int floorId;
    int slotId;
    string vehicleNumber;

public:
    Ticket(string id, int f, int s, string num)
        : id(id), floorId(f), slotId(s), vehicleNumber(num) {}

    string getId() const { return id; }
    int getFloorId() const { return floorId; }
    int getSlotId() const { return slotId; }
    string getVehicleNumber() const { return vehicleNumber; }
};

/* ================= PARKING LOT ================= */

class ParkingLot {
private:
    vector<Floor> floors;
    unordered_map<string, Ticket> activeTickets;

public:
    ParkingLot(int f, int n) {
        for (int i = 0; i < f; i++) {
            floors.emplace_back(i, n);
        }
    }

    string park(unique_ptr<Vehicle> v) {
        if (!v) return "";

        for (auto& floor : floors) {

            int idx = -1;
            ParkingSpot* spot = floor.getFreeSpot(v->getType(), idx);

            if (spot) {
                if (spot->park(move(v))) {

                    string ticketId = "T" + to_string(rand());

                    activeTickets.emplace(
                        ticketId,
                        Ticket(ticketId, floor.getId(), idx, "VEHICLE")
                    );

                    cout << "PARKED: " << ticketId
                         << " Floor: " << floor.getId()
                         << " Slot: " << idx << endl;

                    return ticketId;
                }
            }
        }

        cout << "FULL\n";
        return "";
    }

    void unpark(const string& ticketId) {

        if (activeTickets.find(ticketId) == activeTickets.end()) {
            cout << "INVALID TICKET\n";
            return;
        }

        Ticket t = activeTickets[ticketId];

        floors[t.getFloorId()].freeSpot(t.getSlotId());

        activeTickets.erase(ticketId);

        cout << "UNPARKED: " << ticketId << endl;
    }
};

/* ================= MAIN ================= */

int main() {

    ParkingLot lot(2, 5);

    auto v1 = make_unique<Vehicle>("CAR123", VehicleType::CAR);
    auto v2 = make_unique<Vehicle>("CAR456", VehicleType::CAR);

    string t1 = lot.park(move(v1));
    string t2 = lot.park(move(v2));

    lot.unpark(t1);
    lot.unpark(t2);

    return 0;
}