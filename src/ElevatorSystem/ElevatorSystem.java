package ElevatorSystem;

import java.util.*;

enum Direction {
    UP, DOWN, IDLE
}

enum ElevatorState {
    IDLE, MOVING, DOOR_OPEN, OUT_OF_SERVICE
}

class Request {
    final int sourceFloor;
    final int destinationFloor;
    final Direction direction;
    final long timestamp;

    Request(int sourceFloor, int destinationFloor) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
        this.direction = destinationFloor > sourceFloor
                ? Direction.UP
                : Direction.DOWN;
        this.timestamp = System.currentTimeMillis();
    }
}

class Elevator {
    final int id;
    protected int currentFloor = 0;
    protected Direction direction = Direction.IDLE;
    protected ElevatorState state = ElevatorState.IDLE;

    protected Queue<Request> requestQueue = new LinkedList<>();

    Elevator(int id) {
        this.id = id;
    }

    synchronized void addRequest(Request request) {
        requestQueue.offer(request);
    }

    void processRequests() {
        while (!requestQueue.isEmpty()) {
            Request request = requestQueue.poll();

            moveToFloor(request.sourceFloor);
            openDoor();

            moveToFloor(request.destinationFloor);
            openDoor();
        }

        direction = Direction.IDLE;
        state = ElevatorState.IDLE;
    }

    protected void moveToFloor(int targetFloor) {
        if (currentFloor == targetFloor) return;

        state = ElevatorState.MOVING;
        direction = targetFloor > currentFloor ? Direction.UP : Direction.DOWN;

        while (currentFloor != targetFloor) {
            currentFloor += direction == Direction.UP ? 1 : -1;
            System.out.println("Elevator " + id + " at floor " + currentFloor);
        }
    }

    protected void openDoor() {
        state = ElevatorState.DOOR_OPEN;
        System.out.println("Elevator " + id + " door opened at floor " + currentFloor);
        state = ElevatorState.IDLE;
    }
}

interface ElevatorAssignmentPolicy {
    Elevator selectElevator(List<Elevator> elevators, Request request);
}

class NearestElevatorPolicy implements ElevatorAssignmentPolicy {

    @Override
    public Elevator selectElevator(List<Elevator> elevators, Request request) {
        Elevator best = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.state == ElevatorState.OUT_OF_SERVICE) continue;

            int distance = Math.abs(elevator.currentFloor - request.sourceFloor);
            if (distance < minDistance) {
                minDistance = distance;
                best = elevator;
            }
        }
        return best;
    }
}


class ElevatorController {
    private final List<Elevator> elevators;
    private final ElevatorAssignmentPolicy assignmentPolicy;

    ElevatorController(int numberOfElevators,
                       ElevatorAssignmentPolicy assignmentPolicy) {
        this.assignmentPolicy = assignmentPolicy;
        this.elevators = new ArrayList<>();

        for (int i = 0; i < numberOfElevators; i++) {
            elevators.add(new Elevator(i));
        }
    }

    void submitRequest(Request request) {
        Elevator elevator = assignmentPolicy.selectElevator(elevators, request);

        if (elevator == null) {
            System.out.println("No available elevator for request");
            return;
        }

        elevator.addRequest(request);
        elevator.processRequests();
    }
}

public class ElevatorSystem {
    public static void main(String[] args) {

        ElevatorAssignmentPolicy policy = new NearestElevatorPolicy();
        ElevatorController controller = new ElevatorController(2, policy);

        controller.submitRequest(new Request(0, 5));
        controller.submitRequest(new Request(3, 1));
        controller.submitRequest(new Request(2, 8));
    }
}
