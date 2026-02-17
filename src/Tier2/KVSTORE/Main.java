package Tier2.DistributedLock.KVSTORE;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Interview-ready In-Memory Key Value Store
 * - Thread-safe
 * - O(1) average operations
 * - Easily extensible (TTL, eviction later)
 */
public class Main {

    /* ---------- Interface ---------- */
    interface KeyValueStore<K, V> {
        void put(K key, V value);
        V get(K key);
        void delete(K key);
    }

    /* ---------- Implementation ---------- */
    static class InMemoryKeyValueStore<K, V> implements KeyValueStore<K, V> {

        private final ConcurrentHashMap<K, V> store;

        public InMemoryKeyValueStore() {
            this.store = new ConcurrentHashMap<>();
        }

        @Override
        public void put(K key, V value) {
            store.put(key, value);
        }

        @Override
        public V get(K key) {
            return store.get(key);
        }

        @Override
        public void delete(K key) {
            store.remove(key);
        }
    }

    /* ---------- Driver Code ---------- */
    public static void main(String[] args) {

        KeyValueStore<String, String> kvStore = new InMemoryKeyValueStore<>();

        kvStore.put("name", "Kapil");
        kvStore.put("company", "Microsoft");

        System.out.println(kvStore.get("name"));      // Kapil
        System.out.println(kvStore.get("company"));   // Microsoft

        kvStore.delete("name");
        System.out.println(kvStore.get("name"));      // null
    }
}
