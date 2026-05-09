import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


/*



 */

interface RateLimiter {
    boolean allow(String key);
}


enum RateLimiterType {
    TOKEN_BUCKET,
    FIXED_WINDOW
}

class Bucket {

    private int tokens;
    private long lastRefillTime;

    Bucket(int capacity, long now) {
        this.tokens = capacity;
        this.lastRefillTime = now;
    }

    boolean hasTokens() {
        return tokens > 0;
    }

    void consume() {
        if (tokens > 0) {
            tokens--;
        }
    }

    void refill(int capacity, int refillRatePerSecond, long now) {
        long elapsedSeconds = (now - lastRefillTime) / 1000;

        if (elapsedSeconds <= 0) return;

        int refillTokens = (int) (elapsedSeconds * refillRatePerSecond);
        tokens = Math.min(capacity, tokens + refillTokens);
        lastRefillTime = now;
    }
}

class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final int refillRatePerSecond;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    @Override
    public boolean allow(String key) {
        ReentrantLock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
        lock.lock();
        try {
            long now = System.currentTimeMillis();

            Bucket bucket = buckets.computeIfAbsent( key, k -> new Bucket(capacity, now));
            bucket.refill(capacity, refillRatePerSecond, now);

            if (bucket.hasTokens()) {
                bucket.consume();
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }
}


class RateLimiterFactory {
    public static RateLimiter create( RateLimiterType type, int param1, int param2 ) {
        if (type == RateLimiterType.TOKEN_BUCKET) {
            return new TokenBucketRateLimiter(param1, param2);
        }

        if (type == RateLimiterType.FIXED_WINDOW) {
            return new FixedWindowRateLimiter(param1, param2);
        }

        return null;
    }
}


public class Main {

    public static void main(String[] args) throws InterruptedException {
        // param1 = capacity / limit
        // param2 = refillRate / window

        RateLimiter rateLimiter = RateLimiterFactory.create(RateLimiterType.TOKEN_BUCKET,  3, 1 );

        String[] requests = { "user1", "user1", "user1", "user1",  "user2", "user2" };

        for (String user : requests) {
            System.out.println(rateLimiter.allow(user));
            Thread.sleep(300);
        }
        Thread.sleep(3000);
        System.out.println("After refill");
        for (String user : requests) {
            System.out.println(rateLimiter.allow(user));
        }
    }
}

/*

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Input: TOKEN_BUCKET 5 2
        RateLimiterType type = RateLimiterType.valueOf(sc.next());
        int param1 = sc.nextInt();
        int param2 = sc.nextInt();

        RateLimiter rateLimiter = RateLimiterFactory.create(type, param1, param2);

        while (true) {
            String user = sc.next();
            if (user.equals("END")) break;
            System.out.println(rateLimiter.allow(user));
        }
        sc.close();
    }
}
*/



/*
public class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final int refillRatePerSecond;

    private final Map<String, Bucket> buckets = new HashMap<>();

    public TokenBucketRateLimiter(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    @Override
    public synchronized boolean allow(String key) {
        long now = System.currentTimeMillis();

        Bucket bucket = buckets.computeIfAbsent(key,k -> new Bucket(capacity, now));
        //  Lazy refill
        bucket.refill(capacity, refillRatePerSecond, now);

        if (bucket.hasTokens()) {
            bucket.consume();
            return true;
        }

        return false;
    }
}
*/