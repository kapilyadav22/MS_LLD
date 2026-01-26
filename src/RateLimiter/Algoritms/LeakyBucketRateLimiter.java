package RateLimiter.Algoritms;

import RateLimiter.RateLimitAlgorithm;

import java.util.HashMap;
import java.util.Map;


//Leaky Bucket
/*
   Idea:
    Requests enter bucket
    Bucket leaks at fixed rate
    Burst smoothing
 */
public class LeakyBucketRateLimiter implements RateLimitAlgorithm {

    private static class Bucket {
        int water;
        long lastLeakTime;

        Bucket(long now) {
            this.water = 0;
            this.lastLeakTime = now;
        }
    }

    private final int capacity;
    private final int leakRatePerSecond;

    private final Map<String, Bucket> buckets = new HashMap<>();

    public LeakyBucketRateLimiter(int capacity, int leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
    }

    @Override
    public synchronized boolean allow(String key) {
        long now = System.currentTimeMillis();

        Bucket bucket = buckets.get(key);
        if (bucket == null) {
            bucket = new Bucket(now);
            buckets.put(key, bucket);
        }

        leak(bucket, now);

        if (bucket.water < capacity) {
            bucket.water++;
            return true;
        }

        return false;
    }

    private void leak(Bucket bucket, long now) {
        long elapsedSeconds =
                (now - bucket.lastLeakTime) / 1000;

        if (elapsedSeconds > 0) {
            int leaked =
                    (int) (elapsedSeconds * leakRatePerSecond);

            bucket.water = Math.max(0, bucket.water - leaked);
            bucket.lastLeakTime = now;
        }
    }
}
