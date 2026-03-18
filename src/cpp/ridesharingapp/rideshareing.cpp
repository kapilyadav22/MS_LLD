//Problem Statement : Designing a Ride Sharing App (Uber / Ola / Rapido)


/*
1. Requirements Gathering
Functional Requirements
    Rider can request a ride.
    Driver can accept / reject ride.
    System should match nearest available driver.
    Rider can track ride status.
    Ride goes through states:
        Requested
        Accepted
        Started
        Completed
        Cancelled

Payment after ride completion.
Drivers can go online/offline.

Non Functional Requirements
    Low latency driver matching
    High scalability
    Thread safe ride allocation
    Extensible for:
    Surge pricing
    Multiple vehicle types
    Multiple payment modes
*/
#include <bits/stdc++.h>
using namespace std;

enum class DriverStatus { AVAILABLE, BUSY };

enum class RideStatus {
    REQUESTED,
    ACCEPTED,
    STARTED,
    COMPLETED,
    CANCELLED
};

class Location {
public:
    double x, y;

    Location(double x=0, double y=0) : x(x), y(y) {}

    double distance(const Location& other) const {
//        return hypot(x - other.x, y - other.y);
            return abs(x-other.x) + abs(y-other.y);
    }
};

class Driver {
    string id;
    Location loc;
    DriverStatus status;

public:
    Driver(string id, Location l)
        : id(id), loc(l), status(DriverStatus::AVAILABLE) {}

    string getId() const { return id; }
    Location getLocation() const { return loc; }
    bool isAvailable() const {
        return status == DriverStatus::AVAILABLE;
    }

    void assignRide() { status = DriverStatus::BUSY; }
    void freeDriver() { status = DriverStatus::AVAILABLE; }
};

class Rider {
    string id;
public:
    Rider(string id) : id(id) {}
};

class Payment {
    double amount;

public:
    Payment(double amt) : amount(amt) {}
    void pay() {
        cout << "Payment of Rs " << amount << " completed via CASH\n";
    }
};

class Ride {
    string rideId;
    shared_ptr<Rider> rider;
    shared_ptr<Driver> driver;
    Location src, dest;
    RideStatus status;
    Payment payment;
public:

    Ride(string id, shared_ptr<Rider> r, Location s, Location d)
        : rideId(id), rider(r), src(s), dest(d),
          status(RideStatus::REQUESTED) {
        cout << "Ride " << rideId << " REQUESTED\n";
    }

    void assignDriver(shared_ptr<Driver> d) {
        driver = d;
        driver->assignRide();
        status = RideStatus::ACCEPTED;
        cout << "Ride " << rideId << " ACCEPTED by Driver " << driver->getId() << endl;
    }

    void startRide() {
        if(status == RideStatus::ACCEPTED) {
            status = RideStatus::STARTED;
            cout << "Ride " << rideId << " STARTED\n";
        } else {
            cout << "Cannot start ride\n";
        }
    }

    void completeRide() {
        if(status == RideStatus::COMPLETED ||
           status == RideStatus::CANCELLED) {
            cout << "Ride already finished or cancelled\n";
            return;
        }

        // Auto-fix missing START
//        if(status == RideStatus::ACCEPTED) {
//            cout << "Auto-starting ride " << rideId << endl;
//            status = RideStatus::STARTED;
//        }
//
//        if(status != RideStatus::STARTED) {
//            cout << "Cannot complete ride in current state\n";
//            return;
//        }

        status = RideStatus::COMPLETED;

        if(driver) driver->freeDriver();

        double distance = src.distance(dest);
        double fare = distance * 10;

        cout << "Ride " << rideId << " COMPLETED\n";
        cout << "Distance: " << distance << endl;
        cout << "Fare: Rs " << fare << endl;

        Payment payment(fare);
        payment.pay();
    }

    void cancelRide() {
        if(status == RideStatus::COMPLETED) {
            cout << "Cannot cancel completed ride\n";
            return;
        }

        status = RideStatus::CANCELLED;
        if(driver) driver->freeDriver();
        cout << "Ride " << rideId << " CANCELLED\n";
    }
};

class MatchingStrategy {
public:
    virtual shared_ptr<Driver> matchDriver(Location src,vector<shared_ptr<Driver>>& drivers) = 0;
    virtual ~MatchingStrategy() = default;
};

