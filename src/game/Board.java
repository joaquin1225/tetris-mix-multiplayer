package game;

public class Board {
    private final int rows;
    private final int columns;
    private final char[][] cells;

    public Board(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Dimensiones del tablero invalidas.");
        }
        this.rows = rows;
        this.columns = columns;

        cells = new char[rows][columns];

        initialize();
    }

    private void initialize() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                cells[row][col] = '.';
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public synchronized char getCell(int row, int col) {
        validatePosition(row, col);
        return cells[row][col];
    }

    public synchronized void setCell(int row, int col, char value) {
        validatePosition(row, col);
        cells[row][col] = value;
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    public synchronized boolean isEmpty(int row, int col) {
        validatePosition(row, col);
        return cells[row][col] == '.';
    }

    public synchronized void clearCell(int row, int col) {
        validatePosition(row, col);
        cells[row][col] = '.';
    }

    public synchronized void print() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                System.out.print(cells[row][col]);
            }
            System.out.println();
        }
    }

    private void validatePosition(int row, int col) {
        if (!isInside(row, col)) {
            throw new IllegalArgumentException("Posicion fuera del tablero: (" + row + ", " + col + ")");
        }
    }
}
