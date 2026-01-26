package RateLimiter.Algoritms;

import RateLimiter.RateLimitAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class SlidingWindowCounterRateLimiter implements RateLimitAlgorithm {

    private static class Window {
        int count;
        long windowStart;

        Window(long windowStart) {
            this.windowStart = windowStart;
            this.count = 0;
        }
    }

    private final int limit;
    private final long windowSizeMillis;

    private final Map<String, Window> currentWindow = new HashMap<>();
    private final Map<String, Window> previousWindow = new HashMap<>();

    public SlidingWindowCounterRateLimiter(int limit, long windowSizeMillis) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public synchronized boolean allow(String key) {
        long now = System.currentTimeMillis();
        long currentWindowStart = now - (now % windowSizeMillis);

        Window curr = currentWindow.get(key);

        // First request OR window advanced
        if (curr == null || curr.windowStart != currentWindowStart) {
            Window prev = curr;
            if (prev != null) {
                previousWindow.put(key, prev);
            }
            curr = new Window(currentWindowStart);
            currentWindow.put(key, curr);
        }

        Window prev = previousWindow.get(key);

        double prevWeight = 0.0;
        if (prev != null) {
            long elapsed = now - curr.windowStart;
            prevWeight = (double) (windowSizeMillis - elapsed) / windowSizeMillis;
        }

        double effectiveCount =
                (prev != null ? prev.count * prevWeight : 0)
                        + curr.count;

        if (effectiveCount < limit) {
            curr.count++;
            return true;
        }

        return false;
    }
}

