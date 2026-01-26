package RateLimiter.SeparateLImitStorage;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRateLimitStore implements RateLimitStore {

    private final Map<String, Integer> requestCount = new HashMap<>();
    private final Map<String, Long> windowStartTime = new HashMap<>();

    @Override
    public int getRequestCount(String key) {
        return requestCount.getOrDefault(key, 0);
    }

    @Override
    public void setRequestCount(String key, int count) {
        requestCount.put(key, count);
    }

    @Override
    public long getWindowStartTime(String key) {
        return windowStartTime.getOrDefault(key, 0L);
    }

    @Override
    public void setWindowStartTime(String key, long timestamp) {
        windowStartTime.put(key, timestamp);
    }
}
