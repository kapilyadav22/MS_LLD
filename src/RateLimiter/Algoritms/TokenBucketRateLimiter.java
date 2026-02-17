package RateLimiter.Algoritms;

import RateLimiter.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


class Bucket {

    private int tokens;
    private long lastRefillTime;

    Bucket(int capacity, long now) {
        this.tokens = capacity;
        this.lastRefillTime = now;
    }

    boolean hasTokens() {
        return tokens > 0;
    }

    void consume() {
        if (tokens > 0) {
            tokens--;
        }
    }

    void refill(int capacity, int refillRatePerSecond, long now) {
        long elapsedSeconds = (now - lastRefillTime) / 1000;

        if (elapsedSeconds <= 0) return;

        int refillTokens = (int) (elapsedSeconds * refillRatePerSecond);
        tokens = Math.min(capacity, tokens + refillTokens);
        lastRefillTime = now;
    }
}


public class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final int refillRatePerSecond;

    private final Map<String, Bucket> buckets;
    private final Map<String, ReentrantLock> locks;

    public TokenBucketRateLimiter(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        buckets = new ConcurrentHashMap<>();
        locks = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allow(String key) {
        ReentrantLock lock = locks.computeIfAbsent( key,k -> new ReentrantLock());

        lock.lock();
        try {
            long now = System.currentTimeMillis();
            Bucket bucket = buckets.computeIfAbsent(key,k -> new Bucket(capacity, now));
            bucket.refill(capacity, refillRatePerSecond, now);

            if (bucket.hasTokens()) {
                bucket.consume();
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }
}



/*
public class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final int refillRatePerSecond;

    private final Map<String, Bucket> buckets = new HashMap<>();

    public TokenBucketRateLimiter(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    @Override
    public synchronized boolean allow(String key) {
        long now = System.currentTimeMillis();

        Bucket bucket = buckets.computeIfAbsent(key,k -> new Bucket(capacity, now));
        //  Lazy refill
        bucket.refill(capacity, refillRatePerSecond, now);

        if (bucket.hasTokens()) {
            bucket.consume();
            return true;
        }

        return false;
    }
}
*/