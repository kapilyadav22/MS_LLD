#include <iostream>
#include <vector>
#include <memory>
#include <unordered_map>
#include <chrono>
#include <thread>

using namespace std;

enum class VehicleType {
    BIKE, SCOOTY, AUTO, CAR, TRUCK
};

enum class VehicleSize {
    SMALL, MEDIUM, LARGE
};

enum class SlotStatus {
    FREE, OCCUPIED
};

class Vehicle {

private:
    string number;
    VehicleType type;
    VehicleSize size;

public:

    Vehicle(string number, VehicleType type, VehicleSize size) {
        this->number = number;
        this->type = type;
        this->size = size;
    }

    VehicleType getType() {
        return type;
    }

    VehicleSize getSize() {
        return size;
    }

    string getNumber() {
        return number;
    }
};

/* ===================== PARKING SLOT ===================== */

class ParkingSlot {

protected:
    string slotId;
    VehicleSize capacity;
    SlotStatus status = SlotStatus::FREE;
    shared_ptr<Vehicle> parkedVehicle;

public:

    ParkingSlot(string slotId, VehicleSize capacity) {
        this->slotId = slotId;
        this->capacity = capacity;
    }

    bool isFree() {
        return status == SlotStatus::FREE;
    }

    bool canFit(shared_ptr<Vehicle> vehicle) {
        return (int)vehicle->getSize() <= (int)capacity;
    }

    void park(shared_ptr<Vehicle> vehicle) {

        if(!isFree()) {
            throw runtime_error("Slot occupied");
        }

        parkedVehicle = vehicle;
        status = SlotStatus::OCCUPIED;
    }

    void free() {
        parkedVehicle = nullptr;
        status = SlotStatus::FREE;
    }
};

/* ===================== SIZED SLOT ===================== */

class SizedParkingSlot : public ParkingSlot {

public:
    SizedParkingSlot(string slotId, VehicleSize capacity)
        : ParkingSlot(slotId, capacity) {}
};

/* ===================== TICKET ===================== */

class Ticket {

private:
    string ticketId;

    chrono::time_point<chrono::system_clock> entryTime;
    chrono::time_point<chrono::system_clock> exitTime;

    shared_ptr<ParkingSlot> slot;
    shared_ptr<Vehicle> vehicle;

public:

    Ticket(string ticketId,
           shared_ptr<ParkingSlot> slot,
           shared_ptr<Vehicle> vehicle) {

        this->ticketId = ticketId;
        this->slot = slot;
        this->vehicle = vehicle;
        entryTime = chrono::system_clock::now();
    }

    void close() {
        exitTime = chrono::system_clock::now();
    }

    long durationInHours() {

        auto duration =
        chrono::duration_cast<chrono::hours>(exitTime - entryTime).count();

        return max((long)1, duration);
    }

    shared_ptr<Vehicle> getVehicle() {
        return vehicle;
    }

    shared_ptr<ParkingSlot> getSlot() {
        return slot;
    }

    string getTicketId() {
        return ticketId;
    }
};

/* ===================== PRICING STRATEGY ===================== */

class PricingStrategy {

public:
    virtual double calculatePrice(VehicleType type, long hours) = 0;
    virtual ~PricingStrategy() {}
};

class VehicleTypeHourlyPricingStrategy : public PricingStrategy {

private:

    unordered_map<VehicleType,double> rates;

public:

    VehicleTypeHourlyPricingStrategy() {

        rates[VehicleType::BIKE] = 10;
        rates[VehicleType::SCOOTY] = 15;
        rates[VehicleType::AUTO] = 30;
        rates[VehicleType::CAR] = 50;
        rates[VehicleType::TRUCK] = 100;
    }

    double calculatePrice(VehicleType type, long hours) override {

        return rates[type] * hours;
    }
};

/* ===================== PAYMENT ===================== */

class PaymentService {

public:
    virtual void processPayment(string ticketId, double amount) = 0;
    virtual ~PaymentService() {}
};

class SimplePaymentService : public PaymentService {

public:

    void processPayment(string ticketId, double amount) override {

        cout << "Ticket "
             << ticketId
             << " paid ₹"
             << amount
             << endl;
    }
};

/* ===================== PARKING LOT ===================== */

class ParkingLot {

private:

    string id;

    vector<shared_ptr<ParkingSlot>> slots;

    shared_ptr<PricingStrategy> pricingStrategy;
    shared_ptr<PaymentService> paymentService;

public:

    ParkingLot(
        string id,
        vector<shared_ptr<ParkingSlot>> slots,
        shared_ptr<PricingStrategy> pricingStrategy,
        shared_ptr<PaymentService> paymentService
    ) {

        this->id = id;
        this->slots = slots;
        this->pricingStrategy = pricingStrategy;
        this->paymentService = paymentService;
    }

    shared_ptr<Ticket> parkVehicle(shared_ptr<Vehicle> vehicle) {

        for(auto &slot : slots) {

            if(slot->isFree() && slot->canFit(vehicle)) {

                slot->park(vehicle);

                string ticketId =
                    "T-" + to_string(rand());

                return make_shared<Ticket>(ticketId, slot, vehicle);
            }
        }

        throw runtime_error("No slot available");
    }

    void unparkVehicle(shared_ptr<Ticket> ticket) {

        ticket->close();

        double amount =
        pricingStrategy->calculatePrice(
            ticket->getVehicle()->getType(),
            ticket->durationInHours()
        );

        paymentService->processPayment(
            ticket->getTicketId(),
            amount
        );

        ticket->getSlot()->free();
    }
};

class ParkingSlotFactory {

public:
    static shared_ptr<ParkingSlot>
    createSlot(string slotId, VehicleSize size) {
        return make_shared<SizedParkingSlot>(slotId, size);
    }
};

int main() {
    vector<shared_ptr<ParkingSlot>> slots;
    slots.push_back(ParkingSlotFactory::createSlot("S1", VehicleSize::SMALL));
    slots.push_back(ParkingSlotFactory::createSlot("S2", VehicleSize::MEDIUM));
    slots.push_back(ParkingSlotFactory::createSlot("S3", VehicleSize::LARGE));

    ParkingLot parkingLot(
        "PL-1",
        slots,
        make_shared<VehicleTypeHourlyPricingStrategy>(),
        make_shared<SimplePaymentService>()
    );

    auto scooty = make_shared<Vehicle>("DL-01-SC-1111",VehicleType::SCOOTY,VehicleSize::SMALL);
    auto autoV = make_shared<Vehicle>("DL-01-AU-2222",VehicleType::AUTO,VehicleSize::MEDIUM);
    auto car =make_shared<Vehicle>("DL-01-CA-3333",VehicleType::CAR,VehicleSize::MEDIUM);
    auto t1 = parkingLot.parkVehicle(scooty);
    auto t2 = parkingLot.parkVehicle(autoV);
    this_thread::sleep_for(chrono::seconds(2));
    parkingLot.unparkVehicle(t1);
    parkingLot.unparkVehicle(t2);

    auto t3 = parkingLot.parkVehicle(car);
    parkingLot.unparkVehicle(t3);

    return 0;
}