1. Clarify Requirements (Always say this first 💡)

Functional

    N players (2–6)
    Standard board (1 → 100)
    Snakes (down moves), Ladders (up moves)
    Dice roll (1–6)
    First player to reach exactly 100 wins

Non-Functional
    OOP, clean abstractions
    Easy to extend (multiple dice, custom board)
    Deterministic, testable


2. Core Design Objects (High Signal)

   | Concept            | Responsibility          |
   | ------------------ | ----------------------- |
   | `Game`             | Orchestrates turns      |
   | `Board`            | Holds snakes & ladders  |
   | `Player`           | Name + current position |
   | `Dice`             | Generates random moves  |
   | `Snake` / `Ladder` | Jump logic              |


3. Class Diagram (Textual UML – MS friendly)
+------------------+
|     Game         |
+------------------+
| Board board      |
| Queue<Player>    |
| Dice dice        |
+------------------+
| start()          |
| playTurn()       |
+------------------+

+------------------+
|     Board        |
+------------------+
| Map<Integer,Integer> jumps |
+------------------+
| getNextPos(pos)  |
+------------------+

+------------------+
|    Player        |
+------------------+
| String name      |
| int position     |
+------------------+

+------------------+
|     Dice         |
+------------------+
| roll()           |
+------------------+

⚠️ Key MS expectation: snakes & ladders are just jumps — no separate logic needed.
