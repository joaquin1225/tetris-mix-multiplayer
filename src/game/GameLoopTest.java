package game;

public class GameLoopTest {

    public static void main(String[] args) {
        testAutomaticFallAndLock();
        testLoopRequiresRunningGame();
        testManualStop();
        testTimeLimit();
        testFastDropState();
        testFastDropFallsFaster();
        testActivePieceCollisionDisablesFastDrop();
    }

    private static void testAutomaticFallAndLock() {
        GameManager gameManager = new GameManager(4, 4);
        Player player = gameManager.addPlayer();
        gameManager.startGame();

        Tetromino piece =
                new Tetromino(TetrominoType.SMALL_SQUARE, 3, 0);
        gameManager.setPlayerTetromino(player, piece);

        GameLoop gameLoop = new GameLoop(gameManager, 10);
        gameLoop.runOneStep();

        if (gameManager.getBoard().getCell(3, 0) != 'a') {
            throw new AssertionError(
                    "La pieza debía bloquearse al no poder bajar."
            );
        }

        if (player.getCurrentTetromino() == null) {
            throw new AssertionError(
                    "Debía generarse una nueva pieza."
            );
        }

        System.out.println("Caída y bloqueo automático: OK");
    }

    private static void testLoopRequiresRunningGame() {
        GameManager gameManager = new GameManager(4, 4);
        Player player = gameManager.addPlayer();
        gameManager.setPlayerTetromino(
                player,
                new Tetromino(TetrominoType.SMALL_SQUARE, 0, 0)
        );

        GameLoop gameLoop = new GameLoop(gameManager, 10);

        if (gameLoop.start()) {
            throw new AssertionError(
                    "El loop no debía iniciar una partida WAITING."
            );
        }

        if (player.getCurrentTetromino().getRow() != 0) {
            throw new AssertionError(
                    "La pieza no debía moverse en WAITING."
            );
        }

        System.out.println("Loop protegido por estado: OK");
    }

    private static void testManualStop() {
        GameManager gameManager = new GameManager(4, 4);
        gameManager.addPlayer();
        gameManager.startGame();

        GameLoop gameLoop = new GameLoop(gameManager, 10);

        if (!gameLoop.start()) {
            throw new AssertionError(
                    "El loop debía iniciar en RUNNING."
            );
        }

        if (!gameLoop.stop()) {
            throw new AssertionError(
                    "El loop debía detenerse."
            );
        }

        System.out.println("Detención manual del loop: OK");
    }

    private static void testTimeLimit() {
        GameManager gameManager = new GameManager(4, 4);
        gameManager.addPlayer();
        gameManager.startGame();

        GameLoop gameLoop = new GameLoop(gameManager, 5, 30);

        if (!gameLoop.start()) {
            throw new AssertionError(
                    "El loop debía iniciar en RUNNING."
            );
        }

        long deadline = System.currentTimeMillis() + 500;

        while (gameManager.getGameState() != GameState.FINISHED
                && System.currentTimeMillis() < deadline) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new AssertionError(
                        "La prueba fue interrumpida."
                );
            }
        }

        if (gameManager.getGameState() != GameState.FINISHED) {
            gameLoop.stop();
            throw new AssertionError(
                    "La partida debía finalizar al vencer el límite de tiempo."
            );
        }

        if (gameLoop.isRunning()) {
            gameLoop.stop();
            throw new AssertionError(
                    "El loop debía detenerse al finalizar la partida."
            );
        }

        System.out.println("Finalización por límite de tiempo: OK");
    }

    private static void testFastDropState() {
        GameManager gameManager = new GameManager(4, 4);
        Player player = gameManager.addPlayer();

        if (player.isFastDrop()) {
            throw new AssertionError(
                    "El descenso rápido debía iniciar desactivado."
            );
        }

        player.setFastDrop(true);

        if (!player.isFastDrop()) {
            throw new AssertionError(
                    "El descenso rápido debía activarse."
            );
        }

        player.setFastDrop(false);

        if (player.isFastDrop()) {
            throw new AssertionError(
                    "El descenso rápido debía desactivarse."
            );
        }

        System.out.println("Estado de descenso rápido: OK");
    }

    private static void testFastDropFallsFaster() {
        GameManager normalManager = new GameManager(100, 4);
        Player normalPlayer = normalManager.addPlayer();
        normalManager.startGame();
        normalPlayer.setCurrentTetromino(
                new Tetromino(TetrominoType.SMALL_SQUARE, 0, 0)
        );

        GameManager fastManager = new GameManager(100, 4);
        Player fastPlayer = fastManager.addPlayer();
        fastManager.startGame();
        fastPlayer.setCurrentTetromino(
                new Tetromino(TetrominoType.SMALL_SQUARE, 0, 0)
        );
        fastPlayer.setFastDrop(true);

        GameLoop normalLoop = new GameLoop(normalManager, 100);
        GameLoop fastLoop = new GameLoop(fastManager, 100);

        if (!normalLoop.start() || !fastLoop.start()) {
            throw new AssertionError(
                    "Los loops debían iniciar correctamente."
            );
        }

        try {
            Thread.sleep(260);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError(
                    "La prueba fue interrumpida."
            );
        } finally {
            normalLoop.stop();
            fastLoop.stop();
        }

        int normalRow = normalPlayer.getCurrentTetromino().getRow();
        int fastRow = fastPlayer.getCurrentTetromino().getRow();

        if (fastRow <= normalRow) {
            throw new AssertionError(
                    "La pieza con descenso rápido debía avanzar más."
            );
        }

        System.out.println("Descenso rápido individual: OK");
    }

    private static void testActivePieceCollisionDisablesFastDrop() {
        GameManager gameManager = new GameManager(6, 4);
        Player playerA = gameManager.addPlayer();
        Player playerB = gameManager.addPlayer();
        gameManager.startGame();

        Tetromino pieceA =
                new Tetromino(TetrominoType.SMALL_SQUARE, 0, 0);
        Tetromino pieceB =
                new Tetromino(TetrominoType.SMALL_SQUARE, 1, 0);

        gameManager.setPlayerTetromino(playerA, pieceA);
        gameManager.setPlayerTetromino(playerB, pieceB);

        if (!playerA.pressFastDrop()) {
            throw new AssertionError(
                    "El descenso rápido debía activarse."
            );
        }

        GameLoop gameLoop = new GameLoop(gameManager, 10);
        gameLoop.runOneStep();

        if (pieceA.getRow() != 0) {
            throw new AssertionError(
                    "La pieza bloqueada por otra pieza activa no debía bajar."
            );
        }

        if (playerA.isFastDrop()) {
            throw new AssertionError(
                    "La colisión debía desactivar el descenso rápido."
            );
        }

        if (!playerA.isFastDropBlockedUntilRelease()) {
            throw new AssertionError(
                    "La pieza debía requerir soltar DOWN antes de reactivar."
            );
        }

        if (playerA.pressFastDrop()) {
            throw new AssertionError(
                    "DOWN no debía reactivarse sin una liberación previa."
            );
        }

        playerA.releaseFastDrop();

        if (!playerA.pressFastDrop()) {
            throw new AssertionError(
                    "DOWN debía reactivarse después de soltarlo."
            );
        }

        System.out.println(
                "Colisión con pieza activa y rearme de DOWN: OK"
        );
    }
}
