#include <bits/stdc++.h>
using namespace std;

// -------------------- ENUM --------------------
enum class ChannelType {
    EMAIL,
    SMS,
    PUSH
};

ChannelType getChannelType(const string& type) {
    if (type == "EMAIL") return ChannelType::EMAIL;
    if (type == "SMS") return ChannelType::SMS;
    return ChannelType::PUSH;
}

// -------------------- OBSERVER --------------------
class NotificationChannel {
public:
    virtual void send(const string& userName, const string& message) = 0;
    virtual ~NotificationChannel() = default;
};

// -------------------- CONCRETE CHANNELS --------------------
class EmailChannel : public NotificationChannel {
public:
    void send(const string& userName, const string& message) override {
        cout << "[EMAIL] to " << userName << ": " << message << "\n";
    }
};

class SMSChannel : public NotificationChannel {
public:
    void send(const string& userName, const string& message) override {
        cout << "[SMS] to " << userName << ": " << message << "\n";
    }
};

class PushChannel : public NotificationChannel {
public:
    void send(const string& userName, const string& message) override {
        cout << "[PUSH] to " << userName << ": " << message << "\n";
    }
};

// -------------------- FACTORY --------------------
class NotificationFactory {
public:
    static shared_ptr<NotificationChannel> createChannel(ChannelType type) {
        switch (type) {
            case ChannelType::EMAIL:
                return make_shared<EmailChannel>();
            case ChannelType::SMS:
                return make_shared<SMSChannel>();
            case ChannelType::PUSH:
                return make_shared<PushChannel>();
        }
        return nullptr;
    }
};

// -------------------- USER --------------------
class User {
public:
    string name;

    // shared_ptr justified → channels are shared across users
    unordered_map<ChannelType, shared_ptr<NotificationChannel>> subscriptions;

    User(const string& name) : name(name) {}

    void subscribe(ChannelType type, shared_ptr<NotificationChannel> channel) {
        subscriptions[type] = channel;
    }

    void unsubscribe(ChannelType type) {
        subscriptions.erase(type);
    }

    void notify(const string& message) {
        for (auto& it : subscriptions) {
            it.second->send(name, message);
        }
    }
};

// -------------------- SERVICE --------------------
class NotificationService {
private:
    unordered_map<string, unique_ptr<User>> users;

    // Flyweight cache
    unordered_map<ChannelType, shared_ptr<NotificationChannel>> channelCache;

public:
    void addUser(const string& name) {
        users[name] = make_unique<User>(name);
    }

    shared_ptr<NotificationChannel> getChannel(ChannelType type) {
        auto it = channelCache.find(type);
        if (it != channelCache.end()) {
            return it->second;
        }

        auto channel = NotificationFactory::createChannel(type);
        channelCache[type] = channel;
        return channel;
    }

    void subscribe(const string& userName, ChannelType type) {
        auto it = users.find(userName);
        if (it != users.end()) {
            it->second->subscribe(type, getChannel(type));
        }
    }

    void unsubscribe(const string& userName, ChannelType type) {
        auto it = users.find(userName);
        if (it != users.end()) {
            it->second->unsubscribe(type);
        }
    }

    void sendNotification(const string& message) {
        for (auto& it : users) {
            it.second->notify(message);
        }
    }
};

// -------------------- MAIN --------------------
int main() {
    NotificationService service;

    int q;
    cin >> q;

    while (q--) {
        string command;
        cin >> command;

        if (command == "ADD_USER") {
            string name;
            cin >> name;
            service.addUser(name);
        }
        else if (command == "SUBSCRIBE") {
            string name, type;
            cin >> name >> type;
            service.subscribe(name, getChannelType(type));
        }
        else if (command == "UNSUBSCRIBE") {
            string name, type;
            cin >> name >> type;
            service.unsubscribe(name, getChannelType(type));
        }
        else if (command == "SEND") {
            string message;
            cin.ignore();
            getline(cin, message);
            service.sendNotification(message);
        }
    }

    return 0;
}