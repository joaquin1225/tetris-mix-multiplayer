package game;

public class GameLoopTest {

    public static void main(String[] args) {
        testAutomaticFallAndLock();
        testLoopRequiresRunningGame();
        testManualStop();
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
}
