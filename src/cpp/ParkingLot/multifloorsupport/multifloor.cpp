/*
New Features
    Multi-floor support
    Nearest slot allocation (priority-based)
    Thread-safe (concurrency using per-floor locks)
    Better scalability (no linear scan across all slots)
*/

#include <bits/stdc++.h>
using namespace std;
using namespace chrono;

using TimePoint = steady_clock::time_point;

TimePoint nowTime() {
    return steady_clock::now();
}

/* ================= ENUMS ================= */

enum class VehicleType { BIKE, SCOOTY, AUTO, CAR, TRUCK };
enum class VehicleSize { SMALL, MEDIUM, LARGE };

/* ================= VEHICLE ================= */

class Vehicle {
    string number;
    VehicleType type;
    VehicleSize size;

public:
    Vehicle(string num, VehicleType t, VehicleSize s)
        : number(num), type(t), size(s) {}

    VehicleType getType() { return type; }
    VehicleSize getSize() { return size; }
    string getNumber() { return number; }
};

/* ================= SLOT ================= */

class ParkingSlot {
    string id;
    VehicleSize capacity;
    bool free = true;
    shared_ptr<Vehicle> vehicle;

public:
    ParkingSlot(string id, VehicleSize cap) : id(id), capacity(cap) {}

    bool isFree() { return free; }

    bool canFit(shared_ptr<Vehicle> v) {
        return (int)v->getSize() <= (int)capacity;
    }

    void park(shared_ptr<Vehicle> v) {
        free = false;
        vehicle = v;
    }

    void unpark() {
        free = true;
        vehicle = nullptr;
    }

    string getId() { return id; }
};

/* ================= FLOOR ================= */

class Floor {
    string id;
    vector<shared_ptr<ParkingSlot>> slots;
    // FREE slots sorted → nearest allocation
    set<int> freeSlots;

    mutex mtx;

public:

    Floor(string id, vector<shared_ptr<ParkingSlot>> s)
        : id(id), slots(s) {

        for (int i = 0; i < slots.size(); i++) {
            freeSlots.insert(i);
        }
    }

    pair<shared_ptr<ParkingSlot>, int>
    allocate(shared_ptr<Vehicle> v) {

        lock_guard<mutex> lock(mtx);

        for (int idx : freeSlots) {

            if (slots[idx]->canFit(v)) {

                freeSlots.erase(idx);
                slots[idx]->park(v);

                return {slots[idx], idx};
            }
        }

        return {nullptr, -1};
    }

    void freeSlot(int idx) {

        lock_guard<mutex> lock(mtx);

        slots[idx]->unpark();
        freeSlots.insert(idx);
    }

    string getId() { return id; }
};

/* ================= TICKET ================= */

class Ticket {

    string id;
    TimePoint entry, exitT;

    shared_ptr<Vehicle> vehicle;
    shared_ptr<Floor> floor;
    int slotIdx;

public:

    Ticket(string id,
           shared_ptr<Vehicle> v,
           shared_ptr<Floor> f,
           int idx)
        : id(id), vehicle(v), floor(f), slotIdx(idx) {

        entry = nowTime();
    }

    void close() {
        exitT = nowTime();
    }

    long durationSeconds() {
        auto sec = duration_cast<seconds>(exitT - entry).count();
        return max(1L, sec);
    }

    string getId() { return id; }
    shared_ptr<Vehicle> getVehicle() { return vehicle; }
    shared_ptr<Floor> getFloor() { return floor; }
    int getSlotIdx() { return slotIdx; }
};

/* ================= PRICING ================= */

class PricingStrategy {
public:
    virtual double calc(VehicleType t, long sec) = 0;
};

class SimplePricing : public PricingStrategy {
    unordered_map<VehicleType, double> rate;

public:
    SimplePricing() {
        rate[VehicleType::BIKE] = 1;
        rate[VehicleType::SCOOTY] = 2;
        rate[VehicleType::AUTO] = 3;
        rate[VehicleType::CAR] = 5;
        rate[VehicleType::TRUCK] = 10;
    }

    double calc(VehicleType t, long sec) override {
        return rate[t] * sec;
    }
};

/* ================= PARKING LOT ================= */

class ParkingLot {

    vector<shared_ptr<Floor>> floors;

    unordered_map<string, shared_ptr<Ticket>> active;

    shared_ptr<PricingStrategy> pricing;

public:

    ParkingLot(vector<shared_ptr<Floor>> f,
               shared_ptr<PricingStrategy> p)
        : floors(f), pricing(p) {}

    string park(shared_ptr<Vehicle> v) {

        for (auto &floor : floors) {

            auto res = floor->allocate(v);

            if (res.first != nullptr) {

                string id = "T" + to_string(rand());

                auto ticket = make_shared<Ticket>(
                    id, v, floor, res.second);

                active[id] = ticket;

                cout << "PARKED " << id
                     << " FLOOR " << floor->getId()
                     << " SLOT " << res.second << "\n";

                return id;
            }
        }

        cout << "FULL\n";
        return "";
    }

    void unpark(string id) {

        if (active.find(id) == active.end()) {
            cout << "INVALID\n";
            return;
        }

        auto t = active[id];
        t->close();

        long sec = t->durationSeconds();

        double amt = pricing->calc(
            t->getVehicle()->getType(), sec);

        cout << "PAY " << id << " " << amt << "\n";

        t->getFloor()->freeSlot(t->getSlotIdx());

        active.erase(id);

        cout << "UNPARKED " << id << "\n";
    }
};

/* ================= MAIN ================= */

int main() {

    int f; cin >> f;

    vector<shared_ptr<Floor>> floors;

    for (int i = 0; i < f; i++) {

        int n; cin >> n;

        vector<shared_ptr<ParkingSlot>> slots;

        for (int j = 0; j < n; j++) {
            slots.push_back(make_shared<ParkingSlot>(
                "S" + to_string(j), VehicleSize::LARGE));
        }

        floors.push_back(make_shared<Floor>(
            "F" + to_string(i), slots));
    }

    ParkingLot lot(floors, make_shared<SimplePricing>());

    int q; cin >> q;

    while (q--) {

        string cmd; cin >> cmd;

        if (cmd == "PARK") {

            string num; cin >> num;

            auto v = make_shared<Vehicle>(
                num, VehicleType::CAR, VehicleSize::MEDIUM);

            lot.park(v);
        }
        else {

            string id; cin >> id;
            lot.unpark(id);
        }
    }

    return 0;
}