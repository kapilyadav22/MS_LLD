package lrucache;

/*
1. Clarify Requirements (say this first)
    In-memory cache
    Fixed capacity
    get(key) and put(key, value)
    O(1) operations
    Evict Least Recently Used
    Single process (not distributed)

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
