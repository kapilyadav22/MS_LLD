#include <bits/stdc++.h>
using namespace std;
using namespace chrono;

using TimePoint = steady_clock::time_point;

TimePoint nowTime() {
    return steady_clock::now();
}

/* ================= ENUM ================= */

enum class VehicleSize { SMALL, MEDIUM, LARGE };

/* ================= VEHICLE ================= */

class Vehicle {
    string number;
    VehicleSize size;

public:
    Vehicle(string num, VehicleSize s)
        : number(num), size(s) {}

    VehicleSize getSize() const { return size; }
    string getNumber() const { return number; }
};

/* ================= SLOT ================= */

class ParkingSlot {
    string id;
    VehicleSize capacity;
    bool free = true;
    shared_ptr<Vehicle> vehicle;

public:
    ParkingSlot(string id, VehicleSize cap)
        : id(id), capacity(cap) {}

    bool isFree() const { return free; }

    bool canFit(shared_ptr<Vehicle> v) {
        return free && (int)v->getSize() <= (int)capacity;
    }

    void park(shared_ptr<Vehicle> v) {
        free = false;
        vehicle = v;
    }

    void unpark() {
        free = true;
        vehicle = nullptr;
    }

    string getId() const { return id; }
};

/* ================= FLOOR ================= */

class Floor {
    string id;
    vector<shared_ptr<ParkingSlot>> slots;
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

    string getId() const { return id; }
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

    long durationSeconds() const {
        auto sec = duration_cast<seconds>(exitT - entry).count();
        return max(1L, sec);
    }

    string getId() const { return id; }
    shared_ptr<Vehicle> getVehicle() const { return vehicle; }
    shared_ptr<Floor> getFloor() const { return floor; }
    int getSlotIdx() const { return slotIdx; }
};

/* ================= PRICING ================= */

class PricingStrategy {
public:
    virtual double calc(VehicleSize size, long sec) = 0;
    virtual ~PricingStrategy() = default;
};

class SimplePricing : public PricingStrategy {
    unordered_map<VehicleSize, double> rate;

public:
    SimplePricing() {
        rate[VehicleSize::SMALL] = 1;
        rate[VehicleSize::MEDIUM] = 3;
        rate[VehicleSize::LARGE] = 5;
    }

    double calc(VehicleSize size, long sec) override {
        return rate[size] * sec;
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
            t->getVehicle()->getSize(), sec);

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
                num, VehicleSize::MEDIUM);

            lot.park(v);
        }
        else {
            string id; cin >> id;
            lot.unpark(id);
        }
    }

    return 0;
}