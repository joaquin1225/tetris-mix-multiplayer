package game;

import java.util.List;

public class GameLoop implements Runnable {
    private final GameManager gameManager;
    private final long tickMillis;
    private final long timeLimitMillis;
    private volatile boolean running;
    private long startTimeMillis;
    private Thread thread;

    public GameLoop(GameManager gameManager, long tickMillis) {
        this(gameManager, tickMillis, 0);
    }

    public GameLoop(
            GameManager gameManager,
            long tickMillis,
            long timeLimitMillis
    ) {
        if (gameManager == null) {
            throw new IllegalArgumentException("El GameManager no puede ser null.");
        }

        if (tickMillis <= 0) {
            throw new IllegalArgumentException(
                    "El intervalo del loop debe ser mayor que 0."
            );
        }

        if (timeLimitMillis < 0) {
            throw new IllegalArgumentException(
                    "El límite de tiempo no puede ser negativo."
            );
        }

        this.gameManager = gameManager;
        this.tickMillis = tickMillis;
        this.timeLimitMillis = timeLimitMillis;
    }

    public synchronized boolean start() {
        if (running || gameManager.getGameState() != GameState.RUNNING) {
            return false;
        }

        running = true;
        startTimeMillis = System.currentTimeMillis();
        thread = new Thread(this, "tetris-game-loop");
        thread.start();
        return true;
    }

    public synchronized boolean stop() {
        if (!running) {
            return false;
        }

        running = false;
        thread.interrupt();
        return true;
    }

    public boolean isRunning() {
        return running;
    }

    public void runOneStep() {
        if (gameManager.getGameState() != GameState.RUNNING) {
            return;
        }

        if (hasTimeLimitExpired()) {
            gameManager.finishGame();
            return;
        }

        List<Player> players = gameManager.getPlayersSnapshot();

        for (Player player : players) {
            if (gameManager.getGameState() != GameState.RUNNING) {
                return;
            }

            Tetromino tetromino = player.getCurrentTetromino();

            if (tetromino == null) {
                if (!gameManager.spawnRandomTetromino(player)) {
                    gameManager.finishGame();
                }
                continue;
            }

            if (gameManager.moveDown(player)) {
                continue;
            }

            LockAndSpawnResult result =
                    gameManager.lockAndSpawn(player);

            if (result == LockAndSpawnResult.LOCKED_NO_SPAWN) {
                return;
            }
        }
    }

    @Override
    public void run() {
        try {
            while (running
                    && gameManager.getGameState() == GameState.RUNNING) {
                runOneStep();

                if (gameManager.getGameState() != GameState.RUNNING) {
                    break;
                }

                Thread.sleep(tickMillis);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } finally {
            running = false;
        }
    }

    private boolean hasTimeLimitExpired() {
        return timeLimitMillis > 0
                && System.currentTimeMillis() - startTimeMillis
                >= timeLimitMillis;
    }
}
