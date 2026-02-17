package Tier2.KVSTORE.withLRU;

import java.util.concurrent.ConcurrentHashMap;

interface  DBStore<K, V>  {
    V read(K key);
    void write(K key, V value);
    void delete(K key);
}

public class Database<K,V> implements DBStore<K, V> {
    private final ConcurrentHashMap<K, V> db = new ConcurrentHashMap<>();

    @Override
    public V read(K key) {
        return db.get(key);
    }

    @Override
    public void write(K key, V value) {
        db.put(key, value);
    }

    public void delete(K key) {
        db.remove(key);
    }
}
