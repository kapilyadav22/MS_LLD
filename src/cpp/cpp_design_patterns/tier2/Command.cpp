#include <iostream>
#include <memory>
using namespace std;

class Command {
public:
    virtual void execute() = 0;
};

class Light {
public:
    void turnOn() {
        cout << "Light ON\n";
    }
};

class LightOnCommand : public Command {
    Light* light;

public:
    LightOnCommand(Light* light) {
        this->light = light;
    }

    void execute() override {
        light->turnOn();
    }
};

int main() {

    Light light;

    LightOnCommand cmd(&light);

    cmd.execute();
}

/*
Remote control
Undo/Redo
Queue of commands


When to use
    queue of operations
    undo/redo
    task scheduling

Examples:
    Remote control
    Game actions
    Transaction commands
*/