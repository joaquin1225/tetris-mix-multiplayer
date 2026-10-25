package game;

public class TetrominoTest {

    public static void main(String[] args) {
        for (TetrominoType type : TetrominoType.values()) {

            System.out.println();
            System.out.println("==============================");
            System.out.println("Pieza: " + type);
            System.out.println("==============================");

            Tetromino tetromino =
                    new Tetromino(type, 0, 0);

            System.out.println(
                    "Orientación: " +
                            tetromino.getRotationIndex()
            );

            printShape(tetromino.getShape());

            for (int i = 1; i <= 4; i++) {

                tetromino.rotateClockwise();

                System.out.println();
                System.out.println(
                        "Orientación: " +
                                tetromino.getRotationIndex()
                );

                printShape(tetromino.getShape());
            }
        }
    }

    private static void printShape(
            Tetromino tetromino
    ) {

        boolean[][] shape =
                tetromino.getShape();

        for (int row = 0; row < shape.length; row++) {

            for (
                    int column = 0;
                    column < shape[row].length;
                    column++
            ) {

                System.out.print(
                        shape[row][column]
                                ? "X"
                                : "."
                );
            }

            System.out.println();
        }
    }

    private static void checkSquareMatrix(
            Tetromino tetromino
    ) {

        boolean[][] shape =
                tetromino.getShape();

        int rows = shape.length;

        for (int row = 0; row < rows; row++) {

            if (shape[row].length != rows) {

                throw new AssertionError(
                        "La pieza " +
                                tetromino.getType() +
                                " no tiene una matriz cuadrada."
                );
            }
        }

        System.out.println(
                "Matriz cuadrada: OK"
        );
    }

    private static void printShape(boolean[][] shape) {

        for (int row = 0; row < shape.length; row++) {

            for (int column = 0; column < shape[row].length; column++) {

                System.out.print(
                        shape[row][column] ? "X" : "."
                );
            }

            System.out.println();
        }
    }
}