#include <iostream>
#include <thread>
#include <atomic>

using namespace std;

atomic<int> counter(0);

void increment() {
    for(int i = 0; i < 1000; i++) {
        counter++;
    }
}

int main() {

    thread t1(increment);
    thread t2(increment);

    t1.join();
    t2.join();

    cout << "Final counter: " << counter << endl;
}


/*
Used when multiple threads modify a simple variable safely without mutex.

Problem
    Multiple threads increment a counter.

Two threads can interfere → race condition.
atomic makes the operation thread-safe without mutex.

*/