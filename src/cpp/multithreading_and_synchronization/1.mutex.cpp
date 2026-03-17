#include <iostream>
#include <thread>
#include <mutex>

using namespace std;

mutex mtx;

void print(int id) {

    mtx.lock();
    cout << "Thread " << id << endl;
    mtx.unlock();
}

int main() {

    thread t1(print, 1);
    thread t2(print, 2);

    t1.join();
    t2.join();
}