#include <iostream>
#include <thread>

using namespace std;

void print(int x) {
    cout << "Value: " << x << endl;
}

int main() {

    thread t(print, 10);
    t.join();

}