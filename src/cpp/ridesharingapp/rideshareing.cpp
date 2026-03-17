#include <bits/stdc++.h>
using namespace std;

enum class DriverStatus { AVAILABLE, BUSY };

class Location {
public:
    double x, y;

    Location(double x=0, double y=0) : x(x), y(y) {}

    double distance(const Location& other) const {
        return hypot(x-other.x, y-other.y);
    }
};

class Driver {

    string id;
    Location loc;
    DriverStatus status;

public:

    Driver(string id, Location l) : id(id), loc(l), status(DriverStatus::AVAILABLE) {}

    string getId() const { return id; }

    Location getLocation() const { return loc; }

    bool isAvailable() const {
        return status == DriverStatus::AVAILABLE;
    }

    void assignRide() {
        status = DriverStatus::BUSY;
    }

    void completeRide() {
        status = DriverStatus::AVAILABLE;
    }
};

class Rider {

    string id;

public:
    Rider(string id) : id(id) {}
    string getId() const { return id; }
};

class Ride {
    string rideId;
    shared_ptr<Rider> rider;
    shared_ptr<Driver> driver;

public:
    Ride(string id, shared_ptr<Rider> r) : rideId(id), rider(r) {}

    void assignDriver(shared_ptr<Driver> d) {
        driver = d;
        driver->assignRide();

        cout << "Ride " << rideId
             << " assigned to Driver "
             << driver->getId() << endl;
    }

    void completeRide() {
        if(driver) {
            driver->completeRide();
            cout << "Ride " << rideId << " completed\n";
        }
    }
};

class MatchingStrategy {

public:

    virtual shared_ptr<Driver> matchDriver(
        Location src,
        vector<shared_ptr<Driver>>& drivers) = 0;

    virtual ~MatchingStrategy() = default;
};

class NearestDriverStrategy : public MatchingStrategy {

public:

    shared_ptr<Driver> matchDriver(
        Location src,
        vector<shared_ptr<Driver>>& drivers) override {

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
    unordered_map<string, shared_ptr<Ride>> rides;

    unique_ptr<MatchingStrategy> strategy;

public:
    RideManager() {
        strategy = make_unique<NearestDriverStrategy>();
    }

    void initDriver(string id, double x, double y) {

        drivers.push_back( make_shared<Driver>(id, Location(x,y)));
        cout << "Driver " << id << " added\n";
    }

    void requestRide(string rideId, string riderId, double sx, double sy) {

        auto rider = make_shared<Rider>(riderId);
        auto ride = make_shared<Ride>(rideId, rider);
        auto driver = strategy->matchDriver(Location(sx,sy), drivers);

        if(!driver) {
            cout << "No drivers available\n";
            return;
        }

        ride->assignDriver(driver);
        rides[rideId] = ride;
    }

    void completeRide(string rideId) {
        if(rides.count(rideId)) {
            rides[rideId]->completeRide();
        }
    }
};

int main() {
    RideManager manager;
    string cmd;

    while(cin >> cmd) {
        if(cmd == "INIT_DRIVER") {
            string id;
            double x,y;
            cin >> id >> x >> y;
            manager.initDriver(id,x,y);
        }

        else if(cmd == "REQUEST_RIDE") {
            string rideId,riderId;
            double x,y;
            cin >> rideId >> riderId >> x >> y;
            manager.requestRide(rideId,riderId,x,y);
        }

        else if(cmd == "COMPLETE_RIDE") {
            string rideId;
            cin >> rideId;
            manager.completeRide(rideId);
        }

        else if(cmd == "EXIT")
            break;
    }
}

/*
INPUT:
INIT_DRIVER D1 0 0
INIT_DRIVER D2 10 10
REQUEST_RIDE RIDE1 R1 1 1
COMPLETE_RIDE RIDE1
EXIT

OUTPUT:
Driver D1 added
Driver D2 added
Ride RIDE1 assigned to Driver D1
Ride RIDE1 completed


Supported Commands (Input Format)
    INIT_DRIVER <driverId> <x> <y>
    REQUEST_RIDE <rideId> <riderId> <sourceX> <sourceY>
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