#include <bits/stdc++.h>
using namespace std;

enum class Direction {
    UP,
    DOWN,
    IDLE
};

enum class ElevatorState {
    IDLE,
    MOVING,
    DOOR_OPEN,
    OUT_OF_SERVICE
};

class Request {
public:
    int sourceFloor;
    int destinationFloor;
    Direction direction;
    long timestamp;

    Request(int source, int dest) {
        sourceFloor = source;
        destinationFloor = dest;
        direction = dest > source ? Direction::UP : Direction::DOWN;
        timestamp = time(nullptr);
    }
};

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

            if (direction == Direction::UP)
                currentFloor++;
            else
                currentFloor--;

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

class ElevatorAssignmentPolicy {
public:
    virtual shared_ptr<Elevator> selectElevator(
        vector<shared_ptr<Elevator>>& elevators,
        const Request& request
    ) = 0;

    virtual ~ElevatorAssignmentPolicy() = default;
};

class NearestElevatorPolicy : public ElevatorAssignmentPolicy {

public:

    shared_ptr<Elevator> selectElevator(
        vector<shared_ptr<Elevator>>& elevators,
        const Request& request) override {

        shared_ptr<Elevator> best = nullptr;
        int minDistance = INT_MAX;

        for (auto& elevator : elevators) {

            if (elevator->state == ElevatorState::OUT_OF_SERVICE)
                continue;

            int distance = abs(
                elevator->currentFloor -
                request.sourceFloor
            );

            if (distance < minDistance) {

                minDistance = distance;
                best = elevator;
            }
        }

        return best;
    }
};

class ElevatorController {

private:
    vector<shared_ptr<Elevator>> elevators;
    unique_ptr<ElevatorAssignmentPolicy> assignmentPolicy;

public:

    ElevatorController(
        int numberOfElevators,
        unique_ptr<ElevatorAssignmentPolicy> policy)
    {
        assignmentPolicy = move(policy);

        for (int i = 0; i < numberOfElevators; i++) {
            elevators.push_back(make_shared<Elevator>(i));
        }
    }

    void submitRequest(const Request& request) {

        auto elevator =
            assignmentPolicy->selectElevator(
                elevators,
                request
            );

        if (!elevator) {
            cout << "No available elevator\n";
            return;
        }

        elevator->addRequest(request);
        elevator->processRequests();
    }
};

int main() {

    unique_ptr<ElevatorAssignmentPolicy> policy =
        make_unique<NearestElevatorPolicy>();

    ElevatorController controller(2, move(policy));

    controller.submitRequest(Request(0,5));
    controller.submitRequest(Request(3,1));
    controller.submitRequest(Request(2,8));

    return 0;
}