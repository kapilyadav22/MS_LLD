package RateLimiter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
//
// interface RateLimitAlgorithm {
//    boolean allow(String key);
//}

public class RateLimiter {
    private final RateLimitAlgorithm algorithm;
    public RateLimiter(RateLimitAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public boolean allow(String key) {
        return algorithm.allow(key);
    }
}

/*

| Algorithm                  | When to use           |
| -------------------------- | --------------------- |
| Fixed Window               | Simple internal tools |
| Sliding Window Log         | Strict accuracy       |
| Sliding Window Counter     | Best balance          |
| Token Bucket               | APIs / burst handling |
| Leaky Bucket               | Traffic shaping       |

 */