#include <iostream>
#include <vector>
#include <deque>
#include <cstdlib>
#include <ctime>

using namespace std;

enum class TokenState {
    HOME,
    IN_PLAY,
    FINISHED
};

class Token {

public:
    string id;
    TokenState state;
    int position;

    Token(string id) {
        this->id = id;
        state = TokenState::HOME;
        position = -1;
    }

    bool canMove(int roll) {

        if(state == TokenState::FINISHED)
            return false;

        if(state == TokenState::HOME)
            return roll == 6;

        return position + roll <= 52;
    }

    void move(int roll) {

        if(state == TokenState::HOME && roll == 6) {

            state = TokenState::IN_PLAY;
            position = 0;

            cout << "  " << id << " entered board\n";
        }

        else if(state == TokenState::IN_PLAY) {

            position += roll;

            cout << "  " << id
                 << " -> position "
                 << position << endl;

            if(position >= 52) {

                state = TokenState::FINISHED;

                cout << "  " << id
                     << " finished!\n";
            }
        }
    }
};


class Player {

public:
    string name;
    vector<Token> tokens;

    Player(string name, int tokenCount) {

        this->name = name;

        for(int i = 0; i < tokenCount; i++) {

            tokens.push_back(
                Token(name + "-T" + to_string(i+1))
            );
        }
    }

    bool hasWon() {

        for(auto &t : tokens)
            if(t.state != TokenState::FINISHED)
                return false;

        return true;
    }

    int selectToken(int roll) {

        for(int i = 0; i < tokens.size(); i++)
            if(tokens[i].canMove(roll))
                return i;

        return -1;
    }
};


class Dice {

public:

    int roll() {
        return rand() % 6 + 1;
    }
};

/* ===================== GAME ===================== */

class LudoGame {

private:

    Dice dice;
    deque<Player> players;
    int turnCount;

public:

    LudoGame(vector<Player> playerList) {

        turnCount = 0;

        for(auto &p : playerList)
            players.push_back(p);
    }

    void start() {

        cout << "=== Ludo Game Started ===\n\n";

        while(turnCount < 100) {

            Player current = players.front();
            players.pop_front();

            turnCount++;

            int roll = dice.roll();

            cout << current.name
                 << " rolled "
                 << roll
                 << endl;

            int tokenIndex =
                current.selectToken(roll);

            if(tokenIndex != -1) {

                current.tokens[tokenIndex].move(roll);
            }
            else {

                cout << "  No movable tokens\n";
            }

            if(current.hasWon()) {

                cout << "\n🏆 "
                     << current.name
                     << " WINS!\n";

                break;
            }

            if(roll == 6) {

                cout << "  Rolled 6! Extra turn!\n";

                players.push_front(current);
            }
            else {

                players.push_back(current);
            }

            cout << endl;
        }

        cout << "Game ended after "
             << turnCount
             << " turns\n";
    }
};


int main() {

    srand(time(0));

    vector<Player> players;

    players.push_back(Player("Alice",2));
    players.push_back(Player("Bob",2));

    LudoGame game(players);

    game.start();

    return 0;
}