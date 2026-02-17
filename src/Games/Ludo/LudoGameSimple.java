package Games.Ludo;

import java.util.*;

/* ===================== ENUMS ===================== */

enum TokenState {
    HOME, IN_PLAY, FINISHED
}

/* ===================== MODELS ===================== */

class Token {
    String id;
    TokenState state = TokenState.HOME;
    int position = -1;  // -1=HOME, 0-51=track, 52+=finished

    Token(String id) {
        this.id = id;
    }

    boolean canMove(int roll) {
        if (state == TokenState.FINISHED) return false;
        if (state == TokenState.HOME) return roll == 6;
        return position + roll <= 52;
    }

    void move(int roll) {
        if (state == TokenState.HOME && roll == 6) {
            state = TokenState.IN_PLAY;
            position = 0;
            System.out.println("  " + id + " entered board");
        } else if (state == TokenState.IN_PLAY) {
            position += roll;
            System.out.println("  " + id + " -> position " + position);

            if (position >= 52) {
                state = TokenState.FINISHED;
                System.out.println("  " + id + " finished! 🏠");
            }
        }
    }
}

class Player {
    String name;
    List<Token> tokens = new ArrayList<>();

    Player(String name, int tokenCount) {
        this.name = name;
        for (int i = 0; i < tokenCount; i++) {
            tokens.add(new Token(name + "-T" + (i + 1)));
        }
    }

    boolean hasWon() {
        return tokens.stream().allMatch(t -> t.state == TokenState.FINISHED);
    }

    Token selectToken(int roll) {
        for (Token t : tokens) {
            if (t.canMove(roll)) return t;
        }
        return null;
    }
}

/* ===================== DICE ===================== */

class Dice {
    private Random random = new Random();

    int roll() {
        return random.nextInt(6) + 1;
    }
}

/* ===================== GAME ===================== */

class LudoGame {
    private Dice dice = new Dice();
    private Deque<Player> players;
    private int turnCount = 0;

    LudoGame(List<Player> playerList) {
        this.players = new LinkedList<>(playerList);
    }

    void start() {
        System.out.println("=== Ludo Game Started ===\n");

        while (turnCount < 100) {
            Player current = players.poll();
            turnCount++;

            int roll = dice.roll();
            System.out.println(current.name + " rolled " + roll);

            Token token = current.selectToken(roll);
            if (token != null) {
                token.move(roll);
            } else {
                System.out.println("  No movable tokens");
            }

            if (current.hasWon()) {
                System.out.println("\n🏆 " + current.name + " WINS!");
                break;
            }

            // Extra turn on 6
            if (roll == 6) {
                System.out.println("  Rolled 6! Extra turn!");
                players.addFirst(current);
            } else {
                players.offer(current);
            }

            System.out.println();
        }

        System.out.println("Game ended after " + turnCount + " turns");
    }
}

/* ===================== MAIN ===================== */

public class LudoGameSimple {
    public static void main(String[] args) {

        List<Player> players = new ArrayList<>();
        players.add(new Player("Alice", 2));
        players.add(new Player("Bob", 2));

        LudoGame game = new LudoGame(players);
        game.start();
    }
}

/*
===================== KEY FEATURES =====================

✅ Complete token lifecycle: HOME → IN_PLAY → FINISHED
✅ Proper win condition: All tokens reach finish
✅ Extra turn on rolling 6
✅ Token movement logic
✅ Simple and clean (~100 lines)

===================== TIME: 25 MINUTES =====================

- Enums + Token class: 5 min
- Player class: 5 min
- Dice + Game logic: 10 min
- Main + testing: 5 min

===================== WHAT TO DISCUSS =====================

"For production, I'd add:
- Capture opponent tokens
- Safe zones
- Token selection strategy
- Undo functionality"

*/