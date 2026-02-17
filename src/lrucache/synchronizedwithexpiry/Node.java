package lrucache.synchronizedwithexpiry;

public class Node<K,V> {
    K key;
    V value;
    long expiryTime;
    Node<K,V> prev;
    Node<K,V> next;

    Node(){
//        key = value = -1;
        next = prev = null;
        expiryTime = Long.MAX_VALUE;
    }

    Node(K key, V value, long expiryTime) {
        this.key = key;
        this.value = value;
        this.expiryTime = expiryTime;
    }
}
