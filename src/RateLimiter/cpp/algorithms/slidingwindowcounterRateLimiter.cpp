#include <bits/stdc++.h>
using namespace std;
using namespace chrono;

class SlidingWindowCounter {

private:

    struct Window {
        int count;
        long long windowStart;

        Window(long long start) {
            windowStart = start;
            count = 0;
        }
    };

    shared_ptr<Window> current;
    shared_ptr<Window> previous;

public:

    SlidingWindowCounter(long long windowStart) {
        current = make_shared<Window>(windowStart);
        previous = nullptr;
    }

    bool allow(int limit, long long windowSizeMillis, long long now) {

        long long currentWindowStart = now - (now % windowSizeMillis);

        // window moved
        if (current->windowStart != currentWindowStart) {
            previous = current;
            current = make_shared<Window>(currentWindowStart);
        }

        double prevWeight = 0.0;

        if (previous != nullptr) {
            long long elapsed = now - current->windowStart;
            prevWeight = (double)(windowSizeMillis - elapsed) / windowSizeMillis;
        }

        double effectiveCount =
            (previous ? previous->count * prevWeight : 0)
            + current->count;

        if (effectiveCount < limit) {
            current->count++;
            return true;
        }

        return false;
    }
};


class RateLimiter {
public:
    virtual bool allow(string key) = 0;
    virtual ~RateLimiter() {}
};


class SlidingWindowCounterRateLimiter : public RateLimiter {

private:

    int limit;
    long long windowSizeMillis;

    unordered_map<string, shared_ptr<SlidingWindowCounter>> counters;
    unordered_map<string, shared_ptr<mutex>> locks;

public:

    SlidingWindowCounterRateLimiter(int limit, long long windowSizeMillis) {
        this->limit = limit;
        this->windowSizeMillis = windowSizeMillis;
    }

    bool allow(string key) override {

        if (locks.find(key) == locks.end()) {
            locks[key] = make_shared<mutex>();
        }

        auto lock = locks[key];
        lock_guard<mutex> guard(*lock);

        auto now = duration_cast<milliseconds>(
            steady_clock::now().time_since_epoch()
        ).count();

        long long windowStart = now - (now % windowSizeMillis);

        if (counters.find(key) == counters.end()) {
            counters[key] =
                make_shared<SlidingWindowCounter>(windowStart);
        }

        auto counter = counters[key];

        return counter->allow(limit, windowSizeMillis, now);
    }
};