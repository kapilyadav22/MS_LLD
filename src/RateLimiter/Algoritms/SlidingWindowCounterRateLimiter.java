package RateLimiter.Algoritms;

import RateLimiter.RateLimiter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

class SlidingWindowCounter {

    private static class Window {
        int count;
        long windowStart;

        Window(long windowStart) {
            this.windowStart = windowStart;
            this.count = 0;
        }
    }

    private Window current;
    private Window previous;

    SlidingWindowCounter(long windowStart) {
        this.current = new Window(windowStart);
        this.previous = null;
    }

    boolean allow(int limit, long windowSizeMillis, long now) {
        long currentWindowStart = now - (now % windowSizeMillis);

        // Window moved
        if (current.windowStart != currentWindowStart) {
            previous = current;
            current = new Window(currentWindowStart);
        }

        double prevWeight = 0.0;
        if (previous != null) {
            long elapsed = now - current.windowStart;
            prevWeight =
                    (double) (windowSizeMillis - elapsed) / windowSizeMillis;
        }

        double effectiveCount =
                (previous != null ? previous.count * prevWeight : 0)
                        + current.count;

        if (effectiveCount < limit) {
            current.count++;
            return true;
        }

        return false;
    }
}


public class SlidingWindowCounterRateLimiter implements RateLimiter {

    private final int limit;
    private final long windowSizeMillis;

    private final Map<String, SlidingWindowCounter> counters = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks =new ConcurrentHashMap<>();

    public SlidingWindowCounterRateLimiter(int limit, long windowSizeMillis) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public boolean allow(String key) {

        ReentrantLock lock = locks.computeIfAbsent(key,k -> new ReentrantLock() );
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            long windowStart = now - (now % windowSizeMillis);

            SlidingWindowCounter counter = counters.computeIfAbsent( key, k -> new SlidingWindowCounter(windowStart));

            return counter.allow(limit, windowSizeMillis, now);

        } finally {
            lock.unlock();
        }
    }
}

