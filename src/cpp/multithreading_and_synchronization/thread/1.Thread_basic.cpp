#include <iostream>
#include <thread>

using namespace std;

void task() {
    cout << "Thread running\n";
}

int main() {
    thread t(task);   // create thread
    t.join();         // wait for thread to finish
    return 0;
}

/*
    Important methods
    join() → waits for thread to finish
    detach() → runs thread independently
*/

/* Multiple Threads
#include <iostream>
#include <thread>

using namespace std;

void work(int id) {
    cout << "Thread " << id << " running\n";
}

int main() {

    thread t1(work, 1);
    thread t2(work, 2);
    thread t3(work, 3);

    t1.join();
    t2.join();
    t3.join();
}
*/