package by.bondarik.drawandguess.model.game;

public class Player {
    private static int idCounter = 1;
    private final int id;

    private final PlayerInfo context;

    private boolean isGuessing;
    private boolean isDrawing;

    private int currentScore;

    public Player(PlayerInfo context) {
        this.id = idCounter++;
        this.context = context;
        this.isGuessing = false;
        this.isDrawing = false;
        this.currentScore = 0;
    }

    public int getId() {
        return id;
    }

    public PlayerInfo getContext() {
        return context;
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
        this.context.addScore(score);
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
        return context.equals(player.context);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + context.hashCode();
        result = 31 * result + (isGuessing ? 1 : 0);
        result = 31 * result + (isDrawing ? 1 : 0);
        result = 31 * result + currentScore;
        return result;
    }

    @Override
    public String toString() {
        return context.getName() + ": " + currentScore + '(' + context.getTotalScore() + ')';
    }
}
