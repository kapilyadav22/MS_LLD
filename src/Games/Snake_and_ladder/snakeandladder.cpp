#include <iostream>
#include <vector>
#include <queue>
#include <unordered_map>
#include <memory>
#include <random>

using namespace std;


enum class JumpType {
    SNAKE,
    LADDER
};

enum class GameState {
    NOT_STARTED,
    RUNNING,
    FINISHED
};

class Jump {

private:
    int start;
    int end;
    JumpType type;

public:

    Jump(int start, int end, JumpType type) {
        this->start = start;
        this->end = end;
        this->type = type;
    }

    int getStart() const { return start; }
    int getEnd() const { return end; }
    JumpType getType() const { return type; }
};

class Player {

private:
    string name;
    int position;

public:

    Player(string name) {
        this->name = name;
        this->position = 0;
    }

    string getName() const { return name; }

    int getPosition() const { return position; }

    void setPosition(int pos) {
        position = pos;
    }
};

class Dice {

private:
    random_device rd;
    mt19937 gen;
    uniform_int_distribution<> dist;

public:

    Dice() : gen(rd()), dist(1,6) {}

    int roll() {
        return dist(gen);
    }

    bool isSix(int roll) {
        return roll == 6;
    }
};


class Board {

private:
    int size;
    unordered_map<int, Jump> jumps;

public:

    Board(int size, vector<Jump> jumpList) {

        this->size = size;

        for (Jump &jump : jumpList) {
            jumps.insert({jump.getStart(), jump});
        }
    }

    int getSize() const {
        return size;
    }

    bool hasJump(int position) {

        return jumps.find(position) != jumps.end();
    }

    Jump getJump(int position) {

        return jumps.at(position);
    }
};


class Game {

private:

    Board board;
    Dice dice;
    queue<shared_ptr<Player>> players;
    GameState state;

public:

    Game(Board board, vector<shared_ptr<Player>> playerList)
        : board(board) {

        state = GameState::NOT_STARTED;

        for (auto &p : playerList) {
            players.push(p);
        }
    }

    void start() {

        if (players.size() < 2) {

            cout << "Not enough players to start\n";
            return;
        }

        state = GameState::RUNNING;

        while (state == GameState::RUNNING) {

            auto current = players.front();
            players.pop();

            bool extraTurn = true;

            while (extraTurn && state == GameState::RUNNING) {

                extraTurn = executeTurn(current);
            }

            if (state == GameState::RUNNING) {

                players.push(current);
            }
        }
    }

private:

    bool executeTurn(shared_ptr<Player> player) {

        int roll = dice.roll();

        int currentPos = player->getPosition();
        int nextPos = currentPos + roll;

        cout << player->getName()
             << " rolled "
             << roll;

        if (nextPos > board.getSize()) {

            cout << " -> exceeds board, stays at "
                 << currentPos
                 << endl;

            return dice.isSix(roll);
        }

        cout << " -> landed on "
             << nextPos;

        applyMove(player, nextPos);

        checkWin(player);

        if (state == GameState::RUNNING && dice.isSix(roll)) {

            cout << player->getName()
                 << " rolled a 6 and gets another turn!\n";
        }

        return dice.isSix(roll);
    }

    void applyMove(shared_ptr<Player> player, int nextPos) {

        if (board.hasJump(nextPos)) {

            Jump jump = board.getJump(nextPos);

            player->setPosition(jump.getEnd());

            if (jump.getType() == JumpType::SNAKE) {

                cout << " -> Snake! Slide to "
                     << jump.getEnd()
                     << endl;
            }

            else {

                cout << " -> Ladder! Climb to "
                     << jump.getEnd()
                     << endl;
            }
        }

        else {

            player->setPosition(nextPos);

            cout << " -> Reached "
                 << player->getPosition()
                 << endl;
        }
    }

    void checkWin(shared_ptr<Player> player) {

        if (player->getPosition() == board.getSize()) {

            cout << "\n🎉 "
                 << player->getName()
                 << " wins!\n";

            state = GameState::FINISHED;
        }
    }
};


int main() {

    vector<Jump> jumps;

    jumps.push_back(Jump(4, 14, JumpType::LADDER));
    jumps.push_back(Jump(9, 31, JumpType::LADDER));
    jumps.push_back(Jump(20, 38, JumpType::LADDER));
    jumps.push_back(Jump(28, 84, JumpType::LADDER));

    jumps.push_back(Jump(17, 7, JumpType::SNAKE));
    jumps.push_back(Jump(64, 60, JumpType::SNAKE));
    jumps.push_back(Jump(89, 26, JumpType::SNAKE));

    Board board(100, jumps);

    vector<shared_ptr<Player>> players;

    players.push_back(make_shared<Player>("Alice"));
    players.push_back(make_shared<Player>("Bob"));

    Game game(board, players);

    game.start();

    return 0;
}