class NearestDriverStrategy : public MatchingStrategy {
public:
    shared_ptr<Driver> matchDriver(Location src, vector<shared_ptr<Driver>>& drivers) override {
        shared_ptr<Driver> best = nullptr;
        double minDist = INT_MAX;

        for(auto &d : drivers) {
            if(!d->isAvailable()) continue;
            double dist = d->getLocation().distance(src);

            if(dist < minDist) {
                minDist = dist;
                best = d;
            }
        }
        return best;
    }
};

class RideManager {

    vector<shared_ptr<Driver>> drivers;
    //rideid, ride
    unordered_map<string, shared_ptr<Ride>> rides;
    unique_ptr<MatchingStrategy> strategy;

public:

    RideManager() {
         strategy = make_unique<NearestDriverStrategy>();
    }

    void initDriver(string id, double x, double y) {
        drivers.push_back(make_shared<Driver>(id, Location(x,y)));
        cout << "Driver " << id << " added\n";
    }

    void requestRide(string rideId, string riderId,double sx, double sy, double dx, double dy) {

        auto rider = make_shared<Rider>(riderId);
        auto ride = make_shared<Ride>( rideId, rider,Location(sx,sy), Location(dx,dy));
        auto driver = strategy->matchDriver(Location(sx,sy), drivers);

        if(!driver) {
            cout << "No drivers available\n";
            return;
        }

        ride->assignDriver(driver);
        rides[rideId] = ride;
    }

    void startRide(string rideId) {
        if(rides.count(rideId))
            rides[rideId]->startRide();
        else
            cout << "Ride not found\n";
    }

    void completeRide(string rideId) {
        if(rides.count(rideId))
            rides[rideId]->completeRide();
        else
            cout << "Ride not found\n";
    }

    void cancelRide(string rideId) {
        if(rides.count(rideId))
            rides[rideId]->cancelRide();
        else
            cout << "Ride not found\n";
    }
};

int main() {

    RideManager manager;
    string cmd;

    while(cin >> cmd) {
        if(cmd == "INIT_DRIVER") {
            string id; double x,y;
            cin >> id >> x >> y;
            manager.initDriver(id,x,y);
        }
        else if(cmd == "REQUEST_RIDE") {
            string rideId,riderId;
            double sx,sy,dx,dy;
            cin >> rideId >> riderId >> sx >> sy >> dx >> dy;
            manager.requestRide(rideId,riderId,sx,sy,dx,dy);
        }
        else if(cmd == "START_RIDE") {
            string rideId;
            cin >> rideId;
            manager.startRide(rideId);
        }
        else if(cmd == "COMPLETE_RIDE") {
            string rideId;
            cin >> rideId;
            manager.completeRide(rideId);
        }
        else if(cmd == "CANCEL_RIDE") {
            string rideId;
            cin >> rideId;
            manager.cancelRide(rideId);
        }
        else if(cmd == "EXIT")
            break;
    }
}

/*

Domain Entities (Core Business Objects)
    Driver
    Rider
    Ride
    Location

Service / Control Layer
    RideManager
    MatchingStrategy
    NearestDriverStrategy

INPUT:
INIT_DRIVER D1 0 0
REQUEST_RIDE R1 U1 1 1 5 5
COMPLETE_RIDE R1
EXIT

OUTPUT:
Driver D1 added
Ride R1 REQUESTED
Ride R1 ACCEPTED by Driver D1
Auto-starting ride R1
Ride R1 COMPLETED
Distance: 5.65685
Fare: Rs 56.5685
Payment of Rs 56.5685 completed via CASH

Supported Commands (Input Format)
    INIT_DRIVER <driverId> <x> <y>
    REQUEST_RIDE <rideId> <riderId> <user1> <sourceX> <sourceY> <destx> <desty>
    COMPLETE_RIDE <rideId>
    EXIT

Description:

INIT_DRIVER D1 0 0
    → Adds a driver with id D1 at location (0,0)

REQUEST_RIDE RIDE1 R1 1 1
    → Rider R1 requests ride RIDE1 from location (1,1)

COMPLETE_RIDE RIDE1
    → Marks ride RIDE1 as completed

EXIT
    → Terminates the program
*/

