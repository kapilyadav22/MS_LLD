package RateLimiter.Algoritms;

import RateLimiter.RateLimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

//Fixed Window
class FixedWindow {

    private int count;
    private long windowStart;

    FixedWindow(long now) {
        this.windowStart = now;
        this.count = 0;
    }

    boolean allow(int limit, long windowSizeMillis, long now) {
        if (now - windowStart >= windowSizeMillis) {
            windowStart = now;
            count = 0;
        }

        if (count < limit) {
            count++;
            return true;
        }

        return false;
    }
}

public class FixedWindowRateLimiter implements RateLimiter {

    private final int limit;
    private final long windowSizeMillis;

    private final Map<String, FixedWindow> windows = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int limit, long windowSizeMillis) {
        this.limit = limit;
        this.windowSizeMillis = windowSizeMillis;
    }

    @Override
    public boolean allow(String key) {
        ReentrantLock lock = locks.computeIfAbsent(key,k -> new ReentrantLock() );
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            FixedWindow window = windows.computeIfAbsent( key,k -> new FixedWindow(now));

            return window.allow(limit, windowSizeMillis, now);
        } finally {
            lock.unlock();
        }
    }
}
