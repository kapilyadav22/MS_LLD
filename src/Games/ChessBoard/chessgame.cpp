#include <bits/stdc++.h>
using namespace std;

class Position {
public:
    int row;
    int col;

    Position(int r, int c) : row(r), col(c) {}
};

class Player {
public:
    string name;
    bool isWhite;

    Player(string name, bool isWhite) : name(name), isWhite(isWhite) {}
};

class Piece {
protected:
    bool isWhite;

public:
    Piece(bool isWhite) : isWhite(isWhite) {}

    virtual bool isValidMove(Board &board, Position from, Position to) = 0;

    bool getColor() {
        return isWhite;
    }

    virtual ~Piece() {}
};


class Cell {
public:
    Position position;
    shared_ptr<Piece> piece;

    Cell(int r, int c) : position(r, c), piece(nullptr) {}

    bool isEmpty() {
        return piece == nullptr;
    }
};


class Board {
private:
    static const int SIZE = 8;
    vector<vector<shared_ptr<Cell>>> cells;

public:
    Board() {
        cells.resize(SIZE, vector<shared_ptr<Cell>>(SIZE));

        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                cells[i][j] = make_shared<Cell>(i,j);
            }
        }
    }

    shared_ptr<Cell> getCell(Position pos){
        if(pos.row <0 || pos.row>=SIZE || pos.col<0 || pos.col>=SIZE)
            return nullptr;

        return cells[pos.row][pos.col];
    }

    void placePiece(int r,int c, shared_ptr<Piece> piece){
        cells[r][c]->piece = piece;
    }
};

class Rook : public Piece {
public:
    Rook(bool isWhite) : Piece(isWhite) {}

    bool isValidMove(Board &board, Position from, Position to) override {
        return from.row == to.row || from.col == to.col;
    }
};

class Knight : public Piece {
public:
    Knight(bool isWhite) : Piece(isWhite) {}

    bool isValidMove(Board &board, Position from, Position to) override {

        int dr = abs(from.row - to.row);
        int dc = abs(from.col - to.col);

        return (dr==2 && dc==1) || (dr==1 && dc==2);
    }
};

class Pawn : public Piece {
public:
    Pawn(bool isWhite) : Piece(isWhite) {}

    bool isValidMove(Board &board, Position from, Position to) override {

        int direction = isWhite ? -1 : 1;

        if(from.col == to.col && (to.row-from.row)==direction)
            return true;

        return false;
    }
};

class Move {
public:
    shared_ptr<Player> player;
    Position from;
    Position to;

    Move(shared_ptr<Player> p, Position f, Position t)
        : player(p), from(f), to(t) {}
};

class Game {
private:
    Board board;
    shared_ptr<Player> whitePlayer;
    shared_ptr<Player> blackPlayer;
    shared_ptr<Player> currentTurn;

public:

    Game(shared_ptr<Player> w, shared_ptr<Player> b)
        : whitePlayer(w), blackPlayer(b), currentTurn(w) {

        initializeBoard();
    }

    void initializeBoard(){

        board.placePiece(0,0, make_shared<Rook>(false));
        board.placePiece(0,7, make_shared<Rook>(false));

        board.placePiece(7,0, make_shared<Rook>(true));
        board.placePiece(7,7, make_shared<Rook>(true));

        board.placePiece(0,1, make_shared<Knight>(false));
        board.placePiece(0,6, make_shared<Knight>(false));

        board.placePiece(7,1, make_shared<Knight>(true));
        board.placePiece(7,6, make_shared<Knight>(true));

        for(int i=0;i<8;i++){
            board.placePiece(1,i, make_shared<Pawn>(false));
            board.placePiece(6,i, make_shared<Pawn>(true));
        }
    }

    bool makeMove(Move move){

        auto fromCell = board.getCell(move.from);
        auto toCell = board.getCell(move.to);

        if(!fromCell || fromCell->isEmpty())
            return false;

        auto piece = fromCell->piece;

        if(piece->getColor() != currentTurn->isWhite)
            return false;

        if(!piece->isValidMove(board, move.from, move.to))
            return false;

        toCell->piece = piece;
        fromCell->piece = nullptr;

        switchTurn();

        return true;
    }

    void switchTurn(){
        if(currentTurn == whitePlayer)
            currentTurn = blackPlayer;
        else
            currentTurn = whitePlayer;
    }
};

int main(){
    auto white = make_shared<Player>("White", true);
    auto black = make_shared<Player>("Black", false);

    Game game(white, black);

    int moves;
    cin >> moves;

    while(moves--){

        int fr,fc,tr,tc;
        string player;

        cin >> player >> fr >> fc >> tr >> tc;

        shared_ptr<Player> p = (player=="W") ? white : black;

        Move move(p, Position(fr,fc), Position(tr,tc));

        bool result = game.makeMove(move);

        cout << (result ? "VALID\n" : "INVALID\n");
    }
}