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
        return abs(x - other.x) + abs(y - other.y);
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
    Rider rider;               // owned
    Driver* driver = nullptr;  // non-owning
    Location src, dest;
    RideStatus status;

public:
    Ride(string id, Rider r, Location s, Location d)
        : rideId(id), rider(r), src(s), dest(d),
          status(RideStatus::REQUESTED) {
        cout << "Ride " << rideId << " REQUESTED\n";
    }

    void assignDriver(Driver* d) {
        driver = d;
        driver->assignRide();

        status = RideStatus::ACCEPTED;
        cout << "Ride " << rideId
             << " ACCEPTED by Driver "
             << driver->getId() << endl;
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
    virtual Driver* matchDriver(
        Location src,
        vector<Driver>& drivers) = 0;

    virtual ~MatchingStrategy() = default;
};

class NearestDriverStrategy : public MatchingStrategy {
public:
    Driver* matchDriver(
        Location src,
        vector<Driver>& drivers) override {

        Driver* best = nullptr;
        double minDist = INT_MAX;

        for(auto &d : drivers) {
            if(!d.isAvailable()) continue;

            double dist = d.getLocation().distance(src);

            if(dist < minDist) {
                minDist = dist;
                best = &d;
            }
        }
        return best;
    }
};

class RideManager {
    vector<Driver> drivers;
    unordered_map<string, Ride> rides;
    unique_ptr<MatchingStrategy> strategy;

public:
    RideManager() {
        strategy = make_unique<NearestDriverStrategy>();
    }

    void initDriver(string id, double x, double y) {
        drivers.push_back(id, Location(x,y));
        cout << "Driver " << id << " added\n";
    }

    void requestRide(string rideId, string riderId,
                     double sx, double sy,
                     double dx, double dy) {

        Ride ride(
            rideId,
            Rider(riderId),
            Location(sx,sy),
            Location(dx,dy)
        );

        Driver* driver = strategy->matchDriver(Location(sx,sy), drivers);

        if(!driver) {
            cout << "No drivers available\n";
            return;
        }

        ride.assignDriver(driver);
        rides.emplace(rideId, move(ride));
    }

    void startRide(string rideId) {
        if (auto it = rides.find(rideId); it != rides.end())
            it->second.startRide();
        else
            cout << "Ride not found\n";
    }

    void completeRide(string rideId) {
        if (auto it = rides.find(rideId); it != rides.end())
            it->second.completeRide();
        else
            cout << "Ride not found\n";
    }

    void cancelRide(string rideId) {
        if (auto it = rides.find(rideId); it != rides.end())
            it->second.cancelRide();
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