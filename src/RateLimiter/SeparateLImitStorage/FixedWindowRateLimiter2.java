package RateLimiter.SeparateLImitStorage;


import RateLimiter.RateLimitAlgorithm;

public class FixedWindowRateLimiter2 implements RateLimitAlgorithm {

    private final int limit;
    private final long windowSizeMillis;
    private final RateLimitStore store;

    public FixedWindowRateLimiter2(
            int limit,
            long windowSizeMillis,
            RateLimitStore store
    ) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
        this.store = store;
    }

    @Override
    public synchronized boolean allow(String key) {
        long now = System.currentTimeMillis();

        long windowStart = store.getWindowStartTime(key);
        int count = store.getRequestCount(key);

        // First request OR window expired
        if (windowStart == 0 || now - windowStart >= windowSizeMillis) {
            store.setWindowStartTime(key, now);
            store.setRequestCount(key, 0);
            count = 0;
        }

        if (count < limit) {
            store.setRequestCount(key, count + 1);
            return true;
        }

        return false;
    }
}
