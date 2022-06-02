package by.bondarik.drawandguess.model.server;

import by.bondarik.drawandguess.model.dao.PlayerInfoDao;
import by.bondarik.drawandguess.model.game.GameLogic;
import by.bondarik.drawandguess.model.game.Player;
import by.bondarik.drawandguess.model.game.PlayerInfo;
import by.bondarik.drawandguess.model.network.Message;
import by.bondarik.drawandguess.model.network.MessageType;
import by.bondarik.drawandguess.model.network.TCPConnection;
import by.bondarik.drawandguess.validator.DataValidator;
import by.bondarik.drawandguess.view.ServerView;

import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Server implements Closeable {
    private ServerSocket serverSocket;
    private final int serverPort;
    private ServerView view;
    private boolean isRunning;

    private final ArrayList<ServerThread> threads;

    private final GameLogic gameLogic;
    private final ArrayList<Point> drawingPoints;

    public Server(int serverPort) {
        this.serverPort = serverPort;
        this.isRunning = false;
        this.threads = new ArrayList<>();
        this.gameLogic = new GameLogic();
        this.drawingPoints = new ArrayList<>();
    }

    public void setView(ServerView view) {
        this.view = view;
    }

    public void start() {
        view.initView();

        isRunning = true;

        try {
            serverSocket = new ServerSocket(serverPort);

            view.appendMessage("[SERVER] SERVER IS RUNNING ON: " + InetAddress.getLocalHost().getHostAddress());
            view.appendMessage("[SERVER] SERVER IS LISTENING ON: " + serverPort);

            while (isRunning) {
                Socket socket = serverSocket.accept();

                ServerThread thread = new ServerThread(socket);
                threads.add(thread);
                thread.start();
            }

        } catch (IOException e) {
            view.appendMessage("[ERROR] CONNECTION TO SERVER LOST");
        }
    }

    protected void broadcastMessage(Message message) throws IOException {
        for (ServerThread thread : threads) {
            thread.getConnection().send(message);
        }
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    public void close() throws IOException {
        for (ServerThread thread : threads) thread.getConnection().close();
        serverSocket.close();
    }

    private class ServerThread extends Thread {
        private final TCPConnection connection;

        private Player player;

        protected ServerThread(Socket socket) throws IOException {
            this.connection = new TCPConnection(socket);
            this.player = null;
        }

        public TCPConnection getConnection() {
            return connection;
        }

        private Player login() {
            while (true) {
                try {
                    Message responseMessage = connection.receive();

                    if (responseMessage.getMessageType() == MessageType.RECEIVE_PLAYER_NAME) {
                        String requestedPlayerName = responseMessage.getData();

                        if (!DataValidator.isCorrectLogin(requestedPlayerName)) {
                            connection.send(new Message(MessageType.INVALID_DATA, requestedPlayerName));
                            continue;
                        }

                        PlayerInfo info = PlayerInfoDao.getInfo(requestedPlayerName);

                        if (info == null) {
                            PlayerInfo newInfo = new PlayerInfo(requestedPlayerName);
                            PlayerInfoDao.add(newInfo);

                            connection.send(new Message(MessageType.LOGIN_SUCCESS, requestedPlayerName));
                            broadcastMessage(new Message(MessageType.CHAT, requestedPlayerName + " connected for the first time!"));
                            return new Player(newInfo);
                        }
                        else if (!gameLogic.isPlaying(info)) {
                            connection.send(new Message(MessageType.LOGIN_SUCCESS, requestedPlayerName));
                            broadcastMessage(new Message(MessageType.CHAT, requestedPlayerName + " connected."));
                            return new Player(info);
                        }
                        else {
                            connection.send(new Message(MessageType.NAME_IS_USING));
                            return null;
                        }
                    }
                } catch (Exception e) {
                    view.appendMessage("[ERROR] REQUEST PLAYER FROM " + connection.toString());
                }
            }
        }

        private void communication() {
            boolean isActive = true;

            while (isActive) {
                try {
                    Message message = connection.receive();

                    switch (message.getMessageType()) {
                        case CHAT -> broadcastMessage(new Message(MessageType.CHAT, player.getInfo().getName() + ": " + message.getData()));

                        case GUESS -> {
                            if (gameLogic.isIllegal(player, MessageType.GUESS)) {
                                sendCorrectGameState(true);
                                continue;
                            }

                            if (gameLogic.guessWord(player, message.getData())) {
                                broadcastMessage(new Message(MessageType.CHAT, player.getInfo().getName() + " guessed the word!"));
                                if (gameLogic.isRoundFinished()) {
                                    if (finishRound()) {
                                        newRound(false);
                                    }
                                } else sendCorrectGameState(false);
                            } else broadcastMessage(new Message(MessageType.CHAT, player.getInfo().getName() + ": " + message.getData()));
                        }

                        case POINT -> {
                            if (gameLogic.isIllegal(player, MessageType.POINT)) {
                                sendCorrectGameState(true);
                                continue;
                            }

                            String[] xy = message.getData().split(";");
                            drawingPoints.add(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
                            broadcastMessage(new Message(MessageType.POINT, message.getData()));
                        }

                        case DISCONNECT -> {
                            view.appendMessage("[DISCONNECT] CLIENT DISCONNECTED FROM " + connection);

                            if (player != null) {
                                gameLogic.saveScore(player);
                                broadcastMessage(new Message(MessageType.CHAT, player.getInfo().getName() + " disconnected."));
                                threads.remove(this);
                                gameLogic.removePlayer(player);
                                broadcastScores();

                                if (!gameLogic.isWaitingForPlayers() && gameLogic.isRoundFinished()) {
                                    broadcastMessage(new Message(MessageType.CHAT, "Ending round."));
                                    if (finishRound()) {
                                        newRound(false);
                                    }
                                }
                            }
                            else threads.remove(this);

                            isActive = false;
                        }

                        default -> view.appendMessage("[ERROR] INVALID MESSAGE FROM " + connection);
                    }
                } catch (Exception e) {
                    view.appendMessage("[ERROR] COMMUNICATION FROM " + connection);
                    isActive = false;
                }
            }
        }

        private void newRound(boolean isFirst) throws IOException {
            Player newDrawer = gameLogic.newRound(isFirst);

            for (ServerThread thread : threads) {
                if (thread.player.equals(newDrawer)) {
                    thread.connection.send(new Message(MessageType.GAME_STATE, "DRAWING-" + gameLogic.getCurrentWord()));
                }
                else if (thread.player.isGuessing()) {
                    thread.connection.send(new Message(MessageType.GAME_STATE, "GUESSING"));
                }
            }

            broadcastMessage(new Message(MessageType.CHAT, newDrawer.getInfo().getName() + " is drawing"));
            drawingPoints.clear();

            view.appendMessage("[GAME] NEW ROUND. WORD: " + gameLogic.getCurrentWord());
        }

        private void sendCorrectGameState(boolean sendCanvas) throws IOException {
            StringBuilder stringBuilder = new StringBuilder();

            if (player.isGuessing()) stringBuilder.append("GUESSING");
            else if (player.isDrawing()) stringBuilder.append("DRAWING-").append(gameLogic.getCurrentWord());
            else if (gameLogic.hasGuessedCorrect(player)) stringBuilder.append("CORRECT");
            else stringBuilder.append("WAITING");

            connection.send(new Message(MessageType.CORRECT_GAME_STATE, stringBuilder.toString()));

            if (sendCanvas && !drawingPoints.isEmpty()) {
                for (Point point : drawingPoints) {
                    connection.send(new Message(MessageType.POINT, point.x + ";" + point.y));
                }
            }
        }

        private boolean finishRound() throws IOException {
            gameLogic.finishRound();

            String correctWord = gameLogic.getCurrentWord().toUpperCase();
            view.appendMessage("[GAME] ROUND FINISHED. WORD: " + correctWord);

            broadcastMessage(new Message(MessageType.CHAT, "Round finished. Word: " + correctWord));
            broadcastScores();

            if (threads.size() < GameLogic.MINIMUM_PLAYERS) {
                gameLogic.setWaitingForPlayers(true);
                broadcastMessage(new Message(MessageType.GAME_STATE, "WAITING"));
                return false;
            }

            return true;
        }

        private void broadcastScores() throws IOException {
            ArrayList<Player> sortedPlayers = new ArrayList<>(gameLogic.getPlayers());
            sortedPlayers.sort((p1, p2) -> p2.getCurrentScore() - p1.getCurrentScore());
            String scores = sortedPlayers.stream().map(p -> p.getInfo().getName() + ": " + p.getCurrentScore())
                    .collect(Collectors.joining(","));

            broadcastMessage(new Message(MessageType.SCORES, scores));
        }

        @Override
        public void run() {
            view.appendMessage("[CONNECT] CLIENT CONNECTED FROM " + connection);
            player = login();
            if (player != null) {
                try {
                    if (gameLogic.addPlayer(player)) newRound(true);
                    else sendCorrectGameState(true);

                    broadcastScores();
                } catch (IOException e) {
                    view.appendMessage("[ERROR] PLAYER ADD FROM " + connection);
                }
            }

            communication();
        }
    }
}
