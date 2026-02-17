import java.util.*;

/* ===================== ENUMS ===================== */

enum JumpType {
    SNAKE,
    LADDER
}

enum GameState {
    NOT_STARTED,
    RUNNING,
    FINISHED
}

/* ===================== JUMP ===================== */

class Jump {
    private final int start;
    private final int end;
    private final JumpType type;

    Jump(int start, int end, JumpType type) {
        this.start = start;
        this.end = end;
        this.type = type;
    }

    int getStart() { return start; }
    int getEnd() { return end; }
    JumpType getType() { return type; }
}

/* ===================== PLAYER ===================== */

class Player {
    private final String name;
    private int position;

    Player(String name) {
        this.name = name;
        this.position = 0;
    }

    String getName() { return name; }
    int getPosition() { return position; }
    void setPosition(int position) { this.position = position; }
}

/* ===================== DICE ===================== */

class Dice {
    private final Random random = new Random();

    int roll() {
        return random.nextInt(6) + 1;
    }

    boolean isSix(int roll) {
        return roll == 6;
    }
}

/* ===================== BOARD ===================== */

class Board {
    private final int size;
    private final Map<Integer, Jump> jumps;

    Board(int size, List<Jump> jumpList) {
        this.size = size;
        this.jumps = new HashMap<>();
        for (Jump jump : jumpList) {
            jumps.put(jump.getStart(), jump);
        }
    }

    int getSize() { return size; }

    boolean hasJump(int position) {
        return jumps.containsKey(position);
    }

    Jump getJump(int position) {
        return jumps.get(position);
    }
}

/* ===================== GAME ===================== */

class Game {
    private final Board board;
    private final Dice dice;
    private final Queue<Player> players;
    private GameState state;

    Game(Board board, List<Player> players) {
        this.board = board;
        this.dice = new Dice();
        this.players = new LinkedList<>(players);
        this.state = GameState.NOT_STARTED;
    }

    void start() {
        if (players.size() < 2) {
            System.out.println("Not enough players to start.");
            return;
        }

        state = GameState.RUNNING;

        while (state == GameState.RUNNING) {

            Player current = players.poll();

            boolean extraTurn = true;
            while (extraTurn && state == GameState.RUNNING) {
                extraTurn = executeTurn(current);
            }

            if (state == GameState.RUNNING) {
                players.offer(current);
            }
        }
    }

    private boolean executeTurn(Player player) {
        int roll = dice.roll();
        int currentPos = player.getPosition();
        int nextPos = currentPos + roll;

        System.out.print(player.getName() + " rolled " + roll);

        if (nextPos > board.getSize()) {
            System.out.println(" -> exceeds board, stays at " + currentPos);
            return dice.isSix(roll);
        }

        System.out.print(" -> landed on " + nextPos);

        applyMove(player, nextPos);
        checkWin(player);

        if (state == GameState.RUNNING && dice.isSix(roll)) {
            System.out.println(player.getName() + " rolled a 6 and gets another turn!");
        }

        return dice.isSix(roll);
    }

    private void applyMove(Player player, int nextPos) {
        if (board.hasJump(nextPos)) {
            Jump jump = board.getJump(nextPos);
            player.setPosition(jump.getEnd());

            if (jump.getType() == JumpType.SNAKE) {
                System.out.println(" -> Snake! Slide to " + jump.getEnd());
            } else {
                System.out.println(" -> Ladder! Climb to " + jump.getEnd());
            }
        } else {
            player.setPosition(nextPos);
            System.out.println(" -> Reached " + player.getPosition());
        }
    }

    private void checkWin(Player player) {
        if (player.getPosition() == board.getSize()) {
            System.out.println("\n🎉 " + player.getName() + " wins!");
            state = GameState.FINISHED;
        }
    }
}

/* ===================== MAIN ===================== */

public class SnakeAndLadderGame {

    public static void main(String[] args) {

        List<Jump> jumps = new ArrayList<>();
        jumps.add(new Jump(4, 14, JumpType.LADDER));
        jumps.add(new Jump(9, 31, JumpType.LADDER));
        jumps.add(new Jump(20, 38, JumpType.LADDER));
        jumps.add(new Jump(28, 84, JumpType.LADDER));
        jumps.add(new Jump(17, 7, JumpType.SNAKE));
        jumps.add(new Jump(64, 60, JumpType.SNAKE));
        jumps.add(new Jump(89, 26, JumpType.SNAKE));

        Board board = new Board(100, jumps);

        List<Player> players = new ArrayList<>();
        players.add(new Player("Alice"));
        players.add(new Player("Bob"));

        Game game = new Game(board, players);
        game.start();
    }
}
