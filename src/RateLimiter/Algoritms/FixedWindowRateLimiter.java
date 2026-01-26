package RateLimiter.Algoritms;

import RateLimiter.RateLimitAlgorithm;

import java.util.HashMap;
import java.util.Map;


//Fixed WIndow
public class FixedWindowRateLimiter implements RateLimitAlgorithm {

    private final int limit;
    private final long windowSizeMillis;

    private final Map<String, Integer> requestCount = new HashMap<>();
    private final Map<String, Long> windowStartTime = new HashMap<>();

    public FixedWindowRateLimiter(int limit, long windowSizeMillis) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public synchronized boolean allow(String key) {
        long now = System.currentTimeMillis();

        windowStartTime.putIfAbsent(key, now);
        requestCount.putIfAbsent(key, 0);

        long windowStart = windowStartTime.get(key);

        // Reset window if expired
        if (now - windowStart >= windowSizeMillis) {
            windowStartTime.put(key, now);
            requestCount.put(key, 0);
        }

        if (requestCount.get(key) < limit) {
            requestCount.put(key, requestCount.get(key) + 1);
            return true;
        }

        return false;
    }
}
