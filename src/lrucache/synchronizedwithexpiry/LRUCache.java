package lrucache.synchronizedwithexpiry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache {

    private final int capacity;
    private final Map<Integer, Node> map;
    private final ReentrantLock lock = new ReentrantLock();

    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();

        head = new Node();

        tail = new Node();

        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        lock.lock();
        try {
            Node node = map.get(key);
            if (node == null) {
                return -1;
            }

            if (isExpired(node)) {
                removeNode(node);
                map.remove(key);
                return -1;
            }

            moveToFront(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(int key, int value, long ttlMillis) {
        lock.lock();
        try {
            if (capacity == 0) return;

            long expiryTime = System.currentTimeMillis() + ttlMillis;
            Node node = map.get(key);

            if (node != null) {
                node.value = value;
                node.expiryTime = expiryTime;
                moveToFront(node);
                return;
            }

            if (map.size() == capacity) {
                evictLRU();
            }

            Node newNode = new Node(key, value, expiryTime);
            addAfterHead(newNode);
            map.put(key, newNode);
        } finally {
            lock.unlock();
        }
    }

    private boolean isExpired(Node node) {
        return System.currentTimeMillis() > node.expiryTime;
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addAfterHead(node);
    }

    private void addAfterHead(Node node) {
        Node first = head.next;

        head.next = node;
        node.prev = head;

        node.next = first;
        first.prev = node;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        node.prev = null;
        node.next = null;
    }

    private void evictLRU() {
        Node lru = tail.prev;
        if (lru == head) return;

        map.remove(lru.key);
        removeNode(lru);
    }
}
