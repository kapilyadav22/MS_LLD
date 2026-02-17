package Tier2.KVSTORE.withLRU;

interface CacheStore<K, V> {
    V get(K key);
    void put(K key, V value, long ttlMillis);
    void delete(K key);
}


public class Cache<K, V> implements CacheStore<K, V> {

    private final EvictionPolicy<K, V> cache;
    private final Database<K,V> database;
    private final long defaultTTL;

    public Cache(EvictionPolicy<K, V> cache,long defaultTTL) {
        this.cache = cache;
        this.database = new Database<K, V>();
        this.defaultTTL = defaultTTL;
    }

    // PUT (WRITE-THROUGH PUT)
    @Override
    public void put(K key, V value, long ttlMillis) {
        database.write(key, value);
        cache.put(key, value, ttlMillis);
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            return value;
        }

        // 2️⃣ Cache miss → read from DB
        value = database.read(key);
        if (value == null) {
            return null;
        }

        // 3️⃣ Populate cache
        cache.put(key, value, defaultTTL);
        return value;
    }

    // DELETE (WRITE-THROUGH DELETE)
    @Override
    public void delete(K key) {
        database.delete(key);
        cache.delete(key);
    }
}
