package by.bondarik.drawandguess.model.game;

import by.bondarik.drawandguess.model.dao.WordDao;
import by.bondarik.drawandguess.model.network.MessageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameLogic {
    public static final int MINIMUM_PLAYERS = 2;

    private boolean isWaitingForPlayers;

    private final ArrayList<Player> players;
    private final ArrayList<Player> playersGuessedCorrect;

    private final ArrayList<String> words;
    private ArrayList<String> wordsPool;
    private String currentWord;

    public GameLogic() {
        this.isWaitingForPlayers = true;
        this.players = new ArrayList<>();
        this.playersGuessedCorrect = new ArrayList<>();
        this.words = new ArrayList<>(WordDao.getWords());
        this.wordsPool = new ArrayList<>(this.words);
        Collections.shuffle(this.wordsPool);
    }

    public boolean isWaitingForPlayers() {
        return isWaitingForPlayers;
    }

    public void setWaitingForPlayers(boolean waitingForPlayers) {
        isWaitingForPlayers = waitingForPlayers;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean isPlaying(PlayerInfo context) {
        for (Player player : players) {
            PlayerInfo currentContext = player.getContext();
            if (Objects.equals(currentContext.getName(), context.getName())) {
                return true;
            }
        }

        return false;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public boolean addPlayer(Player player) {
        players.add(player);

        if (isWaitingForPlayers) {
            if (players.size() >= MINIMUM_PLAYERS) {
                isWaitingForPlayers = false;
                return true;
            }
        }
        else player.setGuessing(true);

        return false;
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public boolean isIllegal(Player player, MessageType action) {
        if (player != null) {
            switch (action) {
                case GUESS:
                    if (player.isGuessing()) return false;
                    break;
                case POINT:
                    if (!player.isGuessing()) return false;
                    break;
            }
        }

        return true;
    }

    public Player newRound(boolean isFirst) {
        players.forEach(p -> p.setGuessing(true));
        if (!isFirst) Collections.rotate(players, -1);
        players.get(0).setDrawing(true);
        players.get(0).setGuessing(false);
        newWord();

        return players.get(0);
    }

    private void newWord() {
        if (wordsPool.isEmpty()) {
            wordsPool = new ArrayList<>(words);
            Collections.shuffle(wordsPool);
        }

        currentWord = wordsPool.remove(0);
    }

    public boolean guessWord(Player player, String guess) {
        if (currentWord.equalsIgnoreCase(guess)) {
            player.setGuessing(false);
            playersGuessedCorrect.add(0, player);
            return true;
        }

        return false;
    }

    public boolean hasGuessedCorrect(Player player) {
        return playersGuessedCorrect.contains(player);
    }

    public boolean isRoundFinished() {
        long numGuessing = players.stream().filter(Player::isGuessing).count();
        boolean drawerExists = players.stream().anyMatch(p -> !p.isGuessing());

        return !drawerExists ||
                (players.size() <= 2 && numGuessing == 0) ||
                (players.size() > 2 && numGuessing < 2);
    }

    public void finishRound() {
        distributeScore();
        players.forEach(p -> {
            p.setGuessing(false);
            p.setDrawing(false);
        });
        playersGuessedCorrect.clear();
    }

    private void distributeScore() {
        for (Player player : playersGuessedCorrect) {
            player.addScore(playersGuessedCorrect.indexOf(player) + 1);
        }
    }
}
