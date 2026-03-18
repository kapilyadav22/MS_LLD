// #include <iostream>
// #include <unordered_map>

// #include <string>
#include<bits/stdc++.h>
using namespace std;

class Node {
public:
    int key;
    int value;
    Node* prev;
    Node* next;

    Node(int k, int v) : key(k), value(v), prev(nullptr), next(nullptr) {}
};

class LRUCache {
private:
    int capacity;
    unordered_map<int, Node*> map;
    Node* head;
    Node* tail;

public:
    LRUCache(int cap) {
        capacity = cap;
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head->next = tail;
        tail->prev = head;
    }

    ~LRUCache() {
    Node* cur = head;
    while(cur){
        Node* next = cur->next;
        delete cur;
        cur = next;
    }
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

    int get(int key) {
        if (map.find(key) == map.end())
            return -1;
        Node* node = map[key];
        moveToFront(node);
        return node->value;
    }

    void put(int key, int value) {
        if (map.find(key) != map.end()) {
            Node* node = map[key];
            node->value = value;
            moveToFront(node);
            return;
        }

        if (map.size() == capacity) {
            Node* lru = tail->prev;
            map.erase(lru->key);
            removeNode(lru);
            delete lru;
        }

        Node* node = new Node(key, value);
        addAfterHead(node);
        map[key] = node;
    }
};

int main() {
    string command;
    LRUCache* cache = nullptr;

    while (cin >> command) {
        if (command == "INIT") {
            int capacity;
            cin >> capacity;

            cache = new LRUCache(capacity);
        }  else if (command == "PUT") {
            int key, value;
            cin >> key >> value;
            
            /*
                if key values is in like 1:2 4,5 (take delimeter)
                char delimeter;
                int value;
                cin>>key>>delimeter>>value;
            */
            cache->put(key, value);
        }
        else if (command == "GET") {
            int key;
            cin >> key;
            cout << cache->get(key) << endl;
        }
        else if (command == "END") {
            break;
        }
    }

    return 0;
}