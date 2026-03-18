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
enum class SlotStatus { FREE, OCCUPIED };

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

/* ================= PARKING SLOT ================= */

class ParkingSlot {
protected:
    string id;
    VehicleSize capacity;
    SlotStatus status = SlotStatus::FREE;
    shared_ptr<Vehicle> vehicle;

public:
    ParkingSlot(string id, VehicleSize cap)
        : id(id), capacity(cap) {}

    bool isFree() { return status == SlotStatus::FREE; }

    bool canFit(shared_ptr<Vehicle> v) {
        return (int)v->getSize() <= (int)capacity;
    }

    void park(shared_ptr<Vehicle> v) {
        if (!isFree()) throw runtime_error("Slot Occupied");
        vehicle = v;
        status = SlotStatus::OCCUPIED;
    }

    void free() {
        vehicle = nullptr;
        status = SlotStatus::FREE;
    }

    string getId() { return id; }
};

/* ================= TICKET ================= */

class Ticket {
    string id;
    TimePoint entry;
    TimePoint exitT;

    shared_ptr<ParkingSlot> slot;
    shared_ptr<Vehicle> vehicle;

public:
    Ticket(string id, shared_ptr<ParkingSlot> s, shared_ptr<Vehicle> v)
        : id(id), slot(s), vehicle(v) {
        entry = nowTime();
    }

    void close() {
        exitT = nowTime();
    }

    long durationSeconds() {
        auto elapsed = duration_cast<seconds>(exitT - entry).count();
        return max(1L, elapsed);
    }

    shared_ptr<Vehicle> getVehicle() { return vehicle; }
    shared_ptr<ParkingSlot> getSlot() { return slot; }
    string getId() { return id; }
};

/* ================= PRICING ================= */

class PricingStrategy {
public:
    virtual double calculate(VehicleType type, long seconds) = 0;
    virtual ~PricingStrategy() {}
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

    double calculate(VehicleType type, long seconds) override {
        return rate[type] * seconds;
    }
};

/* ================= PAYMENT ================= */

class PaymentService {
public:
    virtual void pay(string ticketId, double amt) = 0;
    virtual ~PaymentService() {}
};

class SimplePayment : public PaymentService {
public:
    void pay(string ticketId, double amt) override {
        cout << "PAID " << ticketId << " " << amt << "\n";
    }
};

/* ================= PARKING LOT ================= */

class ParkingLot {
    vector<shared_ptr<ParkingSlot>> slots;
    unordered_map<string, shared_ptr<Ticket>> activeTickets;

    shared_ptr<PricingStrategy> pricing;
    shared_ptr<PaymentService> payment;

public:
    ParkingLot(vector<shared_ptr<ParkingSlot>> s,
               shared_ptr<PricingStrategy> p,
               shared_ptr<PaymentService> pay)
        : slots(s), pricing(p), payment(pay) {}

    string park(shared_ptr<Vehicle> v) {

        for (auto &slot : slots) {
            if (slot->isFree() && slot->canFit(v)) {

                slot->park(v);
                string ticketId = "T" + to_string(rand());

                auto ticket = make_shared<Ticket>(ticketId, slot, v);
                activeTickets[ticketId] = ticket;

                cout << "PARKED " << ticketId << " " << slot->getId() << "\n";
                return ticketId;
            }
        }

        cout << "NO_SLOT\n";
        return "";
    }

    void unpark(string ticketId) {

        if (activeTickets.find(ticketId) == activeTickets.end()) {
            cout << "INVALID_TICKET\n";
            return;
        }

        auto ticket = activeTickets[ticketId];
        ticket->close();

        long seconds = ticket->durationSeconds();

        double amt = pricing->calculate(
            ticket->getVehicle()->getType(), seconds);

        payment->pay(ticketId, amt);

        ticket->getSlot()->free();
        activeTickets.erase(ticketId);

        cout << "UNPARKED " << ticketId << "\n";
    }
};

/* ================= FACTORY ================= */

class SlotFactory {
public:
    static shared_ptr<ParkingSlot> create(string id, VehicleSize size) {
        return make_shared<ParkingSlot>(id, size);
    }
};

/* ================= HELPER ================= */

VehicleType getType(string s) {
    if (s == "BIKE") return VehicleType::BIKE;
    if (s == "SCOOTY") return VehicleType::SCOOTY;
    if (s == "AUTO") return VehicleType::AUTO;
    if (s == "CAR") return VehicleType::CAR;
    return VehicleType::TRUCK;
}

VehicleSize getSize(string s) {
    if (s == "SMALL") return VehicleSize::SMALL;
    if (s == "MEDIUM") return VehicleSize::MEDIUM;
    return VehicleSize::LARGE;
}

/* ================= MAIN ================= */

int main() {

    int n;
    cin >> n;

    vector<shared_ptr<ParkingSlot>> slots;

    for (int i = 0; i < n; i++) {
        string id, size;
        cin >> id >> size;
        slots.push_back(SlotFactory::create(id, getSize(size)));
    }

    ParkingLot lot(
        slots,
        make_shared<SimplePricing>(),
        make_shared<SimplePayment>()
    );

    int q;
    cin >> q;

    while (q--) {
        string cmd;
        cin >> cmd;

        if (cmd == "PARK") {
            string num, type, size;
            cin >> num >> type >> size;

            auto v = make_shared<Vehicle>(num, getType(type), getSize(size));
            lot.park(v);
        }
        else if (cmd == "UNPARK") {
            string ticketId;
            cin >> ticketId;
            lot.unpark(ticketId);
        }
    }

    return 0;
}