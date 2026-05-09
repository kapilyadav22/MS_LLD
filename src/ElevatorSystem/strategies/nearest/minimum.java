import java.util.*;

enum Direction {
    UP, DOWN, IDLE
}

class Request {
    int src, dest;
    Direction dir;
    Request(int src, int dest) {
        this.src = src;
        this.dest = dest;
        this.dir = src == dest ? Direction.IDLE : (dest > src ? Direction.UP : Direction.DOWN);
    }
}

class Elevator {
    int id, floor = 0;
    Direction dir = Direction.IDLE;
    TreeSet<Integer> upStops = new TreeSet<>(), downStops = new TreeSet<>(Collections.reverseOrder());

    Elevator(int id) {
        this.id = id;
    }

    void addRequest(Request r) {
        if (r.dir == Direction.IDLE) { // same-floor: just open doors
            floor = r.src;
            System.out.println("Elevator-" + id + " doors open at " + floor);
            return;
        }

        if (r.dir == Direction.UP) {
            upStops.add(r.src);
            upStops.add(r.dest);
        } else {
            downStops.add(r.src);
            downStops.add(r.dest);
        }

        if (dir == Direction.IDLE) dir = r.dir;
    }

    void process() {
        while (!upStops.isEmpty() || !downStops.isEmpty()) {
            if (dir == Direction.UP) {
                serve(upStops);
                if (!downStops.isEmpty())
                    dir = Direction.DOWN;
            }
            if (dir == Direction.DOWN) {
                serve(downStops);
                if (!upStops.isEmpty())
                    dir = Direction.UP;
            }
        }
        dir = Direction.IDLE;
    }

    private void serve(TreeSet<Integer> stops) {
        while (!stops.isEmpty()) {
            floor = stops.pollFirst();
            System.out.println("Elevator-" + id + " stopped at " + floor);
        }
    }

    int pending() {
        return upStops.size() + downStops.size();
    }
}

// Strategy pattern — pluggable assignment policy
interface AssignmentPolicy {
    Elevator select(List<Elevator> elevators, Request req);
}

class NearestPolicy implements AssignmentPolicy {
    public Elevator select(List<Elevator> elevators, Request req) {
        Elevator best = null;
        int bestDist = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            int d = Math.abs(e.floor - req.src);

            if (e.dir == Direction.IDLE && d < bestDist) {
                best = e;
                bestDist = d;
            }
            else if (e.dir == req.dir) {
                boolean onPath;
                if (req.dir == Direction.UP) {
                    onPath = e.floor <= req.src;
                } else {
                    onPath = e.floor >= req.src;
                }

                if (onPath && d < bestDist) {
                    best = e;
                    bestDist = d;
                }
            }
        }

        if(best!=null) return  best;

        Elevator leastBusy = elevators.get(0);
        for (Elevator e : elevators) {
            if (e.pending() < leastBusy.pending()) {
                leastBusy = e;
            }
        }
        return leastBusy;

       // return best != null ? best : Collections.min(elevators, Comparator.comparingInt(Elevator::pending));
    }
}

class ElevatorController {
    List<Elevator> elevators = new ArrayList<>();
    AssignmentPolicy policy;

    ElevatorController(int n, AssignmentPolicy policy) {
        this.policy = policy;
        for (int i = 1; i <= n; i++)
            elevators.add(new Elevator(i));
    }

    void submit(Request r) {
        Elevator elevator = policy.select(elevators, r);
        elevator.addRequest(r);
    }

    void processAll() {
        elevators.forEach(elevator -> elevator.process());
    }
}

public class ElevatorSystem {
    public static void main(String[] args) {
        ElevatorController ctrl = new ElevatorController(2, new NearestPolicy());
        ctrl.submit(new Request(0, 5));
        ctrl.submit(new Request(3, 0));
        ctrl.submit(new Request(7, 2));
        ctrl.submit(new Request(1, 9));
        ctrl.submit(new Request(2, 6));
        ctrl.processAll();
    }
}
