package ParkingLot;

import java.util.*;

enum VehicleType {
    BIKE, SCOOTY, AUTO, CAR, TRUCK
}

enum VehicleSize {
    SMALL, MEDIUM, LARGE
}

enum SlotStatus {
    FREE, OCCUPIED
}

class Vehicle {
    private final String number;
    private final VehicleType type;
    private final VehicleSize size;

    public Vehicle(String number, VehicleType type, VehicleSize size) {
        this.number = number;
        this.type = type;
        this.size = size;
    }

    public VehicleType getType() {
        return type;
    }

    public VehicleSize getSize() {
        return size;
    }

    public String getNumber() {
        return number;
    }
}

abstract class ParkingSlot {
    protected final String slotId;
    protected final VehicleSize capacity;
    protected SlotStatus status = SlotStatus.FREE;
    protected Vehicle parkedVehicle;

    public ParkingSlot(String slotId, VehicleSize capacity) {
        this.slotId = slotId;
        this.capacity = capacity;
    }

    public boolean isFree() {
        return status == SlotStatus.FREE;
    }

    public boolean canFit(Vehicle vehicle) {
        return vehicle.getSize().ordinal() <= capacity.ordinal();
    }

    public void park(Vehicle vehicle) {
        if (!isFree()) {
            throw new IllegalStateException("Slot occupied");
        }
        this.parkedVehicle = vehicle;
        this.status = SlotStatus.OCCUPIED;
    }

    public void free() {
        this.parkedVehicle = null;
        this.status = SlotStatus.FREE;
    }
}

class SizedParkingSlot extends ParkingSlot {
    public SizedParkingSlot(String slotId, VehicleSize capacity) {
        super(slotId, capacity);
    }
}

class Ticket {
    private final String ticketId;
    private final long entryTime;
    private long exitTime;
    private final ParkingSlot slot;
    private final Vehicle vehicle;

    public Ticket(String ticketId, ParkingSlot slot, Vehicle vehicle) {
        this.ticketId = ticketId;
        this.slot = slot;
        this.vehicle = vehicle;
        this.entryTime = System.currentTimeMillis();
    }

    public void close() {
        this.exitTime = System.currentTimeMillis();
    }

    public long durationInHours() {
        return Math.max(1, (exitTime - entryTime) / (1000 * 60 * 60));
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public String getTicketId() {
        return ticketId;
    }
}

interface PricingStrategy {
    double calculatePrice(VehicleType vehicleType, long hours);
}

class VehicleTypeHourlyPricingStrategy implements PricingStrategy {

    private final Map<VehicleType, Double> rates = Map.of(
            VehicleType.BIKE, 10.0,
            VehicleType.SCOOTY, 15.0,
            VehicleType.AUTO, 30.0,
            VehicleType.CAR, 50.0,
            VehicleType.TRUCK, 100.0
    );

    public double calculatePrice(VehicleType vehicleType, long hours) {
        Double rate = rates.get(vehicleType);
        if (rate == null) {
            throw new IllegalArgumentException("No pricing defined");
        }
        return rate * hours;
    }
}

interface PaymentService {
    void processPayment(String ticketId, double amount);
}

class SimplePaymentService implements PaymentService {
    public void processPayment(String ticketId, double amount) {
        System.out.println("Ticket " + ticketId + " paid ₹" + amount);
    }
}

class ParkingLot {
    private final String id;
    private final List<ParkingSlot> slots;
    private final PricingStrategy pricingStrategy;
    private final PaymentService paymentService;

    public ParkingLot(
            String id,
            List<ParkingSlot> slots,
            PricingStrategy pricingStrategy,
            PaymentService paymentService
    ) {
        this.id = id;
        this.slots = slots;
        this.pricingStrategy = pricingStrategy;
        this.paymentService = paymentService;
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        for (ParkingSlot slot : slots) {
            if (slot.isFree() && slot.canFit(vehicle)) {
                slot.park(vehicle);
                return new Ticket("T-" + System.nanoTime(), slot, vehicle);
            }
        }
        throw new RuntimeException("No slot available in parking lot " + id);
    }

    public void unparkVehicle(Ticket ticket) {
        ticket.close();
        double amount = pricingStrategy.calculatePrice(
                ticket.getVehicle().getType(),
                ticket.durationInHours()
        );
        paymentService.processPayment(ticket.getTicketId(), amount);
        ticket.getSlot().free();
//        return amount;
    }
}

class ParkingSlotFactory {
    public static ParkingSlot createSlot(String slotId, VehicleSize capacity) {
        return new SizedParkingSlot(slotId, capacity);
    }
}


public class SingleFloor {

    public static void main(String[] args) {

        List<ParkingSlot> slots = List.of(
                ParkingSlotFactory.createSlot("S1", VehicleSize.SMALL),
                ParkingSlotFactory.createSlot("S2", VehicleSize.MEDIUM),
                ParkingSlotFactory.createSlot("S3", VehicleSize.LARGE)
        );

        ParkingLot parkingLot = new ParkingLot(
                "PL-1",
                slots,
                new VehicleTypeHourlyPricingStrategy(),
                new SimplePaymentService()
        );

        Vehicle scooty = new Vehicle("DL-01-SC-1111", VehicleType.SCOOTY, VehicleSize.SMALL);
        Vehicle auto = new Vehicle("DL-01-AU-2222", VehicleType.AUTO, VehicleSize.MEDIUM);
        Vehicle car = new Vehicle("DL-01-CA-3333", VehicleType.CAR, VehicleSize.MEDIUM);

        Ticket t1 = parkingLot.parkVehicle(scooty);
        Ticket t2 = parkingLot.parkVehicle(auto);

        this_thread::sleep_for(chrono::seconds(2));

        parkingLot.unparkVehicle(t1);
        parkingLot.unparkVehicle(t2);

        Ticket t3 = parkingLot.parkVehicle(car);
        parkingLot.unparkVehicle(t3);
    }
}