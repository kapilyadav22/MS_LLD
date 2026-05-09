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

//avoid slotstatus in interviews, simnply use vehicle==null in interviews
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

// ================= FLOOR =================
class Floor {
    String id;
    List<ParkingSlot> slots;

        Floor(String id, List<ParkingSlot> slots) {
            this.id = id;
            this.slots = slots;
        }

    ParkingSlot allocate(Vehicle v) {
        for (ParkingSlot slot : slots) {
            if (slot.canFit(v)) {
                slot.park(v);
                return slot;
            }
        }
        return null;
    }
}

// ================= TICKET =================
class Ticket {
    String id;
    Vehicle vehicle;
    ParkingSlot slot;
    long entryTime;
    //we can add the floor also if required or can required vehicle also, since slot contains vehicle

    Ticket(String id, Vehicle vehicle, ParkingSlot slot) {
        this.id = id;
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = System.currentTimeMillis();
    }
}

// ================= PARKING LOT =================
class ParkingLot {
    List<Floor> floors;
    Map<String, Ticket> active;

    ParkingLot(List<Floor> floors) {
        this.floors = floors;
        this.active = new HashMap<>();
    }

    public String park(Vehicle v) {
        for (Floor floor : floors) {
            ParkingSlot slot = floor.allocate(v);

            if (slot != null) {
                String id = v.number;
                Ticket ticket = new Ticket(id, v, slot);
                active.put(id, ticket);
                System.out.println("PARKED " + id + " FLOOR " + floor.id + " SLOT " + slot.id);
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
        double amount = duration * 20;
        ticket.slot.unpark();
        active.remove(vehicleNumber);

        System.out.println("UNPARKED " + vehicleNumber + " BILL Rs " + amount);
    }
}

// ================= MAIN =================
public class Main {
    public static void main(String[] args) throws InterruptedException {


        ArrayList<ParkingSlot> slots = new ArrayList<>(Arrays.asList(new ParkingSlot(1,VehicleSize.LARGE));
        ArrayList<Floor> floors = new ArrayList<>(Arrays.asList(new Floor("1", slots)));
        ParkingLot parkingLot = new ParkingLot(floors);

        //ParkingLot parkingLot = new ParkingLot(Arrays.asList(new ParkingFloor( 1, Arrays.asList(new ParkingSlot(1, VehicleType.SMALL)) )));

        Vehicle vehicle = new Vehicle("DL3SFA1080", VehicleType.SMALL);

        Ticket ticket = parkingLot.park(vehicle);

        parkingLot.unPark(ticket);
        /*
        List<Floor> floors = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            List<ParkingSlot> slots = new ArrayList<>();

            for (int j = 0; j < 5; j++) {
                slots.add(new ParkingSlot("S" + j, VehicleSize.LARGE));
            }

            floors.add(new Floor("F" + i, slots));
        }

        ParkingLot lot = new ParkingLot(floors);

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

         */
    }
}
