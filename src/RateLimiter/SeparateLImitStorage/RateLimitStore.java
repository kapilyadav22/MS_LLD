package RateLimiter.SeparateLImitStorage;

public interface RateLimitStore {

    int getRequestCount(String key);
    void setRequestCount(String key, int count);
    long getWindowStartTime(String key);
    void setWindowStartTime(String key, long timestamp);
}