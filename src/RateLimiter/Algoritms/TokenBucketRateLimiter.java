package RateLimiter.Algoritms;

import RateLimiter.RateLimitAlgorithm;

import java.util.HashMap;
import java.util.Map;


//Token Bucket
public class TokenBucketRateLimiter implements RateLimitAlgorithm {

    private static class Bucket {
        int tokens;
        long lastRefillTime;

        Bucket(int tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }

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

        Bucket bucket = buckets.get(key);
        if (bucket == null) {
            bucket = new Bucket(capacity, now);
            buckets.put(key, bucket);
        }

        refill(bucket, now);

        if (bucket.tokens > 0) {
            bucket.tokens--;
            return true;
        }

        return false;
    }

    private void refill(Bucket bucket, long now) {
        long elapsedSeconds =
                (now - bucket.lastRefillTime) / 1000;

        if (elapsedSeconds > 0) {
            int refillTokens =
                    (int) (elapsedSeconds * refillRatePerSecond);

            bucket.tokens =
                    Math.min(capacity, bucket.tokens + refillTokens);

            bucket.lastRefillTime = now;
        }
    }
}


