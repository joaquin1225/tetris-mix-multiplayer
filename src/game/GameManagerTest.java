package game;

public class GameManagerTest {

    public static void main(String[] args) {

        GameManager gameManager =
                new GameManager(10, 10);

        Player playerA =
                gameManager.addPlayer();

        Player playerB =
                gameManager.addPlayer();

        System.out.println();
        System.out.println("=== TEST 1: Posición libre ===");

        Tetromino pieceA =
                new Tetromino(
                        TetrominoType.LARGE_SQUARE,
                        0,
                        4
                );

        gameManager.setPlayerTetromino(
                playerA,
                pieceA
        );

        boolean result =
                gameManager.canPlaceTetromino(
                        pieceA,
                        0,
                        4,
                        pieceA.getShape(),
                        playerA
                );

        System.out.println(
                "¿Se puede colocar? " + result
        );

        System.out.println();
        System.out.println("=== TEST 2: Movimiento izquierda ===");

        result =
                gameManager.moveLeft(playerA);

        System.out.println(
                "¿Movimiento permitido? " + result
        );

        System.out.println(
                "Nueva columna: " +
                        pieceA.getColumn()
        );

        System.out.println();
        System.out.println("=== TEST 3: Movimiento derecha ===");

        result =
                gameManager.moveRight(playerA);

        System.out.println(
                "¿Movimiento permitido? " + result
        );

        System.out.println(
                "Nueva columna: " +
                        pieceA.getColumn()
        );

        System.out.println();
        System.out.println("=== TEST 4: true vs true ===");

        Tetromino pieceB =
                new Tetromino(
                        TetrominoType.SMALL_SQUARE,
                        0,
                        4
                );

        gameManager.setPlayerTetromino(
                playerB,
                pieceB
        );

        result =
                gameManager.canPlaceTetromino(
                        pieceA,
                        0,
                        4,
                        pieceA.getShape(),
                        playerA
                );

        System.out.println(
                "¿Colisión true vs true? " + !result
        );


        System.out.println();
        System.out.println("=== TEST 5: false vs true ===");

        Tetromino pieceT =
                new Tetromino(
                        TetrominoType.T,
                        0,
                        4
                );

        gameManager.setPlayerTetromino(
                playerB,
                pieceT
        );

        /*
         * La matriz inicial de T es:
         *
         * .X.
         * XXX
         * ...
         *
         * La celda local [0][0] es false.
         *
         * Por tanto, aunque pieceB tenga una pieza
         * ocupando esa posición física, esa celda
         * NO debe considerarse ocupada por T.
         */

        boolean[][] tShape =
                pieceT.getShape();

        System.out.println(
                "T[0][0] = " +
                        tShape[0][0]
        );

        Tetromino testPiece =
                new Tetromino(
                        TetrominoType.SMALL_SQUARE,
                        0,
                        4
                );

        result =
                gameManager.canPlaceTetromino(
                        testPiece,
                        0,
                        4,
                        testPiece.getShape(),
                        playerA
                );

        System.out.println(
                "Resultado: " + result
        );


        System.out.println();
        System.out.println("=== TEST 6: Movimiento hacia abajo ===");

        GameManager gameManager6 =
                new GameManager(10, 10);

        Player player6 =
                gameManager6.addPlayer();

        Tetromino fallingPiece =
                new Tetromino(
                        TetrominoType.SMALL_SQUARE,
                        0,
                        5
                );

        gameManager6.setPlayerTetromino(
                player6,
                fallingPiece
        );

        System.out.println(
                "Fila inicial: " +
                        fallingPiece.getRow()
        );

        result =
                gameManager6.moveDown(player6);

        System.out.println(
                "¿Movimiento permitido? " +
                        result
        );

        System.out.println(
                "Nueva fila: " +
                        fallingPiece.getRow()
        );


        System.out.println();
        System.out.println("=== TEST 7: Límite inferior ===");

        GameManager gameManager7 =
                new GameManager(10, 10);

        Player player7 =
                gameManager7.addPlayer();

        Tetromino bottomPiece =
                new Tetromino(
                        TetrominoType.SMALL_SQUARE,
                        9,
                        5
                );

        gameManager7.setPlayerTetromino(
                player7,
                bottomPiece
        );

        System.out.println(
                "Fila inicial: " +
                        bottomPiece.getRow()
        );

        result =
                gameManager7.moveDown(player7);

        System.out.println(
                "¿Movimiento permitido? " +
                        result
        );

        System.out.println(
                "Fila final: " +
                        bottomPiece.getRow()
        );


        System.out.println();
        System.out.println("=== TEST 8: Rotación horaria ===");

        gameManager = new GameManager(10, 10);

        playerA = gameManager.addPlayer();

        Tetromino tetromino =
                new Tetromino(
                        TetrominoType.T,
                        2,
                        2
                );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        System.out.println(
                "Rotación inicial: " +
                        tetromino.getRotationIndex()
        );

        boolean rotatedClockwise =
                gameManager.rotateClockwise(playerA);

        System.out.println(
                "¿Rotación permitida? " +
                        rotatedClockwise
        );

        System.out.println(
                "Nueva rotación: " +
                        tetromino.getRotationIndex()
        );


        System.out.println();
        System.out.println("=== TEST 9: Rotación antihoraria ===");

        gameManager = new GameManager(10, 10);

        playerA = gameManager.addPlayer();

        tetromino =
                new Tetromino(
                        TetrominoType.T,
                        2,
                        2
                );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        System.out.println(
                "Rotación inicial: " +
                        tetromino.getRotationIndex()
        );

        boolean clockwiseRotation =
                gameManager.rotateClockwise(playerA);

        System.out.println(
                "¿Rotación horaria permitida? " +
                        clockwiseRotation
        );

        System.out.println(
                "Después de horario: " +
                        tetromino.getRotationIndex()
        );

        boolean rotatedCounterClockwise =
                gameManager.rotateCounterClockwise(playerA);

        System.out.println(
                "¿Rotación antihoraria permitida? " +
                        rotatedCounterClockwise
        );

        System.out.println(
                "Rotación final: " +
                        tetromino.getRotationIndex()
        );


        System.out.println();
        System.out.println("=== TEST 10: Rotación bloqueada por otro jugador ===");

        gameManager = new GameManager(10, 10);

        playerA = gameManager.addPlayer();
        playerB = gameManager.addPlayer();

        Tetromino tetrominoA =
                new Tetromino(
                        TetrominoType.T,
                        2,
                        2
                );

        Tetromino tetrominoB =
                new Tetromino(
                        TetrominoType.SMALL_SQUARE,
                        3,
                        4
                );

        gameManager.setPlayerTetromino(
                playerA,
                tetrominoA
        );

        gameManager.setPlayerTetromino(
                playerB,
                tetrominoB
        );

        System.out.println(
                "Rotación inicial de A: " +
                        tetrominoA.getRotationIndex()
        );

        boolean rotated =
                gameManager.rotateClockwise(playerA);

        System.out.println(
                "¿Rotación permitida? " +
                        rotated
        );

        System.out.println(
                "Rotación final de A: " +
                        tetrominoA.getRotationIndex()
        );


        System.out.println();
        System.out.println("=== TEST 11: Rotación bloqueada por borde ===");

        gameManager = new GameManager(10, 10);

        playerA = gameManager.addPlayer();

        tetromino =
                new Tetromino(
                        TetrominoType.T,
                        2,
                        8
                );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        System.out.println(
                "Rotación inicial: " +
                        tetromino.getRotationIndex()
        );

        boolean borderRotation =
                gameManager.rotateClockwise(playerA);

        System.out.println(
                "¿Rotación permitida? " +
                        borderRotation
        );

        System.out.println(
                "Rotación final: " +
                        tetromino.getRotationIndex()
        );


        System.out.println();
        System.out.println("=== TEST 12: Fijar pieza ===");

        gameManager = new GameManager(10, 10);

        playerA = gameManager.addPlayer();

        tetromino =
                new Tetromino(
                        TetrominoType.T,
                        2,
                        2
                );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        System.out.println(
                "Pieza antes de fijar: " +
                        (playerA.getCurrentTetromino() != null)
        );

        boolean locked =
                gameManager.lockPiece(playerA);

        System.out.println(
                "¿Pieza fijada? " +
                        locked
        );

        System.out.println(
                "Pieza después de fijar: " +
                        (playerA.getCurrentTetromino() != null)
        );

        System.out.println();
        System.out.println("Tablero:");

        gameManager.getBoard().print();


        System.out.println();
        System.out.println("=== TEST 13: Solo celdas true se fijan ===");

        gameManager = new GameManager(10, 10);

        playerA = gameManager.addPlayer();

        tetromino =
                new Tetromino(
                        TetrominoType.T,
                        2,
                        2
                );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        gameManager.lockPiece(playerA);

        Board board =
                gameManager.getBoard();

        System.out.println(
                "Celda (2,2): " +
                        board.getCell(2, 2)
        );

        System.out.println(
                "Celda (2,3): " +
                        board.getCell(2, 3)
        );

        System.out.println(
                "Celda (3,2): " +
                        board.getCell(3, 2)
        );

        System.out.println(
                "Celda (3,3): " +
                        board.getCell(3, 3)
        );

        System.out.println(
                "Celda (3,4): " +
                        board.getCell(3, 4)
        );
    }
}