/*
1. BASIC FILE SYSTEM (LLD)

Features
Create File
Create Directory
Read File
List Directory
Write File

*/

#include <bits/stdc++.h>
using namespace std;

class Node {
public:
    string name;
    bool isFile;

    Node(string name, bool isFile) : name(name), isFile(isFile) {}
    virtual ~Node() {}
};

class File : public Node {
public:
    string content;

    File(string name) : Node(name, true) {}

    void write(string data) {
        content = data;
    }

    string read() {
        return content;
    }
};

class Directory : public Node {
public:
    unordered_map<string, shared_ptr<Node>> children;

    Directory(string name) : Node(name, false) {}

    void add(shared_ptr<Node> node) {
        children[node->name] = node;
    }

    shared_ptr<Node> get(string name) {
        if (children.find(name) != children.end())
            return children[name];
        return nullptr;
    }

    vector<string> list() {
        vector<string> res;
        for (auto &p : children)
            res.push_back(p.first);
        sort(res.begin(), res.end());
        return res;
    }
};

class FileSystem {
    shared_ptr<Directory> root;

    vector<string> split(string path) {
        vector<string> tokens;
        string temp;
        for (char c : path) {
            if (c == '/') {
                if (!temp.empty()) {
                    tokens.push_back(temp);
                    temp.clear();
                }
            } else temp.push_back(c);
        }
        if (!temp.empty()) tokens.push_back(temp);
        return tokens;
    }

    shared_ptr<Directory> traverseDir(string path) {
        auto tokens = split(path);
        auto curr = root;

        for (auto &t : tokens) {
            auto next = curr->get(t);
            if (!next || next->isFile) return nullptr;
            curr = static_pointer_cast<Directory>(next);
        }
        return curr;
    }

public:
    FileSystem() {
        root = make_shared<Directory>("/");
    }

    void mkdir(string path) {
        auto tokens = split(path);
        auto curr = root;

        for (auto &t : tokens) {
            if (!curr->get(t)) {
                curr->add(make_shared<Directory>(t));
            }
            curr = static_pointer_cast<Directory>(curr->get(t));
        }
    }

    void createFile(string path) {
        auto tokens = split(path);
        string fileName = tokens.back();
        tokens.pop_back();

        auto dir = root;
        for (auto &t : tokens) {
            if (!dir->get(t)) {
                dir->add(make_shared<Directory>(t));
            }
            dir = static_pointer_cast<Directory>(dir->get(t));
        }

        dir->add(make_shared<File>(fileName));
    }

    void writeFile(string path, string content) {
        auto tokens = split(path);
        string fileName = tokens.back();
        tokens.pop_back();

        auto dir = traverseDir("/" + accumulate(tokens.begin(), tokens.end(), string(),
                      [](string a, string b) { return a + "/" + b; }));

        auto file = static_pointer_cast<File>(dir->get(fileName));
        file->write(content);
    }

    void readFile(string path) {
        auto tokens = split(path);
        string fileName = tokens.back();
        tokens.pop_back();

        auto dir = traverseDir("/" + accumulate(tokens.begin(), tokens.end(), string(),
                      [](string a, string b) { return a + "/" + b; }));

        auto file = static_pointer_cast<File>(dir->get(fileName));
        cout << file->read() << endl;
    }

    void ls(string path) {
        auto dir = traverseDir(path);
        auto res = dir->list();
        for (auto &x : res) cout << x << " ";
        cout << endl;
    }
};

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);

    FileSystem fs;
    int q;
    cin >> q;

    while (q--) {
        string cmd;
        cin >> cmd;

        if (cmd == "mkdir") {
            string path;
            cin >> path;
            fs.mkdir(path);
        } else if (cmd == "create") {
            string path;
            cin >> path;
            fs.createFile(path);
        } else if (cmd == "write") {
            string path, content;
            cin >> path >> content;
            fs.writeFile(path, content);
        } else if (cmd == "read") {
            string path;
            cin >> path;
            fs.readFile(path);
        } else if (cmd == "ls") {
            string path;
            cin >> path;
            fs.ls(path);
        }
    }
}