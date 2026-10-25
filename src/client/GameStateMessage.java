package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameStateMessage {
    private final String state;
    private final int rows;
    private final int columns;
    private final List<String> boardRows;
    private final List<PlayerState> players;

    private GameStateMessage(
            String state,
            int rows,
            int columns,
            List<String> boardRows,
            List<PlayerState> players
    ) {
        this.state = state;
        this.rows = rows;
        this.columns = columns;
        this.boardRows = Collections.unmodifiableList(
                new ArrayList<>(boardRows)
        );
        this.players = Collections.unmodifiableList(
                new ArrayList<>(players)
        );
    }

    public static GameStateMessage parse(String message) {
        if (message == null) {
            throw new IllegalArgumentException("El mensaje no puede ser null.");
        }

        String[] sections = message.split("\\|", -1);
        if (sections.length != 6 || !"STATE".equals(sections[0])) {
            throw new IllegalArgumentException("Formato STATE invalido.");
        }

        int rows = parseInteger(sections[2], "filas");
        int columns = parseInteger(sections[3], "columnas");
        String[] boardValues = sections[4].split(",", -1);

        if (boardValues.length != rows) {
            throw new IllegalArgumentException(
                    "Cantidad de filas STATE invalida."
            );
        }

        List<String> boardRows = new ArrayList<>();
        for (String row : boardValues) {
            if (row.length() != columns) {
                throw new IllegalArgumentException(
                        "Longitud de fila STATE invalida."
                );
            }
            boardRows.add(row);
        }

        List<PlayerState> players = new ArrayList<>();
        if (!sections[5].isEmpty()) {
            String[] playerValues = sections[5].split(";", -1);
            for (String playerValue : playerValues) {
                String[] fields = playerValue.split(",", -1);
                if (fields.length != 6) {
                    throw new IllegalArgumentException(
                            "Jugador STATE invalido."
                    );
                }

                players.add(
                        new PlayerState(
                                fields[0],
                                parseInteger(fields[1], "puntuacion"),
                                fields[2],
                                parseInteger(fields[3], "fila"),
                                parseInteger(fields[4], "columna"),
                                parseInteger(fields[5], "rotacion")
                        )
                );
            }
        }

        return new GameStateMessage(
                sections[1],
                rows,
                columns,
                boardRows,
                players
        );
    }

    private static int parseInteger(String value, String field) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Campo numerico invalido: " + field,
                    exception
            );
        }
    }

    public String getState() {
        return state;
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

    public List<PlayerState> getPlayers() {
        return players;
    }

    public static final class PlayerState {
        private final String id;
        private final int score;
        private final String pieceType;
        private final int row;
        private final int column;
        private final int rotation;

        private PlayerState(
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
