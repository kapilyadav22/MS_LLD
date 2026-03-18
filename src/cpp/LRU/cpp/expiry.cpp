// #include <iostream>
// #include <unordered_map>
// #include <chrono>
// #include <string>

#include <bits/stdc++.h>


using namespace std;
using namespace std::chrono;

class Node {
public:
    int key;
    int value;
    long long expiry;
    Node* prev;
    Node* next;

    Node(int k, int v, long long exp)
        : key(k), value(v), expiry(exp), prev(nullptr), next(nullptr) {}
};

class LRUCache {

private:
    int capacity;
    unordered_map<int, Node*> cache;

    Node* head;
    Node* tail;

public:
    LRUCache(int cap) {
        capacity = cap;

        head = new Node(-1, -1, LLONG_MAX);
        tail = new Node(-1, -1, LLONG_MAX);

        head->next = tail;
        tail->prev = head;
    }

    long long now() {
        return duration_cast<milliseconds>(
            system_clock::now().time_since_epoch()
        ).count();
    }

    bool isExpired(Node* node) {
        return now() > node->expiry;
    }

    void addAfterHead(Node* node) {
        Node* first = head->next;

        head->next = node;
        node->prev = head;

        node->next = first;
        first->prev = node;
    }

    void removeNode(Node* node) {
        Node* p = node->prev;
        Node* n = node->next;

        p->next = n;
        n->prev = p;
    }

    void moveToFront(Node* node) {
        removeNode(node);
        addAfterHead(node);
    }

    void evictLRU() {
        Node* lru = tail->prev;

        if (lru == head)
            return;

        cache.erase(lru->key);
        removeNode(lru);
        delete lru;
    }

    int get(int key) {

        if (cache.find(key) == cache.end())
            return -1;

        Node* node = cache[key];

        if (isExpired(node)) {
            cache.erase(key);
            removeNode(node);
            delete node;
            return -1;
        }

        moveToFront(node);

        return node->value;
    }

    void put(int key, int value, int ttlSeconds) {

        long long expiry =
            now() + ttlSeconds * 1000LL;

        if (cache.find(key) != cache.end()) {

            Node* node = cache[key];

            node->value = value;
            node->expiry = expiry;

            moveToFront(node);
            return;
        }

        if (cache.size() == capacity)
            evictLRU();

        Node* node = new Node(key, value, expiry);

        addAfterHead(node);
        cache[key] = node;
    }
};

int main() {

    string cmd;
    LRUCache* cache = nullptr;

    while (cin >> cmd) {

        if (cmd == "INIT") {
            int cap;
            cin >> cap;
            cache = new LRUCache(cap);
        }

        else if (cmd == "PUT") {
            int key, value, ttl;
            cin >> key >> value >> ttl;

            cache->put(key, value, ttl);
        }

        else if (cmd == "GET") {
            int key;
            cin >> key;

            cout << cache->get(key) << endl;
        }

        else if (cmd == "END") {
            break;
        }
    }

    return 0;
}