#include <iostream>
#include <thread>
#include <queue>
#include <mutex>
#include <condition_variable>

using namespace std;

queue<int> q;
mutex mtx;
condition_variable cv;

void producer() {
    for(int i = 1; i <= 5; i++) {
        {
            unique_lock<mutex> lock(mtx);
            q.push(i);
            cout << "Produced: " << i << endl;
        }
        cv.notify_one(); // notify consumer
    }
}

void consumer() {
    for(int i = 1; i <= 5; i++) {
        unique_lock<mutex> lock(mtx);

        cv.wait(lock, []{
            return !q.empty();
        });

        int val = q.front();
        q.pop();

        cout << "Consumed: " << val << endl;
    }
}

int main() {

    thread t1(producer);
    thread t2(consumer);

    t1.join();
    t2.join();
}


/*
Use it when one thread must wait until another thread signals something.

Classic example: Producer → Consumer
Idea
    Producer adds data
    Consumer waits until data is available

Important Things:
cv.wait(lock, condition) means:
    while(condition is false)
       thread sleeps

When producer calls:
    cv.notify_one()
the waiting thread wakes up.
*/