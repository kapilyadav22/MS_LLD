#include <iostream>
#include <thread>
#include <mutex>

using namespace std;

class Counter {
private:
    int count = 0;
    mutex mtx;

public:
    void increment() {
        lock_guard<mutex> lock(mtx);
        count++;
    }

    int get() {
        lock_guard<mutex> lock(mtx);
        return count;
    }
};

int main() {
    Counter counter;

    thread t1(work, ref(counter));
    thread t2(work, ref(counter));
    thread t3(work, ref(counter));

    t1.join();
    t2.join();
    t3.join();

    cout << "Final Counter Value: " << counter.get() << endl;
    return 0;
}

/*
Simple
Exception safe
Prevents forgetting unlock()

How lock_guard Works
    When a lock_guard object is created:
    It locks the mutex
    When the object goes out of scope → mutex automatically unlocks

Reason why we should use mutex with lock_guard:
Consider below code :
mutex m;
void work() {
    m.lock();
    // some logic
    if(true) return;   // mutex never unlocked
    m.unlock();
}
*/


/*

Where lock_guard Appears in LLD Interviews
Common problems:
LRU Cache
Rate Limiter
Logger
Bank Account
Thread-safe queue
Elevator system
*/