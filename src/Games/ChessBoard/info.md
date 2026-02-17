
Entities:
    Game
    Board
    Cell
    Piece (abstract)
    ├── King
    ├── Queen
    ├── Rook
    ├── Bishop
    ├── Knight
    └── Pawn
    Player
    Move
    Position

🧪 Follow-up Questions (Microsoft LOVES these)

    1️⃣ How would you add Check / Checkmate?
        Add isKingInCheck(Board, Player)
        Simulate move → validate king safety
    
    2️⃣ How to support Castling / En Passant?
        Extend Move with metadata
        Add rule engines per move type
    
    3️⃣ How to make it thread-safe?
        Synchronize makeMove
        Or single-threaded game loop
    
    4️⃣ How to persist game state?
        Serialize Board
        Store moves as events (event sourcing)

