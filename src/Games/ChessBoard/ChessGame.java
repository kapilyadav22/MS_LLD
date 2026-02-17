package Games.ChessBoard;

import java.util.*;

/**
 * Chess Game - Low Level Design

 Assumptions you should state:
 8×8 chessboard
     2 players (White, Black)
     Standard chess pieces
     Turn-based play
     Valid move checking
     No UI, no AI
 */


/* ======================= POSITION ======================= */

class Position {
    int row;
    int col;

    Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

/* ======================= PLAYER ======================= */

class Player {
    String name;
    boolean isWhite;

    Player(String name, boolean isWhite) {
        this.name = name;
        this.isWhite = isWhite;
    }
}

/* ======================= MOVE ======================= */

class Move {
    Player player;
    Position from;
    Position to;

    Move(Player player, Position from, Position to) {
        this.player = player;
        this.from = from;
        this.to = to;
    }
}

/* ======================= PIECES ======================= */

abstract class Piece {
    boolean isWhite;

    Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    abstract boolean isValidMove(Board board, Position from, Position to);
}

class Rook extends Piece {
    Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    boolean isValidMove(Board board, Position from, Position to) {
        return from.row == to.row || from.col == to.col;
    }
}

class Knight extends Piece {
    Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    boolean isValidMove(Board board, Position from, Position to) {
        int dr = Math.abs(from.row - to.row);
        int dc = Math.abs(from.col - to.col);
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }
}

class Pawn extends Piece {
    Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    boolean isValidMove(Board board, Position from, Position to) {
        int direction = isWhite ? -1 : 1;
        return from.col == to.col && (to.row - from.row) == direction;
    }
}

/* ======================= CELL ======================= */

class Cell {
    Position position;
    Piece piece;

    Cell(Position position) {
        this.position = position;
    }

    boolean isEmpty() {
        return piece == null;
    }
}

/* ======================= BOARD ======================= */

class Board {
    private static final int SIZE = 8;
    private final Cell[][] cells = new Cell[SIZE][SIZE];

    Board() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new Cell(new Position(i, j));
            }
        }
    }

    void initialize() {
        // Rooks
        cells[0][0].piece = new Rook(false);
        cells[0][7].piece = new Rook(false);
        cells[7][0].piece = new Rook(true);
        cells[7][7].piece = new Rook(true);

        // Knights
        cells[0][1].piece = new Knight(false);
        cells[0][6].piece = new Knight(false);
        cells[7][1].piece = new Knight(true);
        cells[7][6].piece = new Knight(true);

        // Pawns
        for (int i = 0; i < SIZE; i++) {
            cells[1][i].piece = new Pawn(false);
            cells[6][i].piece = new Pawn(true);
        }
    }

    Cell getCell(Position position) {
        if (position.row < 0 || position.row >= SIZE ||
                position.col < 0 || position.col >= SIZE) {
            return null;
        }
        return cells[position.row][position.col];
    }
}

/* ======================= GAME ======================= */

class Game {
    private final Board board;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private Player currentTurn;

    Game(Player whitePlayer, Player blackPlayer) {
        this.board = new Board();
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.currentTurn = whitePlayer;
        board.initialize();
    }

    public boolean makeMove(Move move) {
        Cell fromCell = board.getCell(move.from);
        Cell toCell = board.getCell(move.to);

        if (fromCell == null || fromCell.isEmpty()) return false;

        Piece piece = fromCell.piece;
        if (piece.isWhite != currentTurn.isWhite) return false;

        if (!piece.isValidMove(board, move.from, move.to)) return false;

        toCell.piece = piece;
        fromCell.piece = null;
        switchTurn();
        return true;
    }

    private void switchTurn() {
        currentTurn = (currentTurn == whitePlayer) ? blackPlayer : whitePlayer;
    }
}

/* ======================= MAIN ======================= */

public class ChessGame {
    public static void main(String[] args) {
        Player white = new Player("White", true);
        Player black = new Player("Black", false);

        Game game = new Game(white, black);

        Move move = new Move(white, new Position(6, 0), new Position(5, 0));
        System.out.println("Move success: " + game.makeMove(move));
    }
}