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
    Vehicle* vehicle = nullptr; // non-owning

public:
    ParkingSlot(string id, VehicleSize cap)
        : id(id), capacity(cap) {}

    bool isFree() const { return free; }

    bool canFit(Vehicle* v) {
        return free && (int)v->getSize() <= (int)capacity;
    }

    void park(Vehicle* v) {
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
    vector<unique_ptr<ParkingSlot>> slots;
    set<int> freeSlots;
    mutex mtx;

public:
    Floor(string id, vector<unique_ptr<ParkingSlot>> s)
        : id(id), slots(move(s)) {

        for (int i = 0; i < slots.size(); i++) {
            freeSlots.insert(i);
        }
    }

    pair<ParkingSlot*, int> allocate(Vehicle* v) {
        lock_guard<mutex> lock(mtx);

        for (int idx : freeSlots) {
            if (slots[idx]->canFit(v)) {
                freeSlots.erase(idx);
                slots[idx]->park(v);
                return {slots[idx].get(), idx};
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

    Vehicle* vehicle; // non-owning
    Floor* floor;     // non-owning
    int slotIdx;

public:
    Ticket(string id, Vehicle* v, Floor* f, int idx)
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
    Vehicle* getVehicle() const { return vehicle; }
    Floor* getFloor() const { return floor; }
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

    vector<unique_ptr<Floor>> floors;
    unordered_map<string, unique_ptr<Ticket>> active;
    unique_ptr<PricingStrategy> pricing;

public:

    ParkingLot(vector<unique_ptr<Floor>> f,
               unique_ptr<PricingStrategy> p)
        : floors(move(f)), pricing(move(p)) {}

    string park(Vehicle* v) {

        for (auto &floor : floors) {

            auto res = floor->allocate(v);

            if (res.first != nullptr) {

                string id = "T" + to_string(rand());

                auto ticket = make_unique<Ticket>(
                    id, v, floor.get(), res.second);

                active[id] = move(ticket);

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

        auto &t = active[id];
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

    vector<unique_ptr<Floor>> floors;

    for (int i = 0; i < 2; i++) {

        vector<unique_ptr<ParkingSlot>> slots;

        for (int j = 0; j < 5; j++) {
            slots.push_back(make_unique<ParkingSlot>(
                "S" + to_string(j), VehicleSize::LARGE));
        }

        floors.push_back(make_unique<Floor>(
            "F" + to_string(i), move(slots)));
    }

    ParkingLot lot(move(floors), make_unique<SimplePricing>());

    // Vehicles (stack-based → safest for interview)
    Vehicle v1("CAR123", VehicleSize::MEDIUM);
    Vehicle v2("BIKE123", VehicleSize::SMALL);
    Vehicle v3("TRUCK123", VehicleSize::LARGE);

    string t1 = lot.park(&v1);
    string t2 = lot.park(&v2);
    string t3 = lot.park(&v3);

    this_thread::sleep_for(seconds(2));

    lot.unpark(t1);
    lot.unpark(t2);
    lot.unpark(t3);

    return 0;
}