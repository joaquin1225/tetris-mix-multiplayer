package game;

public class Tetromino {
    private final TetrominoType type;
    private final boolean[][][] rotations;
    private int rotationIndex;
    private int row;
    private int col;

    public Tetromino(TetrominoType type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.rotations = createRotations(type);
        this.rotationIndex = 0;
    }

    private boolean[][][] createRotations(TetrominoType type) {
        boolean[][][] rotations = new boolean[4][][];
        rotations[0] = createShape(type);
        rotations[1] = rotateClockwise(rotations[0]);
        rotations[2] = rotateClockwise(rotations[1]);
        rotations[3] = rotateClockwise(rotations[2]);
        return rotations;
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
                        {true, true, true},
                        {false, false, false}
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

    private boolean[][] rotateClockwise(boolean[][] shape) {
        int size = shape.length;
        boolean[][] rotated = new boolean[size][size];

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                rotated[row][column] = shape[size - 1 - column][row];
            }
        }

        return rotated;
    }

    public void rotateClockwise() {
        rotationIndex = (rotationIndex + 1) % 4;
    }

    public void rotateCounterClockwise() {
        rotationIndex = (rotationIndex + 3) % 4;
    }

    public TetrominoType getType() {
        return type;
    }

    public boolean[][] getShape() {
        return rotations[rotationIndex];
    }

    public boolean[][] getShape(int rotationIndex) {
        return rotations[rotationIndex];
    }

    public int getRotationIndex() {
        return rotationIndex;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return col;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setRotationIndex(int rotationIndex) {
        if (rotationIndex < 0 || rotationIndex >= 4) {
            throw new IllegalArgumentException("Indice de rotacion invalido.");
        }
        this.rotationIndex = rotationIndex;
    }
}
