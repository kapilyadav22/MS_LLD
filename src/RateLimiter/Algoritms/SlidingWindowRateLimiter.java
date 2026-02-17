package RateLimiter.Algoritms;

import RateLimiter.RateLimiter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

//Sliding Window

class SlidingWindow {

    private final Deque<Long> timestamps = new ArrayDeque<>();
    boolean allow(int limit, long windowSizeMillis, long now) {

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


public class SlidingWindowRateLimiter implements RateLimiter {

    private final int limit;
    private final long windowSizeMillis;

    private final Map<String, SlidingWindow> windows = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int limit, long windowSizeMillis) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public boolean allow(String key) {
        ReentrantLock lock = locks.computeIfAbsent(key,k -> new ReentrantLock() );

        lock.lock();
        try {
            long now = System.currentTimeMillis();
            SlidingWindow window = windows.computeIfAbsent( key,k -> new SlidingWindow() );
            return window.allow(limit, windowSizeMillis, now);
        } finally {
            lock.unlock();
        }
    }
}
