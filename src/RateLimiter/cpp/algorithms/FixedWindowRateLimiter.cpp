#include <bits/stdc++.h>
using namespace std;
using namespace chrono;

class FixedWindow {

private:
    int count;
    long long windowStart;

public:
    FixedWindow(long long now) {
        windowStart = now;
        count = 0;
    }

    bool allow(int limit, long long windowSizeMillis, long long now) {

        if (now - windowStart >= windowSizeMillis) {
            windowStart = now;
            count = 0;
        }

        if (count < limit) {
            count++;
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


class FixedWindowRateLimiter : public RateLimiter {

private:
    int limit;
    long long windowSizeMillis;

    unordered_map<string, shared_ptr<FixedWindow>> windows;
    unordered_map<string, shared_ptr<mutex>> locks;

public:

    FixedWindowRateLimiter(int limit, long long windowSizeMillis) {
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
            windows[key] = make_shared<FixedWindow>(now);
        }

        auto window = windows[key];

        return window->allow(limit, windowSizeMillis, now);
    }
};