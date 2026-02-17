package RateLimiter;

//rateconfig
// capacity, refillrate

import java.util.HashMap;
import java.util.Map;

//TokenBucket
interface RateLimiter2 {
    Boolean canAccess(String user);
}


class Bucket2 {
    int token;
    long lastRefiilTime;

    Bucket2(int token, long lastRefiilTime) {
        this.token = token;
        this.lastRefiilTime = lastRefiilTime;
    }

    public boolean hasTokens() {
        return token != 0;
    }

    public void consumeToken() {
        token--;
    }

    public void refillBucket(int refillRate, int capacity, long refillTime) {
        int timeDuration = (int) (refillTime - lastRefiilTime) / 1000;
        if (timeDuration < 1) return;

        token = Math.min(capacity, (refillRate * timeDuration) + token);
        lastRefiilTime = System.currentTimeMillis();
    }
}

class TokenBucketRateLimiter2 implements RateLimiter2 {
    int capacity;
    int refillRatePerSecond;

    Map<String, Bucket2> buckets;
    TokenBucketRateLimiter2(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.buckets = new HashMap<>();
    }


    @Override
    public Boolean canAccess(String user) {
        long now = System.currentTimeMillis();
        Bucket2 bucket = buckets.computeIfAbsent(user, k -> new Bucket2(capacity, now));

        if(!bucket.hasTokens()) {
            return false;
        }

        bucket.consumeToken();
        bucket.refillBucket(refillRatePerSecond,capacity, now);
        return true;

    }
}



class RateLimitConfig{
    int capacity;
    int refillRatePerSecond;
    int window;
    int limit;

    public int getCapacity(){
        return capacity;
    }

    public int getRefillRatePerSecond(){
        return  refillRatePerSecond;
    }
}

enum RateLimiterType2 {
        TOKEN_BUCKET,
}
class RateLimiterFactory2{
    public static  RateLimiter2 create(RateLimiterType2 type, RateLimitConfig config){
        switch (type){
            case TOKEN_BUCKET : return new TokenBucketRateLimiter2(config.getCapacity(), config.getRefillRatePerSecond());
        }
        return null;
    }
}


public class Kapil {
    public static void Main(String args) {
        RateLimitConfig rateLimiterConfig = new RateLimitConfig();
        rateLimiterConfig.capacity = 5;
        rateLimiterConfig.refillRatePerSecond = 3;

        RateLimiter2 rateLimiter = new TokenBucketRateLimiter2(rateLimiterConfig.capacity, rateLimiterConfig.refillRatePerSecond);

        for (int i = 0; i < 10; i++) {
            System.out.println("User permission : " + rateLimiter.canAccess("user1"));
            rateLimiter.canAccess("user1");
        }
        try {
            Thread.sleep(2000);
        } catch (Exception _) {}

        for (int i = 0; i < 10; i++) {
            System.out.println("User permission : " + rateLimiter.canAccess("user1"));
        }

    }
}