package game;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final List<Player> players;
    private final Board board;

    public GameManager(int rows, int columns) {
        players = new ArrayList<>();
        board = new Board(rows, columns);
    }

    public synchronized Player addPlayer() {
        String playerId = generatePlayerId();
        Player player = new Player(playerId);
        players.add(player);
        System.out.println("Player " + player.getId() + " agregado. Total de jugadores: " + players.size());
        return player;
    }

    public synchronized void removePlayer(String playerId) {
        Player player = findPlayerById(playerId);
        if (player != null) {
            players.remove(player);
            System.out.println("Player " + playerId + " eliminado. Total de jugadores: " + players.size());
        }
    }

    public synchronized Player findPlayerById(String playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    private synchronized String generatePlayerId() {
        char id = 'a';
        while(findPlayerById(String.valueOf(id)) != null) {
            id++;
        }
        return String.valueOf(id);
    }

    public synchronized int getPlayerCount() {
        return players.size();
    }

    public Board getBoard() {
        return board;
    }

    private Tetromino createRandomTetromino() {
        TetrominoType[] types = TetrominoType.values();
        int index = (int) (Math.random() * types.length);
        TetrominoType type = types[index];
        int row = 0;
        int col = (board.getColumns() / 2) - 1;
        return new Tetromino(type, row, col);
    }

    public synchronized boolean canPlaceTetromino(
            Tetromino tetromino,
            int targetRow,
            int targetColumn,
            boolean[][] targetShape,
            Player player
    ) {
        for (int localRow = 0; localRow < targetShape.length; localRow++) {
            for (int localColumn = 0; localColumn < targetShape[localRow].length; localColumn++) {
                // Las celdas false no ocupan espacio.
                if (!targetShape[localRow][localColumn]) {
                    continue;
                }

                int boardRow = targetRow + localRow;
                int boardColumn = targetColumn + localColumn;

                // 1. Fuera del tablero.
                if (!board.isInside(boardRow, boardColumn)) {
                    return false;
                }

                // 2. Colision con una pieza ya fijada.
                if (!board.isEmpty(boardRow, boardColumn)) {
                    return false;
                }

                // 3. Colision con otra pieza activa.
                if (isOccupiedByAnotherPlayer(boardRow, boardColumn, player)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isOccupiedByAnotherPlayer(int boardRow, int boardColumn, Player currentPlayer) {
        for (Player player : players) {
            if (player == currentPlayer) {
                continue;
            }

            Tetromino tetromino = player.getCurrentTetromino();

            if (tetromino == null) {
                continue;
            }

            boolean[][] shape = tetromino.getShape();

            int pieceRow = tetromino.getRow();
            int pieceColumn = tetromino.getColumn();

            for (int localRow = 0; localRow < shape.length; localRow++) {
                for (int localColumn = 0; localColumn < shape[localRow].length; localColumn++) {
                    // Nuevamente: false no ocupa espacio.
                    if (!shape[localRow][localColumn]) {
                        continue;
                    }

                    int otherBoardRow = pieceRow + localRow;
                    int otherBoardColumn = pieceColumn + localColumn;

                    if (otherBoardRow == boardRow && otherBoardColumn == boardColumn) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public synchronized boolean moveLeft(Player player) {
        Tetromino tetromino = player.getCurrentTetromino();

        if (tetromino == null) {
            return false;
        }

        int newColumn = tetromino.getColumn() - 1;

        if (!canPlaceTetromino(
                tetromino,
                tetromino.getRow(),
                newColumn,
                tetromino.getShape(),
                player
        )) {
            return false;
        }

        tetromino.setPosition(tetromino.getRow(), newColumn);

        return true;
    }

    public synchronized boolean moveRight(Player player) {
        Tetromino tetromino = player.getCurrentTetromino();

        if (tetromino == null) {
            return false;
        }

        int newColumn = tetromino.getColumn() + 1;

        if (!canPlaceTetromino(
                tetromino,
                tetromino.getRow(),
                newColumn,
                tetromino.getShape(),
                player
        )) {
            return false;
        }

        tetromino.setPosition(tetromino.getRow(), newColumn);

        return true;
    }

    public synchronized void setPlayerTetromino(Player player, Tetromino tetromino) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player no pertenece al juego.");
        }

        player.setCurrentTetromino(tetromino);
    }

    public synchronized boolean moveDown(Player player) {
        Tetromino tetromino = player.getCurrentTetromino();

        if (tetromino == null) {
            return false;
        }

        int newRow = tetromino.getRow() + 1;

        if (!canPlaceTetromino(
                tetromino,
                newRow,
                tetromino.getColumn(),
                tetromino.getShape(),
                player
        )) {
            return false;
        }

        tetromino.setPosition(newRow, tetromino.getColumn());

        return true;
    }

    public synchronized boolean rotateClockwise(Player player) {
        Tetromino tetromino = player.getCurrentTetromino();

        if (tetromino == null) {
            return false;
        }

        int currentRotation = tetromino.getRotationIndex();
        int newRotation = (currentRotation + 1) % 4;
        boolean[][] newShape = tetromino.getShape(newRotation);

        if (!canPlaceTetromino(
                tetromino,
                tetromino.getRow(),
                tetromino.getColumn(),
                newShape,
                player
        )) {
            return false;
        }

        tetromino.setRotationIndex(newRotation);

        return true;
    }

    public synchronized boolean rotateCounterClockwise(Player player) {
        Tetromino tetromino = player.getCurrentTetromino();

        if (tetromino == null) {
            return false;
        }

        int currentRotation = tetromino.getRotationIndex();
        int newRotation = (currentRotation + 3) % 4; // Rotacion inversa
        boolean[][] newShape = tetromino.getShape(newRotation);

        if (!canPlaceTetromino(
                tetromino,
                tetromino.getRow(),
                tetromino.getColumn(),
                newShape,
                player
        )) {
            return false;
        }

        tetromino.setRotationIndex(newRotation);

        return true;
    }

    public synchronized boolean lockPiece(Player player) {
        Tetromino tetromino = player.getCurrentTetromino();

        if (tetromino == null) {
            return false;
        }

        boolean[][] shape = tetromino.getShape();
        int row = tetromino.getRow();
        int col = tetromino.getColumn();

        if (!canPlaceTetromino(
                tetromino,
                row,
                col,
                shape,
                player
        )) {
            return false;
        }

        for (int localRow = 0; localRow < shape.length; localRow++) {
            for (int localCol = 0; localCol < shape[localRow].length; localCol++) {
                if (!shape[localRow][localCol]) {
                    continue;
                }

                int boardRow = row + localRow;
                int boardCol = col + localCol;

                board.setCell(boardRow, boardCol, player.getId().charAt(0));
            }
        }

        player.setCurrentTetromino(null);

        return true;
    }
}
