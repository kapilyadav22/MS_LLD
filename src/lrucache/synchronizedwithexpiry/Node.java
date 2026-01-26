package lrucache.synchronizedwithexpiry;

public class Node {
    int key;
    int value;
    long expiryTime;
    Node prev;
    Node next;

    Node(){
        key = value = -1;
        next = prev = null;
        expiryTime = Long.MAX_VALUE;
    }

    Node(int key, int value, long expiryTime) {
        this.key = key;
        this.value = value;
        this.expiryTime = expiryTime;
    }
}
