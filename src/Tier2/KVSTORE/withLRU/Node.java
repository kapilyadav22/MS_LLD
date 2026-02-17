package Tier2.KVSTORE.withLRU;

public class Node<k,v> {
    k key;
    v value;
    long expiryTime;
    Node<k,v> prev;
    Node <k,v> next;

    Node(){
        next = prev = null;
        expiryTime = Long.MAX_VALUE;
    }

    Node(k key, v value, long expiryTime) {
        this.key = key;
        this.value = value;
        this.expiryTime = expiryTime;
    }
}
