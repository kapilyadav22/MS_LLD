#include <bits/stdc++.h>
#include <thread>

using namespace std;
using namespace chrono;

using TimePoint = steady_clock::time_point;

class Bucket {
private:
    int tokens;
    TimePoint lastRefillTime;

public:
    Bucket(int capacity, TimePoint now)
        : tokens(capacity), lastRefillTime(now) {}

    bool hasTokens() {
        return tokens > 0;
    }

    void consume() {
        if (tokens > 0) tokens--;
    }

    void refill(int capacity, int refillRatePerSecond, TimePoint now) {

        auto elapsedSeconds =
            duration_cast<seconds>(now - lastRefillTime).count();

        if (elapsedSeconds <= 0) return;

        int refillTokens = elapsedSeconds * refillRatePerSecond;

        tokens = min(capacity, tokens + refillTokens);

        lastRefillTime = now;
    }
};

class RateLimiter {
public:
    virtual bool allow(const string& key) = 0;
    virtual ~RateLimiter() = default;
};

class TokenBucketRateLimiter : public RateLimiter {

private:
    int capacity;
    int refillRatePerSecond;

    unordered_map<string, shared_ptr<Bucket>> buckets;
    unordered_map<string, shared_ptr<mutex>> locks;

    mutex mapMutex;   // protects maps

public:

    TokenBucketRateLimiter(int capacity, int refillRatePerSecond)
        : capacity(capacity), refillRatePerSecond(refillRatePerSecond) {}

    bool allow(const string& key) override {

        shared_ptr<mutex> userLock;

        {
            lock_guard<mutex> g(mapMutex);

            if (!locks.count(key))
                locks[key] = make_shared<mutex>();

            if (!buckets.count(key))
                buckets[key] = make_shared<Bucket>(capacity, steady_clock::now());

            userLock = locks[key];
        }

        lock_guard<mutex> guard(*userLock);

        auto now = steady_clock::now();

        auto bucket = buckets[key];

        bucket->refill(capacity, refillRatePerSecond, now);

        if (bucket->hasTokens()) {
            bucket->consume();
            return true;
        }

        return false;
    }
};

class RateLimiterFactory {

public:

    static unique_ptr<RateLimiter> createRateLimiter(const string& type) {

        if (type == "TokenBucket") {

            int capacity, refillRate;
            cin >> capacity >> refillRate;

            return make_unique<TokenBucketRateLimiter>(capacity, refillRate);
        }

        throw runtime_error("Unknown RateLimiter type");
    }
};

int main() {

    string limiterType;
    cin >> limiterType;

    auto limiter = RateLimiterFactory::createRateLimiter(limiterType);

    vector<thread> threads;

    vector<string> users = {"user1","user2","user1","user3","user2","user1"};

    for (auto u : users) {

        threads.emplace_back([&limiter, u]() {

            if (limiter->allow(u))
                cout << u << " -> true\n";
            else
                cout << u << " -> false\n";

        });
    }

    for (auto& t : threads)
        t.join();

    return 0;
}