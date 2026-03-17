#include <iostream>
#include <memory>
#include <vector>

using namespace std;

class Board {
public:
    void show() {
        cout << "Board displayed\n";
    }
};

class Player {

private:
    shared_ptr<Board> board;

public:

    Player(shared_ptr<Board> board) {
        this->board = board;
    }

    void play() {
        board->show();
    }
};

int main() {
    auto board = make_shared<Board>();
    Player p1(board);
    Player p2(board);

}