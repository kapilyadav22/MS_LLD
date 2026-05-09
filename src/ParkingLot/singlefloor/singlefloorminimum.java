import java.util.*;

// ================= ENUMS =================
enum VehicleSize {
    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private final int level;

    VehicleSize(int level) {
        this.level = level;
    }

    boolean canFit(VehicleSize other) {
        return this.level >= other.level;
    }
}

enum SlotStatus {
    FREE,
    OCCUPIED
}

// ================= VEHICLE =================
class Vehicle {
    String number;
    VehicleSize size;

    Vehicle(String number, VehicleSize size) {
        this.number = number;
        this.size = size;
    }
}

// ================= PARKING SLOT =================
class ParkingSlot {
    String id;
    VehicleSize capacity;
    Vehicle vehicle;
    SlotStatus status;

    ParkingSlot(String id, VehicleSize capacity) {
        this.id = id;
        this.capacity = capacity;
        this.status = SlotStatus.FREE;
    }

    boolean canFit(Vehicle v) {
        return status == SlotStatus.FREE && capacity.canFit(v.size);
    }

    void park(Vehicle v) {
        this.vehicle = v;
        this.status = SlotStatus.OCCUPIED;
    }

    void unpark() {
        this.vehicle = null;
        this.status = SlotStatus.FREE;
    }
}

// ================= PRICING STRATEGY =================
interface PricingStrategy {
    double calculatePrice(long durationInSeconds);
}

// ================= SMALL VEHICLE PRICING =================
class SmallVehiclePricing implements PricingStrategy {
    public double calculatePrice(long durationInSeconds) {
        return durationInSeconds * 10;
    }
}

// ================= MEDIUM VEHICLE PRICING =================
class MediumVehiclePricing implements PricingStrategy {
    public double calculatePrice(long durationInSeconds) {
        return durationInSeconds * 20;
    }
}

// ================= LARGE VEHICLE PRICING =================
class LargeVehiclePricing implements PricingStrategy {
    public double calculatePrice(long durationInSeconds) {
        return durationInSeconds * 30;
    }
}

// ================= PRICING FACTORY =================
class PricingFactory {
    static PricingStrategy getPricingStrategy(VehicleSize size) {
        switch (size) {
            case SMALL:
                return new SmallVehiclePricing();
            case MEDIUM:
                return new MediumVehiclePricing();
            case LARGE:
                return new LargeVehiclePricing();
            default:
                throw new IllegalArgumentException("Invalid vehicle size");
        }
    }
}

// ================= TICKET =================
class Ticket {
    String id;
    Vehicle vehicle;
    ParkingSlot slot;
    long entryTime;

    Ticket(String id, Vehicle vehicle, ParkingSlot slot) {
        this.id = id;
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = System.currentTimeMillis();
    }
}

// ================= PARKING LOT =================
class ParkingLot {
    List<ParkingSlot> slots;
    Map<String, Ticket> active;

    ParkingLot(List<ParkingSlot> slots) {
        this.slots = slots;
        this.active = new HashMap<>();
    }

    public String park(Vehicle v) {
        for (ParkingSlot slot : slots) {
            if (slot.canFit(v)) {
                slot.park(v);

                String id = v.number;

                Ticket ticket = new Ticket(id, v, slot);

                active.put(id, ticket);

                System.out.println("PARKED " + id + " SLOT " + slot.id);

                return id;
            }
        }

        System.out.println("PARKING FULL");
        return "";
    }

    public void unpark(String vehicleNumber) {
        Ticket ticket = active.get(vehicleNumber);

        if (ticket == null) {
            System.out.println("INVALID VEHICLE");
            return;
        }

        long duration = Math.max(1, (System.currentTimeMillis() - ticket.entryTime) / 1000);

        PricingStrategy pricingStrategy = PricingFactory.getPricingStrategy(ticket.vehicle.size);

        double amount = pricingStrategy.calculatePrice(duration);

        ticket.slot.unpark();
        active.remove(vehicleNumber);

        System.out.println("UNPARKED " + vehicleNumber + " BILL Rs " + amount);
    }
}

// ================= MAIN =================
public class Main {
    public static void main(String[] args) throws InterruptedException {

        List<ParkingSlot> slots = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            slots.add(new ParkingSlot("S" + i, VehicleSize.LARGE));
        }

        ParkingLot lot = new ParkingLot(slots);

        Vehicle v1 = new Vehicle("CAR123", VehicleSize.MEDIUM);

        Vehicle v2 = new Vehicle("BIKE123", VehicleSize.SMALL);

        Vehicle v3 = new Vehicle("TRUCK123", VehicleSize.LARGE);

        String t1 = lot.park(v1);
        String t2 = lot.park(v2);
        String t3 = lot.park(v3);

        Thread.sleep(2000);

        lot.unpark(t1);
        lot.unpark(t2);
        lot.unpark(t3);
    }
}
