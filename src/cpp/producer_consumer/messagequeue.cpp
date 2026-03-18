#include <bits/stdc++.h>
using namespace std;

/************** Message *****************/
class Message {
public:
    int id;
    string payload;

    Message(int id, string payload) : id(id), payload(payload) {}
};

/************** Topic *****************/
class Topic {
private:
    string name;
    vector<shared_ptr<Message>> messages;
    mutex mtx;

public:
    Topic(string name) : name(name) {}

    void publish(shared_ptr<Message> msg) {
        lock_guard<mutex> lock(mtx);
        messages.push_back(msg);
    }

    shared_ptr<Message> getMessage(int offset) {
        lock_guard<mutex> lock(mtx);
        if (offset < messages.size()) return messages[offset];
        return nullptr;
    }

    int size() {
        lock_guard<mutex> lock(mtx);
        return messages.size();
    }

    string getName() { return name; }
};

/************** Consumer *****************/
class Consumer {
private:
    string name;
    unordered_map<string, int> offsets; // topic -> offset

public:
    Consumer(string name) : name(name) {}

    void consume(shared_ptr<Topic> topic) {
        string tname = topic->getName();

        if (offsets.find(tname) == offsets.end())
            offsets[tname] = 0;

        int offset = offsets[tname];
        auto msg = topic->getMessage(offset);

        if (msg) {
            cout << name << " consumed from " << tname
                 << ": " << msg->payload << "\n";
            offsets[tname]++;
        } else {
            cout << name << " no new messages in " << tname << "\n";
        }
    }
};

/************** Producer *****************/
class Producer {
private:
    string name;

public:
    Producer(string name) : name(name) {}

    void publish(shared_ptr<Topic> topic, int id, string payload) {
        auto msg = make_shared<Message>(id, payload);
        topic->publish(msg);

        cout << name << " published to " << topic->getName()
             << ": " << payload << "\n";
    }
};

/************** MessageQueue System *****************/
class MessageQueue {
private:
    unordered_map<string, shared_ptr<Topic>> topics;

public:
    shared_ptr<Topic> createTopic(string name) {
        if (topics.find(name) == topics.end()) {
            topics[name] = make_shared<Topic>(name);
        }
        return topics[name];
    }

    shared_ptr<Topic> getTopic(string name) {
        if (topics.find(name) != topics.end())
            return topics[name];
        return nullptr;
    }
};

/************** MAIN *****************/
int main() {
    ios::sync_with_stdio(false);
    cin.tie(NULL);

    MessageQueue mq;
    unordered_map<string, shared_ptr<Producer>> producers;
    unordered_map<string, shared_ptr<Consumer>> consumers;

    int q;
    cin >> q;

    while (q--) {
        string cmd;
        cin >> cmd;

        if (cmd == "CREATE_TOPIC") {
            string topic;
            cin >> topic;
            mq.createTopic(topic);
            cout << "Topic created: " << topic << "\n";
        }

        else if (cmd == "CREATE_PRODUCER") {
            string name;
            cin >> name;
            producers[name] = make_shared<Producer>(name);
            cout << "Producer created: " << name << "\n";
        }

        else if (cmd == "CREATE_CONSUMER") {
            string name;
            cin >> name;
            consumers[name] = make_shared<Consumer>(name);
            cout << "Consumer created: " << name << "\n";
        }

        else if (cmd == "PUBLISH") {
            string pname, topic, payload;
            int id;
            cin >> pname >> topic >> id >> payload;

            auto t = mq.getTopic(topic);
            if (t && producers.count(pname)) {
                producers[pname]->publish(t, id, payload);
            }
        }

        else if (cmd == "CONSUME") {
            string cname, topic;
            cin >> cname >> topic;

            auto t = mq.getTopic(topic);
            if (t && consumers.count(cname)) {
                consumers[cname]->consume(t);
            }
        }
    }

    return 0;
}