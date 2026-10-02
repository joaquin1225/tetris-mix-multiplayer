package game;

public class GameManagerTest {

    private static GameManager gameManager;
    private static Player playerA;
    private static Player playerB;
    private static Tetromino pieceA;
    private static Tetromino tetromino;
    private static Board board;
    private static boolean result;
    private static LockAndSpawnResult lockAndSpawnResult;

    public static void main(String[] args) {

        test1();
        test2();
        test3();
        test4();
        test5();
        test6();
        test7();
        test8();
        test9();
        test10();
        test11();
        test12();
        test13();
        test14();
        test15();
        test16();
        test17();
        test18();
        test19();
        test20();
        test21();
        test22();
        test23();
        test24();
        test25();
        test26();
        test27();
        test28();
        test29();
        test30();
        test31();
        test32();
        test33();
        test34();
        test35();
        test36();
        test37();
        test38();
    }

    private static void test1() {
        gameManager =
                new GameManager(10, 10);

        playerA =
                gameManager.addPlayer();

        playerB =
                gameManager.addPlayer();

        pieceA =
                new Tetromino(
                        TetrominoType.LARGE_SQUARE,
                        0,
                        4
                );

        gameManager.setPlayerTetromino(
                playerA,
                pieceA
        );

        System.out.println();
        System.out.println("=== TEST 1: Posición libre ===");

        result =
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
    }

    private static void test2() {
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
    }

    private static void test3() {
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
    }

    private static void test4() {
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
    }

    private static void test5() {
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
    }

    private static void test6() {
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
    }

    private static void test7() {
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
    }

    private static void test8() {
        System.out.println();
        System.out.println("=== TEST 8: Rotación horaria ===");

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
    }

    private static void test9() {
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
    }

    private static void test10() {
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
    }

    private static void test11() {
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
    }

    private static void test12() {
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
    }

