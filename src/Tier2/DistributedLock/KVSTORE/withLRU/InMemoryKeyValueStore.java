package Tier2.DistributedLock.KVSTORE.withLRU;

public class InMemoryKeyValueStore<K, V> implements KeyValueStore<K, V> {

    private final LRUCache<K, V> cache;

    public InMemoryKeyValueStore(int capacity) {
        this.cache = new LRUCache<>(capacity);
    }

    @Override
    public void put(K key, V value, long ttlMillis) {
        cache.put(key, value, ttlMillis);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void delete(K key) {
        cache.delete(key);
    }
}
