package Tier2.DistributedLock.KVSTORE.withLRU;

public interface KeyValueStore<K, V> {
    void put(K key, V value, long ttlMillis);
    V get(K key);
    void delete(K key);
}
