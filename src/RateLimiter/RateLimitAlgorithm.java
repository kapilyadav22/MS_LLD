package RateLimiter;

public interface RateLimitAlgorithm {
    boolean allow(String key);
}