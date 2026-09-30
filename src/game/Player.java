package game;

public class Player {
    private final String id;
    private int score;

    public Player(String id) {
        this.id = id;
        this.score = 0;
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
}
