package game;

public class Player {
    private final String id;
    private int score;
    private Tetromino currentTetromino;
    private boolean fastDrop;
    private boolean fastDropBlockedUntilRelease;

    public Player(String id) {
        this.id = id;
        this.score = 0;
        this.currentTetromino = null;
        this.fastDrop = false;
        this.fastDropBlockedUntilRelease = false;
    }

    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        score += points;
    }

    public Tetromino getCurrentTetromino() {
        return currentTetromino;
    }

    public void setCurrentTetromino(Tetromino tetromino) {
        this.currentTetromino = tetromino;
    }

    public synchronized boolean isFastDrop() {
        return fastDrop;
    }

    public synchronized void setFastDrop(boolean fastDrop) {
        this.fastDrop = fastDrop;
    }

    public synchronized boolean pressFastDrop() {
        if (fastDropBlockedUntilRelease) {
            return false;
        }

        fastDrop = true;
        return true;
    }

    public synchronized void releaseFastDrop() {
        fastDrop = false;
        fastDropBlockedUntilRelease = false;
    }

    public synchronized void disableFastDropUntilRelease() {
        fastDrop = false;
        fastDropBlockedUntilRelease = true;
    }

    public synchronized boolean isFastDropBlockedUntilRelease() {
        return fastDropBlockedUntilRelease;
    }
}
