#include <bits/stdc++.h>
using namespace std;

enum class JumpType {
    SNAKE,
    LADDER
};

class Jump {
public:
    int start, end;
    JumpType type;

    Jump(int s, int e, JumpType t) : start(s), end(e), type(t) {}
};

class Player {
public:
    string name;
    int pos = 0;

    Player(string n) : name(n) {}
};

class Dice {
public:
    int roll() {
        return rand() % 6 + 1;
    }
};

class Board {
public:
    int size;
    unordered_map<int, Jump> jumps;

    Board(int n) : size(n) {}

    void addJump(int s, int e, JumpType t) {
        jumps[s] = Jump(s, e, t);
    }

    Jump* getJump(int pos) {
        if (jumps.count(pos)) return &jumps[pos];
        return nullptr;
    }
};

class Game {
    Board board;
    vector<Player> players;
    queue<int> turnOrder;
    Dice dice;
    bool isRunning = false;
    string winner;

public:
    Game(int boardSize) : board(boardSize) {}

    void addJump(int s, int e, JumpType type) {
        if (type == JumpType::LADDER && s < e)
            board.addJump(s, e, type);
        else if (type == JumpType::SNAKE && s > e)
            board.addJump(s, e, type);
    }

    void addPlayer(string name) {
        players.emplace_back(name);
        turnOrder.push(players.size() - 1);
    }

    void start() {
        isRunning = true;
        cout << "Game started\n";

        while (isRunning) {
            int idx = turnOrder.front();
            turnOrder.pop();

            Player& p = players[idx];
            bool extraTurn = true;

            while (extraTurn && isRunning) {
                int roll = dice.roll();
                extraTurn = (roll == 6);
                int next = p.pos + roll;
                cout << p.name << " rolled " << roll;
                if (next > board.size) {
                    cout << " stays at " << p.pos << "\n";
                    continue;
                }
                cout << " moved to " << next;

                if (next == board.size) {
                    cout << "\n";
                    winner = p.name;
                    isRunning = false;
                    break;
                }

                Jump* jump = board.getJump(next);
                int finalPos = next;

                if (jump) {
                    finalPos = jump->end;

                    if (jump->type == JumpType::LADDER)
                        cout << " Ladder to " << finalPos;
                    else
                        cout << " Snake to " << finalPos;
                }

                cout << "\n";
                p.pos = finalPos;
            }

            if (isRunning)
                turnOrder.push(idx);
        }
    }

    void printResult() {
        if (!winner.empty()) {
            cout << "Winner: " << winner << "\n";
        }
    }
};

int main() {
//    srand(time(0));
    Game game(100);

    // Add snakes
    game.addJump(99, 10, JumpType::SNAKE);
    game.addJump(70, 40, JumpType::SNAKE);
    game.addJump(52, 42, JumpType::SNAKE);
    game.addJump(25, 5, JumpType::SNAKE);

    // Add ladders
    game.addJump(2, 23, JumpType::LADDER);
    game.addJump(8, 34, JumpType::LADDER);
    game.addJump(20, 77, JumpType::LADDER);
    game.addJump(32, 68, JumpType::LADDER);

    // Add players
    game.addPlayer("Kapil");
    game.addPlayer("Aman");

    // Start game
    game.start();

    // Print result
    game.printResult();

    return 0;
}