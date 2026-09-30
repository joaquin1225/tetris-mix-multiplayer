package game;

public class Player {
    private final String id;
    private int score;
    private Tetromino currentTetromino;

    public Player(String id) {
        this.id = id;
        this.score = 0;
        this.currentTetromino = null;
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
}
