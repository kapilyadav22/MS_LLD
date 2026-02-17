package RateLimiter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;



public interface RateLimiter {
    boolean allow(String key);
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