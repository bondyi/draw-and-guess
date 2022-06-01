package by.bondarik.drawandguess.model.client;

import by.bondarik.drawandguess.model.network.Message;
import by.bondarik.drawandguess.model.network.MessageType;
import by.bondarik.drawandguess.model.network.TCPConnection;
import by.bondarik.drawandguess.validator.DataValidator;
import by.bondarik.drawandguess.view.ClientView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Client {
    private TCPConnection connection;

    private ClientView view;

    private String playerName;
    private boolean isGuessing;
    private boolean isDrawing;

    public Client() {
        isGuessing = false;
        isDrawing = false;
    }

    public void setView() {
        this.view = new ClientView(new PressListener(), new DragListener(), new GuessListener());
    }

    public void start() {
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        try {
            playerName = connect();
        } catch (IOException | ClassNotFoundException e) {
            view.createMessageDialog("Unable to login to the server.");
        }

        if (playerName != null) {
            view.setVisible(true);
            communication();
        }
    }

    private String connect() throws IOException, ClassNotFoundException {
        String serverAddress = view.createInputDialog("Server address:");
        if (serverAddress == null) return null;

        if (DataValidator.isCorrectIpAddress(serverAddress)) {
            String serverPortString = view.createInputDialog("Server port:");
            if (serverPortString == null) return null;

            int serverPort;
            try {
                serverPort = Integer.parseInt(serverPortString);
            }
            catch (NumberFormatException e) {
                view.createMessageDialog("Invalid data.");
                return null;
            }

            if (serverPort >= 1024 && serverPort <= 65535) {

                playerName = view.createInputDialog("Player's name");

                connection = new TCPConnection(serverAddress, serverPort);

                connection.send(new Message((MessageType.RECEIVE_PLAYER_NAME), playerName));

                Message response = connection.receive();

                if (response.getMessageType() == MessageType.LOGIN_SUCCESS) {
                    String name = response.getData();
                    view.setTitle("Draw and Guess: " + name);
                    return name;
                }
                else {
                    connection.send(new Message(MessageType.DISCONNECT));
                    switch (response.getMessageType()) {
                        case INVALID_DATA -> view.createMessageDialog("Invalid data.");
                        case NAME_IS_USING -> view.createMessageDialog("Someone is using that name right now");
                        default -> view.createMessageDialog("Unknown error.");
                    }
                }
            }
            else view.createMessageDialog("Invalid data.");
        }
        else view.createMessageDialog("Invalid data.");

        return null;
    }

    private void communication() {
        while (connection.getSocket().isConnected()) {
            try {
                Message message = connection.receive();

                switch (message.getMessageType()) {
                    case CHAT -> view.addMessage(message.getData());

                    case POINT -> {
                        if (!isDrawing) {
                            String[] xy = message.getData().split(";");
                            view.addPointToCanvas(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
                        }
                    }

                    case SCORES -> updateScores(message.getData());
                    case GAME_STATE -> {
                        String[] data = message.getData().split("-");

                        switch (data[0]) {
                            case "DRAWING" -> newRound(true, data[1]);
                            case "GUESSING" -> newRound(false, null);

                            case "WAITING" -> {
                                setGameState(false, false, null);
                                view.setGameStatus("Waiting for players...");
                            }
                        }
                    }

                    case CORRECT_GAME_STATE -> {
                        String[] data = message.getData().split("-");

                        switch (data[0]) {
                            case "DRAWING" -> setGameState(true, false, data[1]);
                            case "GUESSING" -> setGameState(false, true, null);
                            case "CORRECT" -> {
                                setGameState(false, false, null);
                                view.setGameStatus("You guessed correctly!");
                            }

                            case "WAITING" -> {
                                setGameState(false, false, null);
                                view.setGameStatus("Waiting for players...");
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                view.createMessageDialog("Communication error.");
                close();
            }
        }
    }

    public void setGameState(boolean isDrawing, boolean isGuessing, String currentWord) {
        this.isDrawing = isDrawing;
        this.isGuessing = isGuessing;
        view.setDrawingAllowed(isDrawing, currentWord);
        view.setGuessingAllowed(isGuessing);
    }

    public void updateScores(String scoresString) {
        String[] players = scoresString.split(",");
        String[][] scores = new String[players.length][];
        for (int i = 0; i < players.length; i++) {
            scores[i] = players[i].split(": ");
        }
        view.updateScoreLabels(scores);
    }

    public void newRound(boolean isDrawing, String word) {
        setGameState(isDrawing, !isDrawing, word);
        view.clearCanvas();
    }

    public void close() {
        try {
            if (connection != null) {
                connection.send(new Message(MessageType.DISCONNECT));
                connection.close();
            }
        } catch (IOException e) {
            view.createMessageDialog("Closing client error.");
        }

        System.exit(0);
    }

    private class PressListener extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent event) {
            if (!isDrawing) return;

            Point point = event.getPoint();
            view.addPointToCanvas(point);
            try {
                connection.send(new Message(MessageType.POINT, point.x + ";" + point.y));
            } catch (IOException e) {
                view.createMessageDialog("Sending message error.");
            }
        }
    }

    private class DragListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent event) {
            if (!isDrawing) return;

            Point point = event.getPoint();
            view.addPointToCanvas(point);
            try {
                connection.send(new Message(MessageType.POINT, point.x + ";" + point.y));
            } catch (IOException e) {
                view.createMessageDialog("Sending message error.");
            }
        }
    }

    private class GuessListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isGuessing) return;

            if (!e.getActionCommand().isEmpty()) {
                try {
                    connection.send(new Message(MessageType.GUESS, e.getActionCommand()));
                } catch (IOException ex) {
                    view.createMessageDialog("Sending message error.");
                }
                ((JTextField)e.getSource()).setText("");
            }
        }
    }
}
