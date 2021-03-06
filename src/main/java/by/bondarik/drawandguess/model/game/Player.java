package by.bondarik.drawandguess.model.game;

public class Player {
    private static int idCounter = 1;
    private final int id;

    private final PlayerInfo info;

    private boolean isGuessing;
    private boolean isDrawing;

    private int currentScore;

    public Player(PlayerInfo info) {
        this.id = idCounter++;
        this.info = info;
        this.isGuessing = false;
        this.isDrawing = false;
        this.currentScore = 0;
    }

    public int getId() {
        return id;
    }

    public PlayerInfo getInfo() {
        return info;
    }

    public boolean isGuessing() {
        return isGuessing;
    }

    public void setGuessing(boolean guessing) {
        isGuessing = guessing;
    }

    public boolean isDrawing() {
        return isDrawing;
    }

    public void setDrawing(boolean drawing) {
        isDrawing = drawing;
    }

    public void addScore(int score) {
        this.currentScore += score;
        this.info.addScore(score);
    }

    public int getCurrentScore() {
        return currentScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (id != player.id) return false;
        if (isGuessing != player.isGuessing) return false;
        if (isDrawing != player.isDrawing) return false;
        if (currentScore != player.currentScore) return false;
        return info.equals(player.info);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + info.hashCode();
        result = 31 * result + (isGuessing ? 1 : 0);
        result = 31 * result + (isDrawing ? 1 : 0);
        result = 31 * result + currentScore;
        return result;
    }

    @Override
    public String toString() {
        return info.getName() + ": " + currentScore + '(' + info.getTotalScore() + ')';
    }
}
