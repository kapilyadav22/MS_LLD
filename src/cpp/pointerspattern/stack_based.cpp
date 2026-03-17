#include <iostream>

using namespace std;

class Light {
public:
    void on() {
        cout << "Light ON\n";
    }
};

class Command {

private:
    Light& light;

public:
    Command(Light& light) : light(light) {}

    void execute() {
        light.on();
    }
};

int main() {
    Light light;
    Command cmd(light);
    cmd.execute();
}