    private static void test13() {
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

        board =
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

    private static void test14() {
        System.out.println();
        System.out.println("=== TEST 14: Detectar línea completa ===");

        gameManager = new GameManager(5, 5);

        board =
                gameManager.getBoard();

        // Llenamos completamente la fila 4.
        for (int column = 0; column < 5; column++) {
            board.setCell(4, column, 'a');
        }

        System.out.println(
                "¿Fila 4 completa? " +
                        board.isRowFull(4)
        );

        System.out.println(
                "¿Fila 3 completa? " +
                        board.isRowFull(3)
        );
    }

    private static void test15() {
        System.out.println();
        System.out.println("=== TEST 15: Eliminar línea y hacer caer filas ===");

        gameManager = new GameManager(5, 5);

        board = gameManager.getBoard();

        // Colocamos algunas piezas en filas superiores.
        board.setCell(2, 1, 'a');
        board.setCell(3, 2, 'b');

        // Completamos la última fila.
        for (int column = 0; column < 5; column++) {
            board.setCell(4, column, 'c');
        }

        System.out.println("Tablero antes de eliminar:");

        board.print();

        board.clearRow(4);

        System.out.println();
        System.out.println("Tablero después de eliminar:");

        board.print();
    }

    private static void test16() {
        System.out.println();
        System.out.println("=== TEST 16: Múltiples líneas completas ===");

        gameManager = new GameManager(6, 5);

        board = gameManager.getBoard();

        // Una pieza encima de las líneas.
        board.setCell(2, 2, 'a');

        // Dos líneas completas consecutivas.
        for (int column = 0; column < 5; column++) {
            board.setCell(4, column, 'b');
            board.setCell(5, column, 'c');
        }

        System.out.println("Tablero antes de eliminar:");

        board.print();

        for (int row = board.getRows() - 1; row >= 0; row--) {
            if (board.isRowFull(row)) {
                board.clearRow(row);
                // Volvemos a revisar la misma posición.
                row++;
            }
        }

        System.out.println();
        System.out.println("Tablero después de eliminar:");

        board.print();
    }

    private static void test17() {
        System.out.println();
        System.out.println("=== TEST 17: clearFullRows() ===");

        gameManager = new GameManager(6, 5);

        board = gameManager.getBoard();

        board.setCell(2, 2, 'a');

        for (int column = 0; column < 5; column++) {
            board.setCell(4, column, 'b');
            board.setCell(5, column, 'c');
        }

        System.out.println("Tablero antes:");

        board.print();

        int clearedRows =
                gameManager.clearFullRows();

        System.out.println();
        System.out.println(
                "Líneas eliminadas: " +
                        clearedRows
        );

        System.out.println();
        System.out.println("Tablero después:");

        board.print();
    }

    private static void test18() {
        System.out.println();
        System.out.println("=== TEST 18: Spawn aleatorio válido ===");

        GameManager spawnManager = new GameManager(10, 10);
        Player spawnPlayer = spawnManager.addPlayer();

        boolean spawned =
                spawnManager.spawnRandomTetromino(spawnPlayer);

        if (!spawned) {
            throw new AssertionError(
                    "Debía existir al menos una posición válida de spawn."
            );
        }

        assertValidSpawn(spawnManager, spawnPlayer);

        Tetromino spawnedPiece = spawnPlayer.getCurrentTetromino();

        System.out.println("Spawn aleatorio: OK");
        System.out.println("Tipo: " + spawnedPiece.getType());
        System.out.println("Rotación: " + spawnedPiece.getRotationIndex());
        System.out.println("Fila de origen: " + spawnedPiece.getRow());
        System.out.println("Columna de origen: " + spawnedPiece.getColumn());
    }

    private static void test19() {
        System.out.println();
        System.out.println("=== TEST 19: Spawn en espacio restringido ===");

        GameManager restrictedManager = new GameManager(4, 4);
        Player restrictedPlayer = restrictedManager.addPlayer();

        Board restrictedBoard = restrictedManager.getBoard();

        for (int row = 0; row < restrictedBoard.getRows(); row++) {
            for (int column = 0;
                 column < restrictedBoard.getColumns();
                 column++) {

                if (!(row == 0 && column == 0)) {
                    restrictedBoard.setCell(row, column, 'z');
                }
            }
        }

        boolean restrictedSpawned =
                restrictedManager.spawnRandomTetromino(restrictedPlayer);

        if (!restrictedSpawned) {
            throw new AssertionError(
                    "SMALL_SQUARE debía caber en la única celda libre."
            );
        }

        Tetromino restrictedPiece =
                restrictedPlayer.getCurrentTetromino();

        if (restrictedPiece.getType() != TetrominoType.SMALL_SQUARE) {
            throw new AssertionError(
                    "La única pieza que podía caber era SMALL_SQUARE."
            );
        }

        if (restrictedPiece.getRow() != 0
                || restrictedPiece.getColumn() != 0) {
            throw new AssertionError(
                    "SMALL_SQUARE debía aparecer en la celda (0, 0)."
            );
        }

        assertValidSpawn(restrictedManager, restrictedPlayer);

        System.out.println("Spawn restringido: OK");
    }

    private static void test20() {
        System.out.println();
        System.out.println("=== TEST 20: Tablero completamente ocupado ===");

        GameManager fullManager = new GameManager(4, 4);
        Player fullPlayer = fullManager.addPlayer();

        Board fullBoard = fullManager.getBoard();

        for (int row = 0; row < fullBoard.getRows(); row++) {
            for (int column = 0;
                 column < fullBoard.getColumns();
                 column++) {

                fullBoard.setCell(row, column, 'x');
            }
        }

        boolean fullSpawned =
                fullManager.spawnRandomTetromino(fullPlayer);

        if (fullSpawned) {
            throw new AssertionError(
                    "No debía generarse una pieza en un tablero lleno."
            );
        }

        if (fullPlayer.getCurrentTetromino() != null) {
            throw new AssertionError(
                    "El jugador no debía recibir una pieza."
            );
        }

        System.out.println("Tablero lleno: OK");
    }

    private static void test21() {
        System.out.println();
        System.out.println("=== TEST 21: Fila superior bloqueada ===");

        GameManager topBlockedManager = new GameManager(4, 4);
        Player topBlockedPlayer = topBlockedManager.addPlayer();

        Board topBlockedBoard = topBlockedManager.getBoard();

        for (int column = 0;
             column < topBlockedBoard.getColumns();
             column++) {

            topBlockedBoard.setCell(0, column, 'x');
        }

        boolean topBlockedSpawned =
                topBlockedManager.spawnRandomTetromino(topBlockedPlayer);

        if (topBlockedSpawned) {
            throw new AssertionError(
                    "No debía aparecer una pieza con la fila superior bloqueada."
            );
        }

        if (topBlockedPlayer.getCurrentTetromino() != null) {
            throw new AssertionError(
                    "El jugador no debía recibir una pieza."
            );
        }

        System.out.println("Fila superior bloqueada: OK");
    }

    private static void test22() {
        System.out.println();
        System.out.println("=== TEST 22: Jugador con pieza activa ===");

        GameManager activeManager = new GameManager(10, 10);
        Player activePlayer = activeManager.addPlayer();

        boolean firstSpawn =
                activeManager.spawnRandomTetromino(activePlayer);

        if (!firstSpawn) {
            throw new AssertionError(
                    "El primer spawn debía funcionar."
            );
        }

        Tetromino originalPiece = activePlayer.getCurrentTetromino();

        boolean secondSpawn =
                activeManager.spawnRandomTetromino(activePlayer);

        if (secondSpawn) {
            throw new AssertionError(
                    "No debía generarse una segunda pieza activa."
            );
        }

        if (activePlayer.getCurrentTetromino() != originalPiece) {
            throw new AssertionError(
                    "La pieza original no debía ser reemplazada."
            );
        }

        System.out.println("Protección contra doble spawn: OK");
    }

    private static void test23() {
        System.out.println();
        System.out.println("=== TEST 23: Múltiples spawns aleatorios ===");

        for (int i = 0; i < 100; i++) {
            GameManager repeatedManager = new GameManager(10, 10);
            Player repeatedPlayer = repeatedManager.addPlayer();

            boolean repeatedSpawn =
                    repeatedManager.spawnRandomTetromino(repeatedPlayer);

            if (!repeatedSpawn) {
                throw new AssertionError(
                        "Falló el spawn de la iteración " + i + "."
                );
            }

            assertValidSpawn(repeatedManager, repeatedPlayer);
        }

        System.out.println("100 spawns válidos: OK");
    }

    private static void test24() {
        System.out.println();
        System.out.println("=== TEST 24: Spawn sin colisión con otro jugador ===");

        GameManager multiSpawnManager =
                new GameManager(10, 10);

        Player playerC =
                multiSpawnManager.addPlayer();

        Player playerD =
                multiSpawnManager.addPlayer();

        Tetromino pieceC =
                new Tetromino(
                        TetrominoType.LARGE_SQUARE,
                        0,
                        0
                );

        multiSpawnManager.setPlayerTetromino(
                playerC,
                pieceC
        );

        boolean spawnedD =
                multiSpawnManager.spawnRandomTetromino(playerD);

        if (!spawnedD) {
            throw new AssertionError(
                    "El segundo jugador debía encontrar alguna posición válida."
            );
        }

        assertValidSpawn(
                multiSpawnManager,
                playerD
        );

        Tetromino pieceD =
                playerD.getCurrentTetromino();

        System.out.println("Spawn del segundo jugador: OK");
        System.out.println("Jugador A: "
                + pieceC.getType()
                + " en ("
                + pieceC.getRow()
                + ", "
                + pieceC.getColumn()
                + ")");

        System.out.println("Jugador B: "
                + pieceD.getType()
                + " en ("
                + pieceD.getRow()
                + ", "
                + pieceD.getColumn()
                + ")");
    }

    private static void test25() {

        System.out.println();
        System.out.println("=== TEST 25: Bloqueo sin líneas ===");

        gameManager = new GameManager(10, 10);
        playerA = gameManager.addPlayer();

        tetromino = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                8,
                0
        );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        result = gameManager.lockPiece(playerA);

        if (!result) {
            throw new AssertionError(
                    "La pieza debía poder bloquearse."
            );
        }

        if (playerA.getScore() != 0) {
            throw new AssertionError(
                    "El jugador no debía recibir puntos "
                            + "sin eliminar líneas. "
                            + "Puntos: "
                            + playerA.getScore()
            );
        }

        if (playerA.getCurrentTetromino() != null) {
            throw new AssertionError(
                    "La pieza debía dejar de estar activa."
            );
        }

        System.out.println("Bloqueo sin líneas: OK");
    }

