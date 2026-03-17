/*
Logger
ConfigManager
CacheManager
DatabaseConnection
*/


#include <iostream>
using namespace std;

class Logger {
private:
    Logger() {}

public:

/* for preventing copying
    Logger(const Logger&) = delete;
    Logger& operator=(const Logger&) = delete;
*/
    static Logger& getInstance() {
        static Logger instance;
        return instance;
    }

    void log(string msg) {
        cout << msg << endl;
    }
};

int main() {

    Logger& logger = Logger::getInstance();
    logger.log("Hello");
}


/*
Why this works
Since C++11, initialization of function local static object is thread-safe.

Meaning:
    static Logger instance;  is guaranteed to be initialized only once, even with multiple threads.
No mutex needed.
*/