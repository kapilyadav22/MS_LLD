package lrucache.synchronizedwithexpiry;

import lrucache.SimpleLRUCache;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        /* LRU Cache with ttl and thread safety
        LRUCache cache = new LRUCache(2);

        cache.put(1, 100, 3000);
        cache.put(2, 200, 3000);

        System.out.println(cache.get(1)); // 100

        cache.put(3, 300, 3000); // evicts key 2

        System.out.println(cache.get(2)); // -1
        System.out.println(cache.get(3)); // 300


        Thread.sleep(3500);

        System.out.println(cache.get(1)); // -1 (expired)
        System.out.println(cache.get(3)); // -1 (expired)
        */

        //Simple LRUCache
        SimpleLRUCache simpleCache = new SimpleLRUCache(2);
        simpleCache.put(1, 100);
        simpleCache.put(2, 200);

        System.out.println(simpleCache.get(1)); // 100

        simpleCache.put(3, 300); // evicts key 2

        System.out.println(simpleCache.get(2));
        System.out.println(simpleCache.get(3));

    }
}





