#include <iostream>
#include <vector>
#include <unordered_map>
#include <memory>

using namespace std;

class InterfaceName {
public:
    virtual void execute() = 0;
    virtual ~InterfaceName() = default;
};

class ConcreteImplementation : public InterfaceName {
public:
    void execute() override {
        cout << "Implementation\n";
    }
};

class Manager {
private:
    unordered_map<string, shared_ptr<InterfaceName>> objects;

public:
    void add(string id, shared_ptr<InterfaceName> obj) {
        objects[id] = obj;
    }

    void run(string id) {
        objects[id]->execute();
    }
};

int main() {

    auto obj = make_shared<ConcreteImplementation>();

    Manager manager;
    manager.add("1", obj);

    manager.run("1");

    return 0;
}