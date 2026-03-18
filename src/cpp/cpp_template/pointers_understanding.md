1 For runtime object creation, use unique_ptr<> in manager;
2 If objects are used by particular class and are simple and stored in vector or unordered_map only or
is initialised at the time of declaration only, use stack based approach


    like :    unordered_map<int, Jump> jumps;
   
        Jump* getJump(int pos) {
          if (jumps.count(pos)) return &jumps[pos];
          return nullptr;
        }

    Jump jump(p1,p2,p3..);

Think: “Who owns this object?”

3 If need to create any object in any class (runtime): then use shared/unique ptr.
    like Game has board

4 If unique ptr is used, and we need to pass it, either use move() like 

    class Game {
        public:
        Game(unique_ptr<Board> board, vector<shared_ptr<Player>> players)
        : board(move(board)), players(players) {}
    };

5 shared ptr can be pass without move (can have multiple owners)

6 if a class doesn't own another class like Ride doesnt own drivers,
then use Driver* driver;



NOTE: use 1,2,6 majorly



