import java.util.*;

enum DriverStatus {
    AVAILABLE, BUSY
}

enum RideStatus {
    REQUESTED,
    ACCEPTED,
    STARTED,
    COMPLETED,
    CANCELLED
}

class Location {
    double x, y;

    Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double distance(Location other) {
        return Math.hypot(x - other.x, y - other.y);
    }
}

class Driver {
    private String id;
    private Location location;
    private DriverStatus status;

    Driver(String id, Location loc) {
        this.id = id;
        this.location = loc;
        this.status = DriverStatus.AVAILABLE;
    }

    String getId() {
        return id;
    }

    Location getLocation() {
        return location;
    }

    boolean isAvailable() {
        return status == DriverStatus.AVAILABLE;
    }

    void assignRide() {
        status = DriverStatus.BUSY;
    }

    void freeDriver() {
        status = DriverStatus.AVAILABLE;
    }
}

class Rider {
    private String id;

    Rider(String id) {
        this.id = id;
    }
}

class Payment {
    private double amount;

    Payment(double amount) {
        this.amount = amount;
    }

    void pay() {
        System.out.println("Payment of Rs " + amount + " completed via CASH");
    }
}

class Ride {

    private String rideId;
    private Rider rider;
    private Driver driver;

    private Location src, dest;
    private RideStatus status;

    Ride(String id, Rider r, Location s, Location d) {
        this.rideId = id;
        this.rider = r;
        this.src = s;
        this.dest = d;
        this.status = RideStatus.REQUESTED;

        System.out.println("Ride " + rideId + " REQUESTED");
    }

    void assignDriver(Driver d) {
        this.driver = d;
        driver.assignRide();
        status = RideStatus.ACCEPTED;

        System.out.println("Ride " + rideId + " ACCEPTED by Driver " + driver.getId());
    }

    void startRide() {
        if (status == RideStatus.ACCEPTED) {
            status = RideStatus.STARTED;
            System.out.println("Ride " + rideId + " STARTED");
        } else {
            System.out.println("Cannot start ride");
        }
    }

    void completeRide() {

        if (status == RideStatus.COMPLETED || status == RideStatus.CANCELLED) {
            System.out.println("Ride already finished or cancelled");
            return;
        }

        if (status == RideStatus.ACCEPTED) {
            System.out.println("Auto-starting ride " + rideId);
            status = RideStatus.STARTED;
        }

        if (status != RideStatus.STARTED) {
            System.out.println("Cannot complete ride in current state");
            return;
        }

        status = RideStatus.COMPLETED;

        if (driver != null)
            driver.freeDriver();

        double distance = src.distance(dest);
        double fare = distance * 10;

        System.out.println("Ride " + rideId + " COMPLETED");
        System.out.println("Distance: " + distance);
        System.out.println("Fare: Rs " + fare);

        Payment payment = new Payment(fare);
        payment.pay();
    }

    void cancelRide() {

        if (status == RideStatus.COMPLETED) {
            System.out.println("Cannot cancel completed ride");
            return;
        }

        status = RideStatus.CANCELLED;

        if (driver != null)
            driver.freeDriver();

        System.out.println("Ride " + rideId + " CANCELLED");
    }
}

interface MatchingStrategy {
    Driver matchDriver(Location src, List<Driver> drivers);
}

class NearestDriverStrategy implements MatchingStrategy {

    public Driver matchDriver(Location src, List<Driver> drivers) {

        Driver best = null;
        double minDist = Double.MAX_VALUE;

        for (Driver d : drivers) {

            if (!d.isAvailable()) continue;

            double dist = d.getLocation().distance(src);

            if (dist < minDist) {
                minDist = dist;
                best = d;
            }
        }

        return best;
    }
}

class RideManager {

    private List<Driver> drivers = new ArrayList<>();
    private Map<String, Ride> rides = new HashMap<>();

    private MatchingStrategy strategy = new NearestDriverStrategy();

    void initDriver(String id, double x, double y) {
        drivers.add(new Driver(id, new Location(x, y)));
        System.out.println("Driver " + id + " added");
    }

    void requestRide(String rideId, String riderId,
                     double sx, double sy,
                     double dx, double dy) {

        Rider rider = new Rider(riderId);

        Ride ride = new Ride(
                rideId, rider,
                new Location(sx, sy),
                new Location(dx, dy)
        );

        Driver driver = strategy.matchDriver(new Location(sx, sy), drivers);

        if (driver == null) {
            System.out.println("No drivers available");
            return;
        }

        ride.assignDriver(driver);
        rides.put(rideId, ride);
    }

    void startRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null)
            ride.startRide();
        else
            System.out.println("Ride not found");
    }

    void completeRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null)
            ride.completeRide();
        else
            System.out.println("Ride not found");
    }

    void cancelRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null)
            ride.cancelRide();
        else
            System.out.println("Ride not found");
    }
}

public class Main {

    public static void main(String[] args) {

        RideManager manager = new RideManager();

        // Initialize Drivers
        manager.initDriver("D1", 0, 0);
        manager.initDriver("D2", 10, 10);

        System.out.println();

        // Request Ride
        manager.requestRide("R1", "U1", 1, 1, 5, 5);

        System.out.println();

        // Start Ride
        manager.startRide("R1");

        System.out.println();

        // Complete Ride
        manager.completeRide("R1");

        System.out.println();

        // Try Cancel after completion (edge case)
        manager.cancelRide("R1");
    }
}

/*
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        RideManager manager = new RideManager();

        while (sc.hasNext()) {

            String cmd = sc.next();

            if (cmd.equals("INIT_DRIVER")) {
                String id = sc.next();
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                manager.initDriver(id, x, y);
            }

            else if (cmd.equals("REQUEST_RIDE")) {
                String rideId = sc.next();
                String riderId = sc.next();
                double sx = sc.nextDouble();
                double sy = sc.nextDouble();
                double dx = sc.nextDouble();
                double dy = sc.nextDouble();

                manager.requestRide(rideId, riderId, sx, sy, dx, dy);
            }

            else if (cmd.equals("START_RIDE")) {
                String rideId = sc.next();
                manager.startRide(rideId);
            }

            else if (cmd.equals("COMPLETE_RIDE")) {
                String rideId = sc.next();
                manager.completeRide(rideId);
            }

            else if (cmd.equals("CANCEL_RIDE")) {
                String rideId = sc.next();
                manager.cancelRide(rideId);
            }

            else if (cmd.equals("EXIT")) {
                break;
            }
        }

        sc.close();
    }
}

 */