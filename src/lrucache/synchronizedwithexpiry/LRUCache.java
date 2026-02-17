package lrucache.synchronizedwithexpiry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


public class LRUCache<K,V> {

    private final int capacity;
    private final Map<K, Node<K,V>> map;
    private final ReentrantLock lock = new ReentrantLock();

    private final Node<K,V> head;
    private final Node<K,V> tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();

        head = new Node<K,V>();

        tail = new Node<K,V>();

        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        lock.lock();
        try {
            Node<K,V> node = map.get(key);
            if (node == null) {
                return null;
            }

            if (isExpired(node)) {
                removeNode(node);
                map.remove(key);
                return null;
            }

            moveToFront(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value, long ttlMillis) {
        lock.lock();
        try {
            if (capacity == 0) return;

            long expiryTime = System.currentTimeMillis() + ttlMillis;
            Node<K,V> node = map.get(key);

            if (node != null) {
                node.value = value;
                node.expiryTime = expiryTime;
                moveToFront(node);
                return;
            }

            if (map.size() == capacity) {
                evictLRU();
            }

            Node<K,V> newNode = new Node<K,V>(key, value, expiryTime);
            addAfterHead(newNode);
            map.put(key, newNode);
        } finally {
            lock.unlock();
        }
    }

    private boolean isExpired(Node<K,V> node) {
        return System.currentTimeMillis() > node.expiryTime;
    }

    private void moveToFront(Node<K,V> node) {
        removeNode(node);
        addAfterHead(node);
    }

    private void addAfterHead(Node<K,V> node) {
        Node<K,V> first = head.next;

        head.next = node;
        node.prev = head;

        node.next = first;
        first.prev = node;
    }

    private void removeNode(Node<K,V> node) {
        Node<K,V> prevNode = node.prev;
        Node<K,V> nextNode = node.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;

        node.prev = null;
        node.next = null;
    }

    private void evictLRU() {
        Node<K,V> lru = tail.prev;
        if (lru == head) return;

        map.remove(lru.key);
        removeNode(lru);
    }
}
