package tictactoe;

import java.util.*;

class Player1 {
    private final String name;
    private final char symbol;

    Player1(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }
}

class Board1 {
    private final int size;
    private final char[][] grid;
    private int movesPlayed;


    Board1(int size) {
        this.size = size;
        grid = new char[size][size];
        this.movesPlayed = 0;

        for (int i = 0; i < size; i++) {
            Arrays.fill(grid[i], '-');
        }
    }

    public boolean placeMove(int row, int col, char symbol) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            return false;
        }

        if (grid[row][col] != '-') {
            return false;
        }

        grid[row][col] = symbol;
        movesPlayed++;

        return true;
    }

    public boolean checkWinner(int row, int col, char symbol) {
        boolean winRow = true;
        boolean winCol = true;

        // Check row and column
        for (int i = 0; i < size; i++) {
            if (grid[row][i] != symbol) {
                winRow = false;
            }

            if (grid[i][col] != symbol) {
                winCol = false;
            }

            //            if (!winRow && !winCol) {
            //                break;
            //            }
        }

        // If row or column already won
        if (winRow || winCol) {
            return true;
        }

        // Check main diagonal only if needed
        if (row == col) {
            boolean winDiag = true;

            for (int i = 0; i < size; i++) {
                if (grid[i][i] != symbol) {
                    winDiag = false;
                    break;
                }
            }

            if (winDiag) { return true;}
        }

        // Check anti-diagonal only if needed
        if (row + col == size - 1) {
            boolean winAntiDiag = true;

            for (int i = 0; i < size; i++) {
                if (grid[i][size - i - 1] != symbol) {
                    winAntiDiag = false;
                    break;
                }
            }

            if (winAntiDiag) { return true; }
        }
        return false;
    }

    public boolean isBoardFull() {
        return movesPlayed == size * size;
    }

    public void printBoard() {
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}

class TicTacToeGame {

    private final Board1 board;
    private final List<Player1> players;
    private int currentPlayerIndex;

    TicTacToeGame(int size) {
        board = new Board1(size);

        players = new ArrayList<>();
        players.add(new Player1("Player1", 'X'));
        players.add(new Player1("Player2", 'O'));

        currentPlayerIndex = 0;
    }

    public void startGame() {
         Scanner sc = new Scanner(System.in);

        while (true) {
            board.printBoard();
            Player1 currentPlayer = players.get(currentPlayerIndex);

            System.out.println(currentPlayer.getName() + "'s turn");

            int row = sc.nextInt();
            int col = sc.nextInt();

            boolean movePlaced = board.placeMove(
                    row,
                    col,
                    currentPlayer.getSymbol()
            );

            if (!movePlaced) {
                System.out.println("Invalid move");
                continue;
            }

            if (board.checkWinner(row, col, currentPlayer.getSymbol())) {
                board.printBoard();
                System.out.println(currentPlayer.getName() + " wins!");
                break;
            }

            if (board.isBoardFull()) {
                board.printBoard();
                System.out.println("Match Draw");
                break;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }
}

public class TicTacToe {
    public static void main(String[] args) {
        TicTacToeGame game = new TicTacToeGame(3);
        game.startGame();
    }
}