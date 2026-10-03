package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {
    private static final int BASE_POINTS = 10;
    private final List<Player> players;
    private final Board board;
    private final Random random;
    private GameState gameState;

    public GameManager(int rows, int columns) {
        players = new ArrayList<>();
        board = new Board(rows, columns);
        random = new Random();
        gameState = GameState.WAITING;
    }

    public synchronized Player addPlayer() {
        if (gameState != GameState.WAITING) {
            throw new IllegalStateException(
                    "No se pueden agregar jugadores cuando la partida no está en WAITING."
            );
        }

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

    public synchronized List<Player> getPlayersSnapshot() {
        return new ArrayList<>(players);
    }

    public Board getBoard() {
        return board;
    }

    public synchronized GameState getGameState() {
        return gameState;
    }

    public synchronized boolean startGame() {
        if (gameState != GameState.WAITING) {
            return false;
        }

        if (players.isEmpty()) {
            return false;
        }

        gameState = GameState.RUNNING;

        for (Player player : players) {
            if (player.getCurrentTetromino() == null && !spawnRandomTetromino(player)) {
                gameState = GameState.FINISHED;
                return false;
            }
        }

        return true;
    }

    public synchronized boolean finishGame() {
        if (gameState == GameState.FINISHED) {
            return false;
        }

        gameState = GameState.FINISHED;
        return true;
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
        if (gameState != GameState.RUNNING) {
            return false;
        }

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
        if (gameState != GameState.RUNNING) {
            return false;
        }

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
        return moveDownWithResult(player) == DownMoveResult.MOVED;
    }

    synchronized DownMoveResult moveDownWithResult(Player player) {
        if (gameState != GameState.RUNNING) {
            return DownMoveResult.LANDED;
        }

        Tetromino tetromino = player.getCurrentTetromino();

        if (tetromino == null) {
            return DownMoveResult.LANDED;
        }

        int newRow = tetromino.getRow() + 1;
        boolean[][] shape = tetromino.getShape();

        for (int localRow = 0; localRow < shape.length; localRow++) {
            for (int localColumn = 0;
                 localColumn < shape[localRow].length;
                 localColumn++) {

                if (!shape[localRow][localColumn]) {
                    continue;
                }

                int boardRow = newRow + localRow;
                int boardColumn =
                        tetromino.getColumn() + localColumn;

                if (!board.isInside(boardRow, boardColumn)) {
                    return DownMoveResult.LANDED;
                }

                if (!board.isEmpty(boardRow, boardColumn)) {
                    return DownMoveResult.LANDED;
                }

                if (isOccupiedByAnotherPlayer(
                        boardRow,
                        boardColumn,
                        player
                )) {
                    return DownMoveResult.BLOCKED_BY_ACTIVE_PIECE;
                }
            }
        }

        tetromino.setPosition(newRow, tetromino.getColumn());

        return DownMoveResult.MOVED;
    }

    public synchronized boolean rotateClockwise(Player player) {
        if (gameState != GameState.RUNNING) {
            return false;
        }

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
        if (gameState != GameState.RUNNING) {
            return false;
        }

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
        if (gameState != GameState.RUNNING) {
            return false;
        }

        if (player == null) {
            throw new IllegalArgumentException("El player no puede ser null.");
        }

        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player no pertenece al juego.");
        }

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
        int clearedRows = clearFullRows();
        int points = calculateLineClearPoints(clearedRows);
        player.addScore(points);

        return true;
    }

    public synchronized int clearFullRows() {
        int clearedRows = 0;

        for (int row = board.getRows() - 1; row >= 0; row--) {
            if (board.isRowFull(row)) {
                board.clearRow(row);
                clearedRows++;
                row++;
            }
        }
        return clearedRows;
    }

    private int calculateLineClearPoints(int clearedRows) {
        switch (clearedRows) {
            case 1:
                return BASE_POINTS;
            case 2:
                return (int) (BASE_POINTS * 2.5);
            case 3:
                return BASE_POINTS * 4;
            default:
                return 0;
        }
    }

    public synchronized boolean spawnRandomTetromino(Player player) {
        if (gameState != GameState.RUNNING) {
            return false;
        }

        if (player == null) {
            throw new IllegalArgumentException("Player no puede ser null.");
        }

        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player no pertenece al juego.");
        }

        if (player.getCurrentTetromino() != null) {
            return false;
        }

        List<SpawnOption> validOptions = new ArrayList<>();

        for (TetrominoType type : TetrominoType.values()) {
            for (int rotationIndex = 0; rotationIndex < 4; rotationIndex++) {
                Tetromino candidate = new Tetromino(type, 0, 0);
                candidate.setRotationIndex(rotationIndex);

                boolean[][] shape = candidate.getShape();

                int firstOccupiedRow = findFirstOccupiedRow(shape);
                int firstOccupiedColumn = findFirstOccupiedColumn(shape);

                // La primera celda ocupada de la pieza quedará en la fila 0.
                int row = -firstOccupiedRow;

                // leftmostColumn representa la columna de la primera
                // celda ocupada de la pieza, no el origen de su matriz.
                for (int leftmostColumn = 0; leftmostColumn < board.getColumns(); leftmostColumn++) {
                    int column = leftmostColumn - firstOccupiedColumn;

                    if (canPlaceTetromino(
                            candidate,
                            row,
                            column,
                            shape,
                            player
                    )) {
                        validOptions.add(new SpawnOption(type, rotationIndex, row, column));
                    }
                }
            }
        }

        if (validOptions.isEmpty()) {
            return false;
        }

        SpawnOption selectedOption = validOptions.get(random.nextInt(validOptions.size()));
        Tetromino spawnedTetromino = new Tetromino(selectedOption.type, selectedOption.row, selectedOption.column);

        spawnedTetromino.setRotationIndex(selectedOption.rotationIndex);
        player.setCurrentTetromino(spawnedTetromino);

        return true;
    }

    private int findFirstOccupiedRow(boolean[][] shape) {
        for (int row = 0; row < shape.length; row++) {
            for (int column = 0; column < shape[row].length; column++) {
                if (shape[row][column]) {
                    return row;
                }
            }
        }

        throw new IllegalArgumentException("La pieza no contiene celdas ocupadas.");
    }

    private int findFirstOccupiedColumn(boolean[][] shape) {
        for (int column = 0; column < shape[0].length; column++) {
            for (int row = 0; row < shape.length; row++) {
                if (shape[row][column]) {
                    return column;
                }
            }
        }

        throw new IllegalArgumentException("La pieza no contiene celdas ocupadas.");
    }

    private boolean hasOccupiedCellInTopRow(Tetromino tetromino) {
        boolean[][] shape = tetromino.getShape();

        for (int column = 0; column < shape[0].length; column++) {
            if (shape[0][column]) {
                return true;
            }
        }

        return false;
    }

    private static class SpawnOption {
        private final TetrominoType type;
        private final int rotationIndex;
        private final int row;
        private final int column;

        private SpawnOption(TetrominoType type, int rotationIndex, int row, int column) {
            this.type = type;
            this.rotationIndex = rotationIndex;
            this.row = row;
            this.column = column;
        }
    }

    public synchronized LockAndSpawnResult lockAndSpawn(Player player) {
        boolean locked = lockPiece(player);

        if (!locked) {
            return LockAndSpawnResult.LOCK_FAILED;
        }

        boolean spawned = spawnRandomTetromino(player);

        if (!spawned) {
            finishGame();
            return LockAndSpawnResult.LOCKED_NO_SPAWN;
        }

        return LockAndSpawnResult.LOCK_AND_SPAWNED;
    }
}
