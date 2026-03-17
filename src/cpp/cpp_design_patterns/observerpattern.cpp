/*
YouTube subscribers
Stock price updates
Notification system
Event listeners
*/

#include <iostream>
#include <vector>
#include <memory>

using namespace std;

class Observer {
public:
    virtual void update(string message) = 0;
};

class User : public Observer {
    string name;

public:
    User(string name) : name(name) {}

    void update(string message) override {
        cout << name << " received: " << message << endl;
    }
};

class Channel {
    vector<shared_ptr<Observer>> subscribers;

public:
    void subscribe(shared_ptr<Observer> user) {
        subscribers.push_back(user);
    }

    void notify(string message) {
        for(auto &user : subscribers)
            user->update(message);
    }
};

int main() {

    Channel channel;

    auto u1 = make_shared<User>("Kapil");
    auto u2 = make_shared<User>("Rahul");

    channel.subscribe(u1);
    channel.subscribe(u2);

    channel.notify("New Video Uploaded");
}