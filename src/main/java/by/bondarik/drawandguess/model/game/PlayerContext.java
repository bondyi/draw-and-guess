package by.bondarik.drawandguess.model.game;

public class PlayerContext {
    private final String name;
    private int totalScore;

    public PlayerContext(String name) {
        this.name = name;
        this.totalScore = 0;
    }

    public PlayerContext(String name, int totalScore) {
        this.name = name;
        this.totalScore = totalScore;
    }

    public String getName() {
        return name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void addScore(int score) {
        this.totalScore += score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerContext that = (PlayerContext) o;

        if (totalScore != that.totalScore) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + totalScore;
        return result;
    }

    @Override
    public String toString() {
        return name + ' ' + totalScore;
    }
}
