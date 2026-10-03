package game;

import java.util.List;

public class GameLoop implements Runnable {
    private final GameManager gameManager;
    private final long tickMillis;
    private volatile boolean running;
    private Thread thread;

    public GameLoop(GameManager gameManager, long tickMillis) {
        if (gameManager == null) {
            throw new IllegalArgumentException("El GameManager no puede ser null.");
        }

        if (tickMillis <= 0) {
            throw new IllegalArgumentException(
                    "El intervalo del loop debe ser mayor que 0."
            );
        }

        this.gameManager = gameManager;
        this.tickMillis = tickMillis;
    }

    public synchronized boolean start() {
        if (running || gameManager.getGameState() != GameState.RUNNING) {
            return false;
        }

        running = true;
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
                Thread.sleep(tickMillis);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } finally {
            running = false;
        }
    }
}
