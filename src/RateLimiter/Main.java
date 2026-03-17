package RateLimiter;


import RateLimiter.Algoritms.FixedWindowRateLimiter;
import RateLimiter.Algoritms.TokenBucketRateLimiter;
import RateLimiter.SeparateLImitStorage.InMemoryRateLimitStore;
import RateLimiter.SeparateLImitStorage.RateLimitStore;

import RateLimiter.SeparateLImitStorage.FixedWindowRateLimiter2;

import java.time.Duration;

//Different creation logic → Factory.
//Different behavior → Strategy.

class RateLimiterConfig {
    int capacity;
    int refillRate;
    int limit;
    int window;


    public int getCapacity() {
        return capacity;
    }

    public int getRefillRate() {
        return refillRate;
    }

    public int getLimit() {
        return limit;
    }

    public int getWindow() {
        return window;
    }
}

enum RateLimiterType { TOKEN_BUCKET, FIXED_WINDOW}

class RateLimiterFactory {
    public static RateLimiter create(RateLimiterType type, RateLimiterConfig config) {
        switch (type) {
            case TOKEN_BUCKET:
                return new TokenBucketRateLimiter(
                        config.getCapacity(),
                        config.getRefillRate()
                );
            case FIXED_WINDOW:
                return new FixedWindowRateLimiter(
                        config.getLimit(),
                        config.getWindow()
                );
            default:
                throw new IllegalArgumentException("Unsupported type");
        }
    }
}



public class Main {
    public static void main(String[] args) {
        RateLimiterConfig config = new RateLimiterConfig();
        config.capacity = 10;
        config.refillRate = 2; // tokens/sec
        config.limit = 100;
        config.window = 5000;

//        RateLimiterConfig config = new RateLimiterConfig.Builder()
//                .capacity(10)
//                .refillRate(2)
//                .build();

        RateLimiter limiter = RateLimiterFactory.create(RateLimiterType.TOKEN_BUCKET, config);

        RateLimiter rateLimiter = RateLimiterFactory.create(RateLimiterType.TOKEN_BUCKET, config);
        //RateLimitAlgorithm limiter = new FixedWindowRateLimiter(config.limit, config.window);        // per 5 seconds

        RateLimitStore store = new InMemoryRateLimitStore();
       // RateLimiter limiter2 = new FixedWindowRateLimiter2(3, 5000, store);

        String user = "user123";
        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " + rateLimiter.allow(user));
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("After window reset ");

        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " + rateLimiter.allow(user));
        }
    }
}

/* Rate Limiting Algorithms & Their Parameters
| Algorithm                  | Required Parameters               | What Each Parameter Means                          |
| -------------------------- | --------------------------------- | -------------------------------------------------- |
| **Fixed Window**           | `limit`, `windowSizeMillis`       | Max requests allowed in a fixed time window        |
| **Sliding Window (Log)**   | `limit`, `windowSizeMillis`       | Exact count of requests in the last rolling window |
| **Sliding Window Counter** | `limit`, `windowSizeMillis`       | Approximate rolling count using weighted windows   |
| **Token Bucket**           | `capacity`, `refillRatePerSecond` | Max burst size + steady refill rate                |
| **Leaky Bucket**           | `capacity`, `leakRatePerSecond`   | Max queue size + steady outflow rate               |

 */

//create ratelimiterconfig using builder pattern, if time persists. because we have optional attributes.
/*
class RateLimiterConfig {
    int capacity;
    int refillRate;
    int limit;
    Duration window;

    static class Builder {
        private final RateLimiterConfig config = new RateLimiterConfig();

        Builder capacity(int c) { config.capacity = c; return this; }
        Builder refillRate(int r) { config.refillRate = r; return this; }
        Builder limit(int l) { config.limit = l; return this; }
        Builder window(Duration w) { config.window = w; return this; }

        RateLimiterConfig build() {
            return config;
        }
    }
}
*/

/*
enum SpotSize { REGULAR, MOTORCYCLE, LARGE }

interface Vehicle {
  SpotSize getRequiredSpotSize();
}

class Car implements Vehicle {
  public SpotSize getRequiredSpotSize() { return SpotSize.REGULAR; }
}

class Motorcycle implements Vehicle {
  public SpotSize getRequiredSpotSize() { return SpotSize.MOTORCYCLE; }
}

class Truck implements Vehicle {
  public SpotSize getRequiredSpotSize() { return SpotSize.LARGE; }
}

class ParkingLot {
  boolean parkVehicle(Vehicle v) {
    SpotSize required = v.getRequiredSpotSize();
    ParkingSpot spot = findSpotBySize(required);
    return spot != null;
  }

  private ParkingSpot findSpotBySize(SpotSize size) { return null; }
}

class ParkingSpot {}


 */
