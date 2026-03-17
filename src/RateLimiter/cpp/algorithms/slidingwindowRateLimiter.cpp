#include <bits/stdc++.h>
using namespace std;
using namespace chrono;

class SlidingWindow {

private:
    deque<long long> timestamps;

public:
    bool allow(int limit, long long windowSizeMillis, long long now) {

        // Remove expired timestamps
        while (!timestamps.empty() &&
               now - timestamps.front() >= windowSizeMillis) {
            timestamps.pop_front();
        }

        if (timestamps.size() < limit) {
            timestamps.push_back(now);
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


class SlidingWindowRateLimiter : public RateLimiter {

private:
    int limit;
    long long windowSizeMillis;

    unordered_map<string, shared_ptr<SlidingWindow>> windows;
    unordered_map<string, shared_ptr<mutex>> locks;

public:

    SlidingWindowRateLimiter(int limit, long long windowSizeMillis) {
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

        if (windows.find(key) == windows.end()) {
            windows[key] = make_shared<SlidingWindow>();
        }

        auto window = windows[key];

        return window->allow(limit, windowSizeMillis, now);
    }
};