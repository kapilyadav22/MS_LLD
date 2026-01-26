package RateLimiter.SeparateLImitStorage;

import RateLimiter.RateLimitAlgorithm;

public class RateLimiter1 {

    private final RateLimitAlgorithm algorithm;

    public RateLimiter1(RateLimitAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public boolean allow(String key) {
        return algorithm.allow(key);
    }

}
