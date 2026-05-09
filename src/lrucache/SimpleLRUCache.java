package lrucache;

/*
1. Clarify Requirements (say this first)
    In-memory cache
    Fixed capacity
    get(key) and put(key, value),

    O(1) operations
    Eviction Policy :  Least Recently Used
    Generic types?
    Thread-safe?
    TTL needed?
    Persistence needed?
    Single process (not distributed)

    follow up :
        remove(K key)
        Thread-safe?
        TTL needed?
        Persistence needed?
 */


import java.util.HashMap;
import java.util.Map;

class Node1 {
    int key;
    int value;
    Node1 prev;
    Node1 next;

    Node1() {
        key = value = -1;
        next = prev = null;
    }

    Node1(int key, int value) {
        this.key = key;
        this.value = value;
    }
}


public class SimpleLRUCache {

    private final int capacity;
    private final Map<Integer, Node1> map;

    private final Node1 head;
    private final Node1 tail;

    public SimpleLRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();

        head = new Node1();
        tail = new Node1();

        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node1 Node1 = map.get(key);
        if (Node1 == null) {
            return -1;
        }

        moveToFront(Node1);
        return Node1.value;
    }

    // to ensure thread safety, use synchronized keyword in method,
    // but better to use reentrantlock
    //   public synchronized int get(int key) { ... }
    //   public synchronized void put(int key, int value) { ... }

    public void put(int key, int value) {
        Node1 Node1 = map.get(key);

        if (Node1 != null) {
            Node1.value = value;
            moveToFront(Node1);
            return;
        }

        if (map.size() == capacity) {
            removeTail();
        }
        Node1 newNode1 = new Node1(key, value);
        insertAfterHead(newNode1);
        map.put(key, newNode1);
    }

    private void  moveToFront(Node1 Node1) {
        removeNode1(Node1);
        insertAfterHead(Node1);
    }

    private void insertAfterHead(Node1 node) {

        Node1 nextNode = head.next;
        head.next = node;
        nextNode.prev = node;
        node.prev = head;
        node.next = nextNode;
    }

    private void removeNode1(Node1 node1) {
        Node1 prevNode = node1.prev;
        Node1 nextNode = node1.next;

        // Remove pointers to node
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    private void removeTail() {
        Node1 node = tail.prev;
        map.remove(node.key);
        removeNode1(node);
    }
}

public class Main {
    public static void main(String[] args) {

        SimpleLRUCache cache = new SimpleLRUCache(2);

        cache.put(1, 10);
        cache.put(2, 20);

        System.out.println(cache.get(1)); // 10

        cache.put(3, 30); // evicts key 2

        System.out.println(cache.get(2)); // -1

        cache.put(4, 40); // evicts key 1

        System.out.println(cache.get(1)); // -1
        System.out.println(cache.get(3)); // 30
        System.out.println(cache.get(4)); // 40
    }
}


/*
import java.util.*;

public class MainUserInput {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int capacity = sc.nextInt();
        int q = sc.nextInt();

        SimpleLRUCache cache = new SimpleLRUCache(capacity);

        while (q-- > 0) {
            String op = sc.next();

            if (op.equals("put")) {
                int key = sc.nextInt();
                int value = sc.nextInt();
                cache.put(key, value);
            } else if (op.equals("get")) {
                int key = sc.nextInt();
                System.out.println(cache.get(key));
            }
        }

        sc.close();
    }
}
 */