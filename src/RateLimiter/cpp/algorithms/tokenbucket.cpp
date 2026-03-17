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
    Bucket(int capacity, TimePoint now) {
        tokens = capacity;
        lastRefillTime = now;
    }

    bool hasTokens() {
        return tokens > 0;
    }

    void consume() {
        if (tokens > 0) tokens--;
    }

    void refill(int capacity, int refillRatePerSecond, TimePoint now) {

        auto elapsedSeconds =   duration_cast<seconds>(now - lastRefillTime).count();

        if (elapsedSeconds <= 0) return;

        int refillTokens = elapsedSeconds * refillRatePerSecond;

        tokens = min(capacity, tokens + refillTokens);

        lastRefillTime = now;
    }
};

class RateLimiter {
public:
    virtual bool allow(string key) = 0;
    virtual ~RateLimiter() {}
};

class TokenBucketRateLimiter : public RateLimiter {

private:
    int capacity;
    int refillRatePerSecond;

    unordered_map<string, shared_ptr<Bucket>> buckets;
    unordered_map<string,mutex> locks;

public:

    TokenBucketRateLimiter(int capacity, int refillRatePerSecond) {
        this->capacity = capacity;
        this->refillRatePerSecond = refillRatePerSecond;
    }

    bool allow(string key) override {

        lock_guard<mutex> guard(locks[key]);

        auto now = steady_clock::now();

        if (buckets.find(key) == buckets.end()) {
            buckets[key] = make_shared<Bucket>(capacity, now);
        }

        auto& bucket = buckets[key];

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

    string key;
    int i=1;
    while (cin >> key) {
        if (limiter->allow(key))
            cout << "true\n";
        else
            cout << "false\n";
    if(i==4) this_thread::sleep_for(seconds(1));
    i++;

    }

    return 0;
}


//not thread safa