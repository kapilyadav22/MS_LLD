package Tier2.DistributedLock.KVSTORE.withLRU;

import Tier2.DistributedLock.KVSTORE.withLRU.InMemoryKeyValueStore;
import Tier2.DistributedLock.KVSTORE.withLRU.KeyValueStore;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        KeyValueStore<String, String> store =
                new InMemoryKeyValueStore<>(2);

        store.put("A", "Apple", 3000);
        store.put("B", "Banana", 5000);

        System.out.println(store.get("A")); // Apple

        Thread.sleep(3500);

        System.out.println(store.get("A")); // null (expired)

        store.put("C", "Cherry", 5000); // evicts B

        System.out.println(store.get("B")); // null
        System.out.println(store.get("C")); // Cherry
    }
}
