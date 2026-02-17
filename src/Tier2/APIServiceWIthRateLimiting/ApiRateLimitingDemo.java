package Tier2.DistributedLock.APIServiceWIthRateLimiting;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Entry point (Main class)
 */
public class ApiRateLimitingDemo {

    public static void main(String[] args) throws InterruptedException {

        RateLimitRule rule = new RateLimitRule(3, 10); // 3 requests per 10 seconds
        RateLimiter rateLimiter = new FixedWindowRateLimiter(rule);

        ApiService apiService = new ApiService(rateLimiter);

        String clientId = "client-1";

        for (int i = 1; i <= 5; i++) {
            try {
                apiService.handleRequest(clientId);
                System.out.println("Request " + i + " allowed");
            } catch (RuntimeException ex) {
                System.out.println("Request " + i + " rejected: " + ex.getMessage());
            }
            Thread.sleep(1000);
        }
    }
}

/**
 * API Service
 */
class ApiService {

    private final RateLimiter rateLimiter;

    public ApiService(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public void handleRequest(String clientId) {
        if (!rateLimiter.allowRequest(clientId)) {
            throw new RuntimeException("429 Too Many Requests");
        }
        // Business logic goes here
    }
}

/**
 * RateLimiter Strategy Interface
 */
interface RateLimiter {
    boolean allowRequest(String clientId);
}

/**
 * Fixed Window Rate Limiter Implementation
 */
class FixedWindowRateLimiter implements RateLimiter {

    private final RateLimitRule rule;
    private final ConcurrentHashMap<String, WindowCounter> store = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(RateLimitRule rule) {
        this.rule = rule;
    }

    @Override
    public boolean allowRequest(String clientId) {
        long currentTime = System.currentTimeMillis() / 1000;
        long windowStart = currentTime - (currentTime % rule.getWindowInSeconds());

        WindowCounter counter = store.computeIfAbsent(
                clientId,
                key -> new WindowCounter(windowStart)
        );

        synchronized (counter) {
            if (counter.getWindowStart() != windowStart) {
                counter.reset(windowStart);
            }

            if (counter.getCount() >= rule.getMaxRequests()) {
                return false;
            }

            counter.increment();
            return true;
        }
    }
}

/**
 * Rate limit rule
 */
class RateLimitRule {

    private final int maxRequests;
    private final long windowInSeconds;

    public RateLimitRule(int maxRequests, long windowInSeconds) {
        this.maxRequests = maxRequests;
        this.windowInSeconds = windowInSeconds;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public long getWindowInSeconds() {
        return windowInSeconds;
    }
}

/**
 * Window Counter
 */
class WindowCounter {

    private long windowStart;
    private final AtomicInteger count = new AtomicInteger(0);

    public WindowCounter(long windowStart) {
        this.windowStart = windowStart;
    }

    public long getWindowStart() {
        return windowStart;
    }

    public int getCount() {
        return count.get();
    }

    public void increment() {
        count.incrementAndGet();
    }

    public void reset(long newWindowStart) {
        this.windowStart = newWindowStart;
        count.set(0);
    }
}
