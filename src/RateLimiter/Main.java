package RateLimiter;


import RateLimiter.SeparateLImitStorage.InMemoryRateLimitStore;
import RateLimiter.SeparateLImitStorage.RateLimitStore;

import RateLimiter.SeparateLImitStorage.FixedWindowRateLimiter2;

public class Main {
    public static void main(String[] args) {
        //RateLimitAlgorithm algorithm = new FixedWindowRateLimiter(3, 5000);        // per 5 seconds
        RateLimitStore store = new InMemoryRateLimitStore();

        RateLimitAlgorithm algorithm2 = new FixedWindowRateLimiter2(3, 5000, store);

        RateLimiter rateLimiter = new RateLimiter(algorithm2);

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
