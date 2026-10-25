package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameStateSnapshot {
    private final GameState gameState;
    private final int rows;
    private final int columns;
    private final List<String> boardRows;
    private final List<PlayerSnapshot> players;

    public GameStateSnapshot(
            GameState gameState,
            int rows,
            int columns,
            List<String> boardRows,
            List<PlayerSnapshot> players
    ) {
        this.gameState = gameState;
        this.rows = rows;
        this.columns = columns;
        this.boardRows = Collections.unmodifiableList(
                new ArrayList<>(boardRows)
        );
        this.players = Collections.unmodifiableList(
                new ArrayList<>(players)
        );
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public List<String> getBoardRows() {
        return boardRows;
    }

    public List<PlayerSnapshot> getPlayers() {
        return players;
    }

    public static final class PlayerSnapshot {
        private final String id;
        private final int score;
        private final String pieceType;
        private final int row;
        private final int column;
        private final int rotation;

        public PlayerSnapshot(
                String id,
                int score,
                String pieceType,
                int row,
                int column,
                int rotation
        ) {
            this.id = id;
            this.score = score;
            this.pieceType = pieceType;
            this.row = row;
            this.column = column;
            this.rotation = rotation;
        }

        public String getId() {
            return id;
        }

        public int getScore() {
            return score;
        }

        public String getPieceType() {
            return pieceType;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public int getRotation() {
            return rotation;
        }
    }
}
