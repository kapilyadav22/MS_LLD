#include <bits/stdc++.h>
using namespace std;

// ---------------- ENUM ----------------
enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
};

// ---------------- UTILS ----------------
string levelToString(LogLevel level) {
    switch(level) {
        case LogLevel::DEBUG: return "DEBUG";
        case LogLevel::INFO: return "INFO";
        case LogLevel::WARN: return "WARN";
        case LogLevel::ERROR: return "ERROR";
    }
    return "";
}

// ---------------- LOG MESSAGE ----------------
class LogMessage {
public:
    LogLevel level;
    string message;
    time_t timestamp;

    LogMessage(LogLevel level, const string& message)
        : level(level), message(message) {
        timestamp = time(nullptr);
    }
};

// ---------------- FORMATTER ----------------
class Formatter {
public:
    virtual string format(const LogMessage& log) = 0;
    virtual ~Formatter() = default;
};

class SimpleFormatter : public Formatter {
public:
    string format(const LogMessage& log) override {
        return "[" + levelToString(log.level) + "] " + log.message;
    }
};

// ---------------- APPENDER ----------------
class Appender {
protected:
    shared_ptr<Formatter> formatter;

public:
    Appender(shared_ptr<Formatter> fmt) : formatter(fmt) {}
    virtual void append(const LogMessage& log) = 0;
    virtual ~Appender() = default;
};

class ConsoleAppender : public Appender {
public:
    ConsoleAppender(shared_ptr<Formatter> fmt) : Appender(fmt) {}

    void append(const LogMessage& log) override {
        cout << formatter->format(log) << endl;
    }
};

class FileAppender : public Appender {
private:
    string filename;

public:
    FileAppender(const string& file, shared_ptr<Formatter> fmt)
        : Appender(fmt), filename(file) {}

    void append(const LogMessage& log) override {
        ofstream fout(filename, ios::app);
        fout << formatter->format(log) << endl;
        fout.close();
    }
};

// ---------------- LOGGER ----------------
class Logger {
private:
    vector<shared_ptr<Appender>> appenders;
    LogLevel minLevel;

public:
    Logger(LogLevel level) : minLevel(level) {}

    void addAppender(shared_ptr<Appender> appender) {
        appenders.push_back(appender);
    }

    void log(LogLevel level, const string& message) {
        if ((int)level < (int)minLevel) return;

        LogMessage logMsg(level, message);

        for (auto& appender : appenders) {
            appender->append(logMsg);
        }
    }
};

// ---------------- FACTORY ----------------
class LoggerFactory {
public:
    static shared_ptr<Logger> createLogger(LogLevel level, const string& file) {
        auto formatter = make_shared<SimpleFormatter>();

        auto logger = make_shared<Logger>(level);

        logger->addAppender(make_shared<ConsoleAppender>(formatter));
        logger->addAppender(make_shared<FileAppender>(file, formatter));

        return logger;
    }
};

// ---------------- PARSER ----------------
LogLevel parseLevel(const string& level) {
    if (level == "DEBUG") return LogLevel::DEBUG;
    if (level == "INFO") return LogLevel::INFO;
    if (level == "WARN") return LogLevel::WARN;
    return LogLevel::ERROR;
}

// ---------------- MAIN ----------------
int main() {
    int n;
    cin >> n;

    string minLevelStr, filename;
    cin >> minLevelStr >> filename;

    auto logger = LoggerFactory::createLogger(parseLevel(minLevelStr), filename);

    cin.ignore();

    while (n--) {
        string levelStr, message;
        cin >> levelStr;
        getline(cin, message);

        // remove leading space
        if (!message.empty() && message[0] == ' ') {
            message = message.substr(1);
        }

        logger->log(parseLevel(levelStr), message);
    }

    return 0;
}