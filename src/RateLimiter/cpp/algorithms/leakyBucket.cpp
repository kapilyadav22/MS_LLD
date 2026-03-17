#include <bits/stdc++.h>

using namespace std;
using namespace chrono;

using TimePoint = steady_clock::time_point;

class LeakyBucket {

private:
    int water;
    TimePoint lastLeakTime;

public:

    LeakyBucket(TimePoint now) {
        water = 0;
        lastLeakTime = now;
    }

    void leak(int leakRatePerSecond, TimePoint now) {

        auto elapsedSeconds =
            duration_cast<seconds>(now - lastLeakTime).count();

        if (elapsedSeconds <= 0) return;

        int leaked = elapsedSeconds * leakRatePerSecond;

        water = max(0, water - leaked);

        lastLeakTime = now;
    }

    bool canAccept(int capacity) {
        return water < capacity;
    }

    void addDrop() {
        water++;
    }
};


class RateLimiter {
public:
    virtual bool allow(string key) = 0;
    virtual ~RateLimiter() {}
};


class LeakyBucketRateLimiter : public RateLimiter {

private:

    int capacity;
    int leakRatePerSecond;

    unordered_map<string, LeakyBucket> buckets;
    unordered_map<string, mutex> locks;

public:

    LeakyBucketRateLimiter(int capacity, int leakRatePerSecond) {
        this->capacity = capacity;
        this->leakRatePerSecond = leakRatePerSecond;
    }

    bool allow(string key) override {

        auto& lock = locks[key];
        lock_guard<mutex> guard(lock);

        auto now = steady_clock::now();

//        if (buckets.find(key) == buckets.end()) {
//            buckets[key] = make_shared<LeakyBucket>(now);
//        }

        auto& bucket = buckets[key];

        bucket->leak(leakRatePerSecond, now);

        if (bucket->canAccept(capacity)) {
            bucket->addDrop();
            return true;
        }

        return false;
    }
};