//import java.util.*;
//
//public class ParkingLotDemo {
//
//    public static void main(String[] args) {
//
//        ParkingFloor floor1 = new ParkingFloor(
//                1,
//                List.of(
//                        ParkingSlotFactory.createSlot("F1-S1", SlotType.SMALL),
//                        ParkingSlotFactory.createSlot("F1-S2", SlotType.MEDIUM)
//                )
//        );
//
//        ParkingFloor floor2 = new ParkingFloor(
//                2,
//                List.of(
//                        ParkingSlotFactory.createSlot("F2-S1", SlotType.MEDIUM),
//                        ParkingSlotFactory.createSlot("F2-S2", SlotType.LARGE)
//                )
//        );
//
//        ParkingLot parkingLot = new ParkingLot(
//                "PL-1",
//                List.of(floor1, floor2),
//                new VehicleTypeHourlyPricingStrategy(),
//                new SimplePaymentService()
//        );
//
//        Vehicle car = new Vehicle("DL-01-AB-1234", VehicleType.CAR);
//
//        Ticket ticket = parkingLot.parkVehicle(car);
//
//        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
//
//        double amount = parkingLot.unparkVehicle(ticket);
//
//        System.out.println("Total amount paid: ₹" + amount);
//    }
//}
//
//enum VehicleType {
//    BIKE, CAR, TRUCK
//}
//
//enum SlotType {
//    SMALL, MEDIUM, LARGE
//}
//
//enum SlotStatus {
//    FREE, OCCUPIED
//}
//
//class Vehicle {
//    private final String number;
//    private final VehicleType type;
//
//    public Vehicle(String number, VehicleType type) {
//        this.number = number;
//        this.type = type;
//    }
//
//    public VehicleType getType() {
//        return type;
//    }
//
//    public String getNumber() {
//        return number;
//    }
//}
//
//abstract class ParkingSlot {
//    protected final String slotId;
//    protected SlotStatus status = SlotStatus.FREE;
//    protected Vehicle parkedVehicle;
//
//    public ParkingSlot(String slotId) {
//        this.slotId = slotId;
//    }
//
//    public boolean isFree() {
//        return status == SlotStatus.FREE;
//    }
//
//    public void park(Vehicle vehicle) {
//        if (!isFree()) {
//            throw new IllegalStateException("Slot occupied");
//        }
//        this.parkedVehicle = vehicle;
//        this.status = SlotStatus.OCCUPIED;
//    }
//
//    public void free() {
//        this.parkedVehicle = null;
//        this.status = SlotStatus.FREE;
//    }
//
//    public abstract boolean canFit(Vehicle vehicle);
//}
//
//class SmallParkingSlot extends ParkingSlot {
//    public SmallParkingSlot(String slotId) {
//        super(slotId);
//    }
//
//    public boolean canFit(Vehicle vehicle) {
//        return vehicle.getType() == VehicleType.BIKE;
//    }
//}
//
//class MediumParkingSlot extends ParkingSlot {
//    public MediumParkingSlot(String slotId) {
//        super(slotId);
//    }
//
//    public boolean canFit(Vehicle vehicle) {
//        return vehicle.getType() == VehicleType.CAR;
//    }
//}
//
//class LargeParkingSlot extends ParkingSlot {
//    public LargeParkingSlot(String slotId) {
//        super(slotId);
//    }
//
//    public boolean canFit(Vehicle vehicle) {
//        return vehicle.getType() == VehicleType.TRUCK;
//    }
//}
//
//class ParkingFloor {
//    private final int floorNumber;
//    private final List<ParkingSlot> slots;
//
//    public ParkingFloor(int floorNumber, List<ParkingSlot> slots) {
//        this.floorNumber = floorNumber;
//        this.slots = slots;
//    }
//
//    public ParkingSlot findAvailableSlot(Vehicle vehicle) {
//        for (ParkingSlot slot : slots) {
//            if (slot.isFree() && slot.canFit(vehicle)) {
//                return slot;
//            }
//        }
//        return null;
//    }
//}
//
//class Ticket {
//    private final String ticketId;
//    private final long entryTime;
//    private long exitTime;
//    private final ParkingSlot slot;
//    private final Vehicle vehicle;
//
//    public Ticket(String ticketId, ParkingSlot slot, Vehicle vehicle) {
//        this.ticketId = ticketId;
//        this.slot = slot;
//        this.vehicle = vehicle;
//        this.entryTime = System.currentTimeMillis();
//    }
//
//    public void close() {
//        this.exitTime = System.currentTimeMillis();
//    }
//
//    public long durationInHours() {
//        return Math.max(1, (exitTime - entryTime) / (1000 * 60 * 60));
//    }
//
//    public Vehicle getVehicle() {
//        return vehicle;
//    }
//
//    public ParkingSlot getSlot() {
//        return slot;
//    }
//
//    public String getTicketId() {
//        return ticketId;
//    }
//}
//
//interface PricingStrategy {
//    double calculatePrice(VehicleType vehicleType, long hours);
//}
//
//class VehicleTypeHourlyPricingStrategy implements PricingStrategy {
//
//    private final Map<VehicleType, Double> rates = Map.of(
//            VehicleType.BIKE, 20.0,
//            VehicleType.CAR, 50.0,
//            VehicleType.TRUCK, 100.0
//    );
//
//    public double calculatePrice(VehicleType vehicleType, long hours) {
//        Double rate = rates.get(vehicleType);
//        if (rate == null) {
//            throw new IllegalArgumentException("No pricing defined");
//        }
//        return rate * hours;
//    }
//}
//
//interface PaymentService {
//    void processPayment(String ticketId, double amount);
//}
//
//class SimplePaymentService implements PaymentService {
//    public void processPayment(String ticketId, double amount) {
//        System.out.println("Ticket " + ticketId + " paid ₹" + amount);
//    }
//}
//
//class ParkingLot {
//    private final String id;
//    private final List<ParkingFloor> floors;
//    private final PricingStrategy pricingStrategy;
//    private final PaymentService paymentService;
//
//    public ParkingLot(
//            String id,
//            List<ParkingFloor> floors,
//            PricingStrategy pricingStrategy,
//            PaymentService paymentService
//    ) {
//        this.id = id;
//        this.floors = floors;
//        this.pricingStrategy = pricingStrategy;
//        this.paymentService = paymentService;
//    }
//
//    public Ticket parkVehicle(Vehicle vehicle) {
//        for (ParkingFloor floor : floors) {
//            ParkingSlot slot = floor.findAvailableSlot(vehicle);
//            if (slot != null) {
//                slot.park(vehicle);
//                return new Ticket("T-" + System.nanoTime(), slot, vehicle);
//            }
//        }
//        throw new RuntimeException("No slot available in parking lot " + id);
//    }
//
//    public double unparkVehicle(Ticket ticket) {
//        ticket.close();
//        double amount = pricingStrategy.calculatePrice(
//                ticket.getVehicle().getType(),
//                ticket.durationInHours()
//        );
//        paymentService.processPayment(ticket.getTicketId(), amount);
//        ticket.getSlot().free();
//        return amount;
//    }
//}
//
//class ParkingSlotFactory {
//    public static ParkingSlot createSlot(String slotId, SlotType type) {
//        switch (type) {
//            case SMALL: return new SmallParkingSlot(slotId);
//            case MEDIUM: return new MediumParkingSlot(slotId);
//            case LARGE: return new LargeParkingSlot(slotId);
//            default: throw new IllegalArgumentException();
//        }
//    }
//}
