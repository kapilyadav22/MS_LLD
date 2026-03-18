#include <bits/stdc++.h>
using namespace std;

/* ---------- ENUMS ---------- */

enum class Direction {
    UP, DOWN, IDLE
};

enum class ElevatorState {
    IDLE, MOVING, DOOR_OPEN, OUT_OF_SERVICE
};

/* ---------- REQUEST ---------- */

class Request {
public:
    int sourceFloor;
    int destinationFloor;
    Direction direction;
    long long timestamp;

    Request(int source, int dest) {
        sourceFloor = source;
        destinationFloor = dest;
        direction = dest > source ? Direction::UP : Direction::DOWN;
        timestamp = time(nullptr);
    }
};

/* ---------- ELEVATOR ---------- */

class Elevator {
private:
    int id;

public:
    int currentFloor = 0;
    Direction direction = Direction::IDLE;
    ElevatorState state = ElevatorState::IDLE;

    queue<Request> requestQueue;

    Elevator(int id) : id(id) {}

    void addRequest(const Request& request) {
        requestQueue.push(request);
    }

    void processRequests() {

        while (!requestQueue.empty()) {

            Request request = requestQueue.front();
            requestQueue.pop();

            if (request.sourceFloor == request.destinationFloor)
                continue;

            moveToFloor(request.sourceFloor);
            openDoor();

            moveToFloor(request.destinationFloor);
            openDoor();
        }

        direction = Direction::IDLE;
        state = ElevatorState::IDLE;
    }

private:

    void moveToFloor(int targetFloor) {

        if (currentFloor == targetFloor)
            return;

        state = ElevatorState::MOVING;
        direction = targetFloor > currentFloor
                    ? Direction::UP
                    : Direction::DOWN;

        while (currentFloor != targetFloor) {

            currentFloor += (direction == Direction::UP ? 1 : -1);

            cout << "Elevator " << id
                 << " at floor "
                 << currentFloor << endl;
        }
    }

    void openDoor() {

        state = ElevatorState::DOOR_OPEN;

        cout << "Elevator " << id
             << " door opened at floor "
             << currentFloor << endl;

        state = ElevatorState::IDLE;
    }
};

/* ---------- ASSIGNMENT POLICY ---------- */

class ElevatorAssignmentPolicy {
public:
    virtual Elevator* selectElevator(
        vector<Elevator>& elevators,
        const Request& request
    ) = 0;

    virtual ~ElevatorAssignmentPolicy() = default;
};

/* ---------- DEFAULT POLICY ---------- */

class NearestElevatorPolicy : public ElevatorAssignmentPolicy {

public:

    Elevator* selectElevator(
        vector<Elevator>& elevators,
        const Request& request) override {

        Elevator* best = nullptr;
        int minDistance = INT_MAX;

        for (auto& elevator : elevators) {

            if (elevator.state == ElevatorState::OUT_OF_SERVICE)
                continue;

            int distance = abs(
                elevator.currentFloor - request.sourceFloor
            );

            if (distance < minDistance) {
                minDistance = distance;
                best = &elevator;
            }
        }

        return best;
    }
};

/* ---------- CONTROLLER ---------- */

class ElevatorController {

private:
    vector<Elevator> elevators;
    unique_ptr<ElevatorAssignmentPolicy> policy;

public:

    ElevatorController(int n,
        unique_ptr<ElevatorAssignmentPolicy> policy)
        : policy(move(policy))
    {
        for (int i = 0; i < n; i++) {
            elevators.emplace_back(i);
        }
    }

    void submitRequest(const Request& request) {

        Elevator* elevator =
            policy->selectElevator(elevators, request);

        if (!elevator) {
            cout << "No available elevator\n";
            return;
        }

        elevator->addRequest(request);
        elevator->processRequests();
    }
};

/* ---------- MAIN (CUSTOM INPUT) ---------- */

int main() {

    unique_ptr<ElevatorController> controller;

    string command;

    while (cin >> command) {

        if (command == "init") {
            int n;
            cin >> n;

            controller = make_unique<ElevatorController>(n,
                make_unique<NearestElevatorPolicy>()
            );
        }

        else if (command == "request") {
            int src, dest;
            cin >> src >> dest;

            if (!controller) {
                cout << "Initialize system first\n";
                continue;
            }

            controller->submitRequest(Request(src, dest));
        }

        else if (command == "exit") {
            break;
        }
    }

    return 0;
}

/*
INPUT:
    init 2
    request 0 5
    request 3 1
    request 2 8
    exit

OUTPUT:
    Elevator 0 door opened at floor 0
    Elevator 0 at floor 1
    Elevator 0 at floor 2
    Elevator 0 at floor 3
    Elevator 0 at floor 4
    Elevator 0 at floor 5
    Elevator 0 door opened at floor 5
    Elevator 0 at floor 4
    Elevator 0 at floor 3
    Elevator 0 door opened at floor 3
    Elevator 0 at floor 2
    Elevator 0 at floor 1
    Elevator 0 door opened at floor 1
    Elevator 0 at floor 2
    Elevator 0 door opened at floor 2
    Elevator 0 at floor 3
    Elevator 0 at floor 4
    Elevator 0 at floor 5
    Elevator 0 at floor 6
    Elevator 0 at floor 7
    Elevator 0 at floor 8
    Elevator 0 door opened at floor 8

*/