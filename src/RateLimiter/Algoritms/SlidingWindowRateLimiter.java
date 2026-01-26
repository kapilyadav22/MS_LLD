package RateLimiter.Algoritms;

import RateLimiter.RateLimitAlgorithm;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

//Sliding Window


 public class SlidingWindowRateLimiter implements RateLimitAlgorithm {

    private final int limit;
    private final long windowSizeMillis;

    private final Map<String, Deque<Long>> requests = new HashMap<>();

    public SlidingWindowRateLimiter(int limit, long windowSizeMillis) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public synchronized boolean allow(String key) {
        long now = System.currentTimeMillis();

        requests.putIfAbsent(key, new ArrayDeque<>());
        Deque<Long> timestamps = requests.get(key);

        // Remove expired timestamps
        while (!timestamps.isEmpty()
                && now - timestamps.peekFirst() >= windowSizeMillis) {
            timestamps.pollFirst();
        }

        if (timestamps.size() < limit) {
            timestamps.addLast(now);
            return true;
        }

        return false;
    }
}