    private static void test26() {

        System.out.println();
        System.out.println("=== TEST 26: Una línea ===");

        gameManager = new GameManager(4, 4);
        playerA = gameManager.addPlayer();

        board = gameManager.getBoard();

        // Dejamos libre únicamente la columna 0
        // de la última fila.
        for (int column = 1;
             column < board.getColumns();
             column++) {

            board.setCell(
                    3,
                    column,
                    'x'
            );
        }

        tetromino = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                3,
                0
        );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        result = gameManager.lockPiece(playerA);

        if (!result) {
            throw new AssertionError(
                    "La pieza debía poder bloquearse."
            );
        }

        if (playerA.getScore() != 10) {
            throw new AssertionError(
                    "Una línea debía otorgar 10 puntos. "
                            + "Puntos obtenidos: "
                            + playerA.getScore()
            );
        }

        if (playerA.getCurrentTetromino() != null) {
            throw new AssertionError(
                    "La pieza debía dejar de estar activa."
            );
        }

        System.out.println("Una línea: OK");
    }

    private static void test27() {

        System.out.println();
        System.out.println("=== TEST 27: Dos líneas ===");

        gameManager = new GameManager(4, 4);
        playerA = gameManager.addPlayer();

        board = gameManager.getBoard();

        // Dejamos libres (2,0) y (3,0).
        // Las demás posiciones de ambas filas
        // quedan ocupadas.
        for (int row = 2; row <= 3; row++) {

            for (int column = 1;
                 column < board.getColumns();
                 column++) {

                board.setCell(
                        row,
                        column,
                        'x'
                );
            }
        }

        tetromino = new Tetromino(
                TetrominoType.SMALL_I,
                2,
                0
        );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        result = gameManager.lockPiece(playerA);

        if (!result) {
            throw new AssertionError(
                    "La pieza debía poder bloquearse."
            );
        }

        if (playerA.getScore() != 25) {
            throw new AssertionError(
                    "Dos líneas debían otorgar 25 puntos. "
                            + "Puntos obtenidos: "
                            + playerA.getScore()
            );
        }

        if (playerA.getCurrentTetromino() != null) {
            throw new AssertionError(
                    "La pieza debía dejar de estar activa."
            );
        }

        System.out.println("Dos líneas: OK");
    }

    private static void test28() {

        System.out.println();
        System.out.println("=== TEST 28: Tres líneas ===");

        gameManager = new GameManager(4, 4);
        playerA = gameManager.addPlayer();

        board = gameManager.getBoard();

        // Dejamos libres:
        // (1,0), (2,0) y (3,0).
        //
        // Las demás posiciones de esas tres filas
        // quedan ocupadas.
        for (int row = 1; row <= 3; row++) {

            for (int column = 1;
                 column < board.getColumns();
                 column++) {

                board.setCell(
                        row,
                        column,
                        'x'
                );
            }
        }

        tetromino = new Tetromino(
                TetrominoType.LARGE_I,
                1,
                -1
        );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        result = gameManager.lockPiece(playerA);

        if (!result) {
            throw new AssertionError(
                    "La pieza debía poder bloquearse."
            );
        }

        if (playerA.getScore() != 40) {
            throw new AssertionError(
                    "Tres líneas debían otorgar 40 puntos. "
                            + "Puntos obtenidos: "
                            + playerA.getScore()
            );
        }

        if (playerA.getCurrentTetromino() != null) {
            throw new AssertionError(
                    "La pieza debía dejar de estar activa."
            );
        }

        System.out.println("Tres líneas: OK");
    }

    private static void test29() {

        System.out.println();
        System.out.println("=== TEST 29: Limpiar filas no mueve piezas activas ===");

        gameManager = new GameManager(4, 4);

        playerA = gameManager.addPlayer();
        playerB = gameManager.addPlayer();

        board = gameManager.getBoard();

        /*
         * Jugador A:
         * Completa la última fila.
         *
         * Dejamos libres (3,0) y colocamos
         * las otras tres celdas.
         */
        for (int column = 1;
             column < board.getColumns();
             column++) {

            board.setCell(
                    3,
                    column,
                    'a'
            );
        }

        tetromino = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                3,
                0
        );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        /*
         * Jugador B tiene una pieza activa
         * que NO participa en la limpieza.
         */
        pieceA = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                1,
                2
        );

        gameManager.setPlayerTetromino(
                playerB,
                pieceA
        );

        // Guardamos la posición original de B.
        int originalRow = pieceA.getRow();
        int originalColumn = pieceA.getColumn();
        int originalRotation = pieceA.getRotationIndex();

        /*
         * A bloquea su pieza.
         * Esto completa la fila 3 y provoca
         * la limpieza automática.
         */
        result = gameManager.lockPiece(playerA);

        if (!result) {
            throw new AssertionError(
                    "La pieza de A debía poder bloquearse."
            );
        }

        /*
         * La pieza de B debe seguir exactamente
         * donde estaba.
         */
        if (pieceA.getRow() != originalRow) {
            throw new AssertionError(
                    "La fila de la pieza activa de B cambió. "
                            + "Antes: " + originalRow
                            + ", después: " + pieceA.getRow()
            );
        }

        if (pieceA.getColumn() != originalColumn) {
            throw new AssertionError(
                    "La columna de la pieza activa de B cambió. "
                            + "Antes: " + originalColumn
                            + ", después: " + pieceA.getColumn()
            );
        }

        if (pieceA.getRotationIndex() != originalRotation) {
            throw new AssertionError(
                    "La rotación de la pieza activa de B cambió."
            );
        }

        /*
         * La pieza de B debe seguir siendo la pieza activa.
         */
        if (playerB.getCurrentTetromino() != pieceA) {
            throw new AssertionError(
                    "La pieza activa de B no debía ser reemplazada."
            );
        }

        /*
         * A debe haber recibido los puntos por
         * completar una línea.
         */
        if (playerA.getScore() != 10) {
            throw new AssertionError(
                    "A debía recibir 10 puntos. "
                            + "Puntos obtenidos: "
                            + playerA.getScore()
            );
        }

        System.out.println(
                "Las piezas activas no se desplazaron: OK"
        );
    }

    private static void test30() {

        System.out.println();
        System.out.println("=== TEST 30: Puntos para el jugador correcto ===");

        gameManager = new GameManager(4, 4);

        playerA = gameManager.addPlayer();
        playerB = gameManager.addPlayer();

        board = gameManager.getBoard();

        /*
         * Preparamos una línea que A va a completar.
         */
        for (int column = 1;
             column < board.getColumns();
             column++) {

            board.setCell(
                    3,
                    column,
                    'a'
            );
        }

        tetromino = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                3,
                0
        );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        /*
         * B también tiene una pieza activa,
         * pero no participa en la limpieza.
         */
        pieceA = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                0,
                0
        );

        gameManager.setPlayerTetromino(
                playerB,
                pieceA
        );

        result = gameManager.lockPiece(playerA);

        if (!result) {
            throw new AssertionError(
                    "La pieza de A debía poder bloquearse."
            );
        }

        if (playerA.getScore() != 10) {
            throw new AssertionError(
                    "A debía recibir 10 puntos. "
                            + "Puntos obtenidos: "
                            + playerA.getScore()
            );
        }

        if (playerB.getScore() != 0) {
            throw new AssertionError(
                    "B no debía recibir puntos. "
                            + "Puntos obtenidos: "
                            + playerB.getScore()
            );
        }

        System.out.println(
                "Puntos asignados al jugador correcto: OK"
        );
    }

    private static void test31() {

        System.out.println();
        System.out.println(
                "=== TEST 31: Bloqueo sin líneas entre jugadores ==="
        );

        gameManager = new GameManager(10, 10);

        playerA = gameManager.addPlayer();
        playerB = gameManager.addPlayer();

        tetromino = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                5,
                0
        );

        pieceA = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                2,
                5
        );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        gameManager.setPlayerTetromino(
                playerB,
                pieceA
        );

        int originalBRow = pieceA.getRow();
        int originalBColumn = pieceA.getColumn();

        result = gameManager.lockPiece(playerA);

        if (!result) {
            throw new AssertionError(
                    "La pieza de A debía poder bloquearse."
            );
        }

        if (playerA.getScore() != 0) {
            throw new AssertionError(
                    "A no debía recibir puntos."
            );
        }

        if (playerB.getScore() != 0) {
            throw new AssertionError(
                    "B no debía recibir puntos."
            );
        }

        if (pieceA.getRow() != originalBRow
                || pieceA.getColumn() != originalBColumn) {

            throw new AssertionError(
                    "La pieza activa de B fue modificada."
            );
        }

        if (playerB.getCurrentTetromino() != pieceA) {
            throw new AssertionError(
                    "La pieza activa de B no debía cambiar."
            );
        }

        System.out.println(
                "Estado del otro jugador preservado: OK"
        );
    }

    private static void test32() {
        System.out.println();
        System.out.println("=== TEST 32: Lock + Spawn de nueva pieza ===");

        gameManager = new GameManager(10, 10);
        playerA = gameManager.addPlayer();

        // Pieza inicial que será bloqueada.
        pieceA = new Tetromino(TetrominoType.SMALL_SQUARE, 8, 4);
        gameManager.setPlayerTetromino(playerA, pieceA);


        lockAndSpawnResult = gameManager.lockAndSpawn(playerA);

        if (lockAndSpawnResult != LockAndSpawnResult.LOCK_AND_SPAWNED) {
            throw new AssertionError(
                    "El bloqueo y el spawn debían completarse."
            );
        }

        // El bloqueo debe haber sido exitoso y debe haberse generado otra pieza.
        assert result : "LockAndSpawn deberia devolver true.";

        // La pieza anterior debe haber quedado fija en el tablero.
        assert gameManager.getBoard().getCell(8, 4) == 'a'
                : "La pieza bloqueada deberia estar en el tablero.";

        // No se limpiaron lineas, por lo que el score debe seguir en 0.
        assert playerA.getScore() == 0
                : "El score deberia seguir en 0.";

        // Debe existir una nueva pieza activa.
        assert playerA.getCurrentTetromino() != null
                : "El jugador deberia tener una nueva pieza.";

        // La nueva pieza no debe ser exactamente la pieza anterior.
        assert playerA.getCurrentTetromino() != pieceA
                : "La nueva pieza deberia ser una instancia diferente.";

        System.out.println("TEST 32 PASO");
    }

    private static void test33() {

        System.out.println();
        System.out.println("=== TEST 33: Bloqueo y spawn de nueva pieza ===");

        gameManager = new GameManager(10, 10);
        playerA = gameManager.addPlayer();

        tetromino = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                8,
                0
        );

        gameManager.setPlayerTetromino(
                playerA,
                tetromino
        );

        lockAndSpawnResult = gameManager.lockAndSpawn(playerA);

        if (lockAndSpawnResult != LockAndSpawnResult.LOCK_AND_SPAWNED) {
            throw new AssertionError(
                    "La pieza debía bloquearse "
                            + "y debía generarse una nueva pieza."
            );
        }

        board = gameManager.getBoard();

        if (board.getCell(8, 0) != 'a') {
            throw new AssertionError(
                    "La pieza bloqueada debía quedar "
                            + "en el tablero."
            );
        }

        if (playerA.getCurrentTetromino() == null) {
            throw new AssertionError(
                    "El jugador debía recibir "
                            + "una nueva pieza."
            );
        }

        if (playerA.getCurrentTetromino() == tetromino) {
            throw new AssertionError(
                    "La nueva pieza debía ser diferente "
                            + "de la pieza bloqueada."
            );
        }

        System.out.println("Bloqueo y spawn de nueva pieza: OK");
    }

    private static void test34() {
        System.out.println();
        System.out.println("=== TEST 34: Ciclo sin líneas ===");

        gameManager = new GameManager(10, 10);
        playerA = gameManager.addPlayer();

        pieceA = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                8,
                4
        );

        gameManager.setPlayerTetromino(playerA, pieceA);

        lockAndSpawnResult = gameManager.lockAndSpawn(playerA);

        if (lockAndSpawnResult != LockAndSpawnResult.LOCK_AND_SPAWNED) {
            throw new AssertionError(
                    "El bloqueo y el spawn debían completarse."
            );
        }

        if (gameManager.getBoard().getCell(8, 4) != 'a') {
            throw new AssertionError(
                    "La pieza debía quedar fijada en el tablero."
            );
        }

        if (playerA.getScore() != 0) {
            throw new AssertionError(
                    "No debían asignarse puntos sin eliminar líneas."
            );
        }

        if (playerA.getCurrentTetromino() == null) {
            throw new AssertionError(
                    "Debía generarse una nueva pieza activa."
            );
        }

        assertValidSpawn(gameManager, playerA);

        System.out.println("Ciclo sin líneas: OK");
    }

    private static void test35() {
        System.out.println();
        System.out.println("=== TEST 35: Ciclo con una línea ===");

        gameManager = new GameManager(4, 4);
        playerA = gameManager.addPlayer();
        board = gameManager.getBoard();

        for (int column = 1; column < board.getColumns(); column++) {
            board.setCell(3, column, 'x');
        }

        pieceA = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                3,
                0
        );

        gameManager.setPlayerTetromino(playerA, pieceA);

        lockAndSpawnResult = gameManager.lockAndSpawn(playerA);

        if (lockAndSpawnResult != LockAndSpawnResult.LOCK_AND_SPAWNED) {
            throw new AssertionError(
                    "El bloqueo, la limpieza y el spawn debían completarse."
            );
        }

        if (playerA.getScore() != 10) {
            throw new AssertionError(
                    "Una línea debía otorgar 10 puntos. "
                            + "Puntos obtenidos: "
                            + playerA.getScore()
            );
        }

        if (board.getCell(3, 0) != '.') {
            throw new AssertionError(
                    "La fila completa debía eliminarse."
            );
        }

        if (playerA.getCurrentTetromino() == null) {
            throw new AssertionError(
                    "Debía generarse una nueva pieza activa."
            );
        }

        assertValidSpawn(gameManager, playerA);

        System.out.println("Ciclo con una línea: OK");
    }

    private static void test36() {
        System.out.println();
        System.out.println("=== TEST 36: Bloqueo sin spawn posterior ===");

        gameManager = new GameManager(2, 4);
        playerA = gameManager.addPlayer();
        playerB = gameManager.addPlayer();
        Player playerC = gameManager.addPlayer();
        Player playerD = gameManager.addPlayer();
        Player playerE = gameManager.addPlayer();

        board = gameManager.getBoard();

        gameManager.setPlayerTetromino(
                playerB,
                new Tetromino(TetrominoType.SMALL_SQUARE, 0, 0)
        );
        gameManager.setPlayerTetromino(
                playerC,
                new Tetromino(TetrominoType.SMALL_SQUARE, 0, 1)
        );
        gameManager.setPlayerTetromino(
                playerD,
                new Tetromino(TetrominoType.SMALL_SQUARE, 0, 2)
        );
        gameManager.setPlayerTetromino(
                playerE,
                new Tetromino(TetrominoType.SMALL_SQUARE, 0, 3)
        );

        pieceA = new Tetromino(
                TetrominoType.SMALL_SQUARE,
                1,
                0
        );

        gameManager.setPlayerTetromino(playerA, pieceA);

        lockAndSpawnResult = gameManager.lockAndSpawn(playerA);

        if (lockAndSpawnResult != LockAndSpawnResult.LOCKED_NO_SPAWN) {
            throw new AssertionError(
                    "El bloqueo debía funcionar, pero no debía existir "
                            + "un spawn posterior válido."
            );
        }

        if (board.getCell(1, 0) != 'a') {
            throw new AssertionError(
                    "La pieza debía quedar fijada aunque no hubiera spawn."
            );
        }

        if (playerA.getCurrentTetromino() != null) {
            throw new AssertionError(
                    "El jugador debía quedar sin pieza activa."
            );
        }

        if (playerA.getScore() != 0) {
            throw new AssertionError(
                    "No debían asignarse puntos."
            );
        }

        if (gameManager.getGameState() != GameState.FINISHED) {
            throw new AssertionError(
                    "La partida debía finalizar al no existir un spawn válido."
            );
        }

        System.out.println("Bloqueo sin spawn posterior: OK");
    }

    private static void test37() {
        System.out.println();
        System.out.println("=== TEST 37: Estado inicial de la partida ===");

        gameManager = new GameManager(10, 10);

        if (gameManager.getGameState() != GameState.WAITING) {
            throw new AssertionError(
                    "La partida debía iniciar en estado WAITING."
            );
        }

        System.out.println("Estado inicial WAITING: OK");
    }

    private static void test38() {
        System.out.println();
        System.out.println("=== TEST 38: Transiciones del estado de la partida ===");

        gameManager = new GameManager(10, 10);

        if (!gameManager.startGame()) {
            throw new AssertionError(
                    "La partida debía pasar de WAITING a RUNNING."
            );
        }

        if (gameManager.getGameState() != GameState.RUNNING) {
            throw new AssertionError(
                    "La partida debía quedar en estado RUNNING."
            );
        }

        if (gameManager.startGame()) {
            throw new AssertionError(
                    "Una partida RUNNING no debía iniciarse nuevamente."
            );
        }

        if (!gameManager.finishGame()) {
            throw new AssertionError(
                    "La partida debía pasar de RUNNING a FINISHED."
            );
        }

        if (gameManager.getGameState() != GameState.FINISHED) {
            throw new AssertionError(
                    "La partida debía quedar en estado FINISHED."
            );
        }

        if (gameManager.finishGame()) {
            throw new AssertionError(
                    "Una partida FINISHED no debía finalizarse nuevamente."
            );
        }

        System.out.println("Transiciones WAITING -> RUNNING -> FINISHED: OK");
    }

    private static void assertValidSpawn(
            GameManager manager,
            Player player
    ) {
        Tetromino piece = player.getCurrentTetromino();

        if (piece == null) {
            throw new AssertionError(
                    "El jugador debía tener una pieza activa."
            );
        }

        boolean[][] shape = piece.getShape();
        int minOccupiedRow = Integer.MAX_VALUE;

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {

                // Las celdas false no ocupan espacio.
                if (!shape[r][c]) {
                    continue;
                }

                int boardRow = piece.getRow() + r;
                int boardColumn = piece.getColumn() + c;

                if (!manager.getBoard().isInside(
                        boardRow,
                        boardColumn
                )) {
                    throw new AssertionError(
                            "Una celda ocupada quedó fuera del tablero: ("
                                    + boardRow + ", " + boardColumn + ")."
                    );
                }

                if (!manager.getBoard().isEmpty(
                        boardRow,
                        boardColumn
                )) {
                    throw new AssertionError(
                            "La pieza se superpone con una celda fija: ("
                                    + boardRow + ", " + boardColumn + ")."
                    );
                }

                minOccupiedRow = Math.min(minOccupiedRow, boardRow);
            }
        }

        if (minOccupiedRow != 0) {
            throw new AssertionError(
                    "La primera celda ocupada debía estar en la fila 0, "
                            + "pero está en la fila " + minOccupiedRow + "."
            );
        }

        if (!manager.canPlaceTetromino(
                piece,
                piece.getRow(),
                piece.getColumn(),
                shape,
                player
        )) {
            throw new AssertionError(
                    "La pieza generada no debía tener conflictos."
            );
        }
    }
}