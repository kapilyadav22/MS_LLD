package RateLimiter.Algoritms;

import RateLimiter.RateLimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


//Leaky Bucket
/*
   Idea:
    Requests enter bucket2
    Bucket leaks at fixed rate
    Burst smoothing
 */


class LeakyBucket {

    private int water;
    private long lastLeakTime;

    LeakyBucket(long now) {
        this.water = 0;
        this.lastLeakTime = now;
    }

    void leak(int leakRatePerSecond, long now) {
        long elapsedSeconds = (now - lastLeakTime) / 1000;

        if (elapsedSeconds <= 0) return;

        int leaked = (int) (elapsedSeconds * leakRatePerSecond);
        water = Math.max(0, water - leaked);
        lastLeakTime = now;
    }

    boolean canAccept(int capacity) {
        return water < capacity;
    }

    void addDrop() {
        water++;
    }
}

public class LeakyBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final int leakRatePerSecond;

    private final Map<String, LeakyBucket> buckets = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public LeakyBucketRateLimiter(int capacity, int leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
    }

    @Override
    public boolean allow(String key) {
        ReentrantLock lock = locks.computeIfAbsent(key,k -> new ReentrantLock());

        lock.lock();
        try {
            long now = System.currentTimeMillis();
            LeakyBucket bucket = buckets.computeIfAbsent(key,k -> new LeakyBucket(now));
            bucket.leak(leakRatePerSecond, now);

            if (bucket.canAccept(capacity)) {
                bucket.addDrop();
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }
}


