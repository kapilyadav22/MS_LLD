import java.util.*;

enum Direction { UP, DOWN, IDLE }
enum ElevatorState { MOVING, STOPPED, IDLE }

class Request {
    private final int sourceFloor;
    private final int destinationFloor;
    private final Direction direction;
    private final long timestamp;

    public Request(int sourceFloor, int destinationFloor) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
        this.direction = destinationFloor > sourceFloor ? Direction.UP : Direction.DOWN;
        this.timestamp = System.currentTimeMillis();
    }

    public int getSourceFloor()      { return sourceFloor; }
    public int getDestinationFloor() { return destinationFloor; }
    public Direction getDirection()  { return direction; }

    @Override
    public String toString() {
        return "Request(" + sourceFloor + " -> " + destinationFloor + " " + direction + ")";
    }
}

class Elevator {
    private final int id;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private final TreeSet<Integer> upRequests;
    private final TreeSet<Integer> downRequests;

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.IDLE;
        this.upRequests = new TreeSet<>();
        this.downRequests = new TreeSet<>(Collections.reverseOrder());
    }

    public int getId()              { return id; }
    public int getCurrentFloor()    { return currentFloor; }
    public Direction getDirection()  { return direction; }
    public ElevatorState getState() { return state; }

    public int getPendingRequestCount() {
        return upRequests.size() + downRequests.size();
    }

    /**
     * Adds both source and destination floors into the correct request set
     * based on the request direction and the elevator's current state.
     */
    public void addRequest(Request request) {
        int src = request.getSourceFloor();
        int dest = request.getDestinationFloor();

        if (request.getDirection() == Direction.UP) {
            upRequests.add(src);
            upRequests.add(dest);
        } else {
            downRequests.add(src);
            downRequests.add(dest);
        }

        // If elevator is idle, start moving in the request's direction
        if (state == ElevatorState.IDLE) {
            direction = request.getDirection();
            state = ElevatorState.MOVING;
        }

        System.out.println("  Elevator " + id + " accepted " + request);
    }

    /**
     * SCAN (elevator) algorithm:
     *  1. Serve all floors in the current direction.
     *  2. When done, switch direction if the other set is non-empty.
     *  3. If both sets are empty, go IDLE.
     */
    public void processRequests() {
        while (!upRequests.isEmpty() || !downRequests.isEmpty()) {

            if (direction == Direction.UP || direction == Direction.IDLE) {
                processUp();
                if (!downRequests.isEmpty()) {
                    direction = Direction.DOWN;
                    processDown();
                }
            } else {
                processDown();
                if (!upRequests.isEmpty()) {
                    direction = Direction.UP;
                    processUp();
                }
            }
        }
        state = ElevatorState.IDLE;
        direction = Direction.IDLE;
        System.out.println("  Elevator " + id + " is now IDLE at floor " + currentFloor);
    }

    private void processUp() {
        while (!upRequests.isEmpty()) {
            int nextFloor = upRequests.pollFirst();
            moveToFloor(nextFloor);
            openDoor();
        }
    }

    private void processDown() {
        while (!downRequests.isEmpty()) {
            int nextFloor = downRequests.pollFirst();
            moveToFloor(nextFloor);
            openDoor();
        }
    }

    protected void moveToFloor(int floor) {
        System.out.println("  Elevator " + id + " moving " + direction  + " from " + currentFloor + " to " + floor);
        currentFloor = floor;
        state = ElevatorState.MOVING;
    }

    protected void openDoor() {
        state = ElevatorState.STOPPED;
        System.out.println("  Elevator " + id + " stopped at floor " + currentFloor + " — doors open");
    }

    @Override
    public String toString() {
        return "Elevator " + id + "(floor " + currentFloor + ", dir " + direction + ", state " + state + ")";
    }
}

interface ElevatorAssignmentPolicy {
    Elevator selectElevator(List<Elevator> elevators, Request request);
}

/**
 * Picks the nearest elevator that is either:
 *  1. IDLE, or
 *  2. Moving TOWARD the source floor in the SAME direction as the request.
 * Falls back to the elevator with the fewest pending requests.
 */
class NearestElevatorPolicy implements ElevatorAssignmentPolicy {

    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        Elevator best = null;
        int bestDist = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            int dist = Math.abs(e.getCurrentFloor() - request.getSourceFloor());

            // Case 1: elevator is idle — always a candidate
            if (e.getDirection() == Direction.IDLE) {
                if (dist < bestDist) { bestDist = dist; best = e; }
                continue;
            }

            // Case 2: elevator is moving in SAME direction as request
            //         AND hasn't passed the source floor yet
            if (e.getDirection() == request.getDirection()) {
                boolean canPickUp =
                        (request.getDirection() == Direction.UP   && e.getCurrentFloor() <= request.getSourceFloor()) ||
                                (request.getDirection() == Direction.DOWN  && e.getCurrentFloor() >= request.getSourceFloor());
                if (canPickUp && dist < bestDist) { bestDist = dist; best = e; }
            }
        }

        // Fallback: pick the elevator with the least load
        if (best == null) {
            best = Collections.min(elevators, Comparator.comparingInt(Elevator::getPendingRequestCount));
        }
        return best;
    }
}

class ElevatorController {
    private final List<Elevator> elevators;
    private final ElevatorAssignmentPolicy policy;

    public ElevatorController(int numElevators, ElevatorAssignmentPolicy policy) {
        this.policy = policy;
        this.elevators = new ArrayList<>();
        for (int i = 1; i <= numElevators; i++) {
            elevators.add(new Elevator(i));
        }
    }

    public void submitRequest(Request request) {
        System.out.println("\n  New " + request);
        Elevator chosen = policy.selectElevator(elevators, request);
        chosen.addRequest(request);
    }

    public void processAll() {
        System.out.println("\n Processing all elevators");
        for (Elevator e : elevators) {
            e.processRequests();
        }
    }

    public void status() {
        System.out.println("\n Elevator Status");
        for (Elevator e : elevators) {
            System.out.println("  " + e);
        }
    }
}

public class ElevatorSystem {
    public static void main(String[] args) {

        ElevatorAssignmentPolicy policy = new NearestElevatorPolicy();
        ElevatorController controller = new ElevatorController(3, policy);

        // Edge-case 1: same floor request (press button, doors just open)
        controller.submitRequest(new Request(0, 5));   // ground → 5  (UP)
        controller.submitRequest(new Request(3, 0));   // 3 → ground  (DOWN)
        controller.submitRequest(new Request(7, 2));   // 7 → 2       (DOWN)
        controller.submitRequest(new Request(5, 5));   // same floor   (edge)
        controller.submitRequest(new Request(1, 9));   // 1 → 9       (UP, long)

        controller.status();
        controller.processAll();
        controller.status();
    }
}
