#include <bits/stdc++.h>
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
    int start;
    int end;
    JumpType type;

public:
    Jump(int s,int e) : start(s), end(e) {
        type = (s < e) ? JumpType::LADDER : JumpType::SNAKE;
    }

    int getStart() const { return start; }
    int getEnd() const { return end; }
    JumpType getType() const { return type; }
};

class Player {
    string name;
    int position;

public:
    Player(string name) : name(name), position(0) {}
    string getName() const { return name; }
    int getPosition() const { return position; }
    void setPosition(int pos) { position = pos; }
};

class Dice {

public:
    int roll() {
        return rand()%6 + 1;
    }

    bool isSix(int r) {
        return r==6;
    }
};

class Board {

    int size;
    unordered_map<int,int> jumps;

public:

    Board(int size) : size(size) {}

    void addJump(int start,int end) {
        jumps[start] = end;
    }

    int getSize() const {
        return size;
    }

    int applyJump(int position) {
        if(jumps.count(position))
            return jumps[position];
        return position;
    }
};

class Game {
    Board board;
    Dice dice;
    queue<shared_ptr<Player>> players;
    GameState state;

public:

    Game(Board board, vector<shared_ptr<Player>> playerList)
        : board(board) {
        state = GameState::NOT_STARTED;
        for(auto &p : playerList)
            players.push(p);
    }

    void start() {
        state = GameState::RUNNING;
        while(state == GameState::RUNNING) {
            auto player = players.front();
            players.pop();
            bool extraTurn = true;
            while(extraTurn && state==GameState::RUNNING) {
                extraTurn = executeTurn(player);
            }

            if(state==GameState::RUNNING)
                players.push(player);
        }
    }

private:

    bool executeTurn(shared_ptr<Player> player) {
        int roll = dice.roll();
        int current = player->getPosition();
        int next = current + roll;

        cout<<player->getName()<<" rolled "<<roll;

        if(next > board.getSize()) {
            cout<<" -> stays at "<<current<<"\n";
            return dice.isSix(roll);
        }

        cout<<" -> moved to "<<next;
        int finalPos = board.applyJump(next);
        if(finalPos > next)
            cout<<" -> Ladder to "<<finalPos;
        else if(finalPos < next)
            cout<<" -> Snake to "<<finalPos;
        cout<<"\n";

        player->setPosition(finalPos);
        if(finalPos == board.getSize()) {
            cout<<"\nWinner: "<<player->getName()<<"\n";
            state = GameState::FINISHED;
        }
        return dice.isSix(roll);
    }
};

int main() {
    string command;
    int boardSize;
    vector<pair<int,int>> jumpInputs;
    vector<shared_ptr<Player>> players;

    while(cin >> command) {
        if(command=="INIT") {
            cin>>boardSize;
        }
        else if(command=="JUMP") {
            int x,y;
            cin>>x>>y;
            jumpInputs.push_back({x,y});
        }
        else if(command=="PLAYER") {
            string name;
            cin>>name;
            players.push_back(make_shared<Player>(name));
        }
        else if(command=="START") {
            Board board(boardSize);
            for(auto &j : jumpInputs)
                board.addJump(j.first,j.second);
            Game game(board,players);
            game.start();
            break;
        }
    }
    return 0;
}


/*
INIT 100
JUMP 4 14
JUMP 9 31
JUMP 17 7
JUMP 28 84
JUMP 64 60
PLAYER Alice
PLAYER Bob
START
*/