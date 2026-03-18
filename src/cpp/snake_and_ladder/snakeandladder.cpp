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

    void addJump(int s, int e, string typeStr) {
        if (typeStr == "LADDER") {
            if (s >= e) return;
            board.addJump(s, e, JumpType::LADDER);
        } else if (typeStr == "SNAKE") {
            if (s <= e) return;
            board.addJump(s, e, JumpType::SNAKE);
        }
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

                if (jump != nullptr) {
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
        } else {
            cout << "Game in progress:\n";
            for (auto& p : players) {
                cout << p.name << ": " << p.pos << "\n";
            }
        }
    }
};

class GameManager {
    unique_ptr<Game> game;

public:
    void init(int size) {
        game = make_unique<Game>(size);
        cout << "Board initialized with size " << size << "\n";
    }

    void addJump(string type, int s, int e) {
        if (!game) return;

        if (type == "LADDER")
            cout << "Added Ladder from " << s << " to " << e << "\n";
        else
            cout << "Added Snake from " << s << " to " << e << "\n";

        game->addJump(s, e, type);
    }

    void addPlayer(string name) {
        if (!game) return;

        game->addPlayer(name);
        cout << "Player " << name << " added\n";
    }

    void startGame() {
        if (game)
            game->start();
    }

    void endGame() {
        if (game)
            game->printResult();
    }
};

int main() {
    GameManager manager;
    string command;

    while (cin >> command) {

        if (command == "INIT") {
            int size;
            cin >> size;
            manager.init(size);
        }

        else if (command == "JUMP") {
            string type;
            int s, e;
            cin >> type >> s >> e;
            manager.addJump(type, s, e);
        }

        else if (command == "PLAYER") {
            string name;
            cin >> name;
            manager.addPlayer(name);
        }

        else if (command == "START") {
            manager.startGame();
        }

        else if (command == "END") {
            manager.endGame();
            break;
        }
    }

    return 0;
}