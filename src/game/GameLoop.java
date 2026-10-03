package game;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GameLoop implements Runnable {
    private final GameManager gameManager;
    private final long tickMillis;
    private final long timeLimitMillis;
    private final Map<Player, Long> nextFallTimes;
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
        this.nextFallTimes = new HashMap<>();
    }

    public synchronized boolean start() {
        if (running || gameManager.getGameState() != GameState.RUNNING) {
            return false;
        }

        running = true;
        startTimeMillis = System.currentTimeMillis();
        nextFallTimes.clear();
        long firstFallTime = System.currentTimeMillis() + tickMillis;

        for (Player player : gameManager.getPlayersSnapshot()) {
            nextFallTimes.put(player, firstFallTime);
        }

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

            DownMoveResult moveResult =
                    gameManager.moveDownWithResult(player);

            if (moveResult == DownMoveResult.MOVED) {
                continue;
            }

            if (moveResult == DownMoveResult.BLOCKED_BY_ACTIVE_PIECE) {
                player.disableFastDropUntilRelease();
                continue;
            }

            LockAndSpawnResult result =
                    gameManager.lockAndSpawn(player);

            if (result == LockAndSpawnResult.LOCKED_NO_SPAWN) {
                return;
            }
        }
    }

    private void runScheduledStep() {
        if (gameManager.getGameState() != GameState.RUNNING) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        List<Player> players = gameManager.getPlayersSnapshot();

        for (Player player : players) {
            Long nextFallTime = nextFallTimes.get(player);

            if (nextFallTime == null) {
                nextFallTimes.put(
                        player,
                        currentTime + getFallInterval(player)
                );
                continue;
            }

            if (currentTime < nextFallTime) {
                continue;
            }

            processPlayerFall(player);
            nextFallTimes.put(
                    player,
                    currentTime + getFallInterval(player)
            );
        }
    }

    private void processPlayerFall(Player player) {
        Tetromino tetromino = player.getCurrentTetromino();

        if (tetromino == null) {
            if (!gameManager.spawnRandomTetromino(player)) {
                gameManager.finishGame();
            }
            return;
        }

        DownMoveResult moveResult =
                gameManager.moveDownWithResult(player);

        if (moveResult == DownMoveResult.MOVED) {
            return;
        }

        if (moveResult == DownMoveResult.BLOCKED_BY_ACTIVE_PIECE) {
            player.disableFastDropUntilRelease();
            return;
        }

        LockAndSpawnResult result =
                gameManager.lockAndSpawn(player);

        if (result == LockAndSpawnResult.LOCKED_NO_SPAWN) {
            return;
        }
    }

    private long getFallInterval(Player player) {
        if (!player.isFastDrop()) {
            return tickMillis;
        }

        return Math.max(1, tickMillis / 10);
    }

    @Override
    public void run() {
        try {
            while (running
                    && gameManager.getGameState() == GameState.RUNNING) {
                if (hasTimeLimitExpired()) {
                    gameManager.finishGame();
                    break;
                }

                runScheduledStep();

                if (gameManager.getGameState() != GameState.RUNNING) {
                    break;
                }

                Thread.sleep(Math.min(50, tickMillis));
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
