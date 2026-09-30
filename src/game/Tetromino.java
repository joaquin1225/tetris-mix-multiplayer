package game;

public class Tetromino {
    private final TetrominoType type;
    private final boolean[][] shape;
    private final double pivotRow;
    private final double pivotCol;
    private int row;
    private int col;

    public Tetromino(TetrominoType type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.shape = createShape(type);
        this.pivotRow = createPivotRow(type);
        this.pivotCol = createPivotCol(type);
    }

    private boolean[][] createShape(TetrominoType type) {
        switch (type) {
            case SMALL_SQUARE:
                return new boolean[][]{
                        {true}
                };
            case LARGE_SQUARE:
                return new boolean[][]{
                        {true, true},
                        {true, true}
                };
            case T:
                return new boolean[][]{
                        {false, true, false},
                        {true, true, true}
                };
            case SMALL_I:
                return new boolean[][]{
                        {true, false},
                        {true, false}
                };
            case LARGE_I:
                return new boolean[][]{
                        {false, true, false},
                        {false, true, false},
                        {false, true, false}
                };
            case L:
                return new boolean[][]{
                        {true, true},
                        {true, false}
                };
            case Y:
                return new boolean[][]{
                        {true, false, true},
                        {true, true, true},
                        {false, true, false}
                };
            default:
                throw new IllegalArgumentException("Tipo de pieza desconocida.");
        }
    }

    private double createPivotRow(TetrominoType type) {
        switch (type) {
            case SMALL_SQUARE:
                return 0.0;
            case LARGE_SQUARE:
                return 0.5;
            case T:
                return 1.0;
            case SMALL_I:
                return 0.5;
            case LARGE_I:
                return 1.0;
            case L:
                return 0.5;
            case Y:
                return 1.0;
            default:
                throw new IllegalArgumentException("Tipo de pieza desconocida.");
        }
    }

    private double createPivotCol(TetrominoType type) {
        switch (type) {
            case SMALL_SQUARE:
                return 0.0;
            case LARGE_SQUARE:
                return 0.5;
            case T:
                return 1.0;
            case SMALL_I:
                return 0.5;
            case LARGE_I:
                return 1.0;
            case L:
                return 0.5;
            case Y:
                return 1.0;
            default:
                throw new IllegalArgumentException("Tipo de pieza desconocida.");
        }
    }

    public TetrominoType getType() {
        return type;
    }

    public boolean[][] getShape() {
        return shape;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return col;
    }

    public double getPivotRow() {
        return pivotRow;
    }

    public double getPivotColumn() {
        return pivotCol;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
