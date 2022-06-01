package by.bondarik.drawandguess.view;

import by.bondarik.drawandguess.view.component.Canvas;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class ClientView extends JFrame {
    private final Canvas canvas;

    private final JTextField tfInput;
    private final JTextPane tpChatLog;
    private final JLabel lbGameStatus;
    private final ArrayList<JLabel> lbPlayers;

    public ClientView(MouseAdapter adapter, MouseMotionAdapter motionAdapter, ActionListener listener) {
        super("Draw and Guess: Connecting...");

        JPanel pnPlayers = new JPanel();
        JPanel pnChat = new JPanel();
        JPanel pnChatLog = new JPanel();

        canvas = new Canvas(adapter, motionAdapter);
        tfInput = new JTextField();
        tpChatLog = new JTextPane();
        lbGameStatus = new JLabel();
        JLabel lbScore = new JLabel();
        lbPlayers = new ArrayList<>();

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setSize(800, 600);
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        this.add(canvas, BorderLayout.CENTER);

        pnPlayers.setLayout(new GridLayout(0, 1, 0, 0));
        pnPlayers.setPreferredSize(new Dimension(150, 0));

        lbScore.setText("SCORE");
        pnPlayers.add(lbScore);

        for (int i = 0; i < 11; i++) {
            JLabel currentLabel = new JLabel();
            currentLabel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY));
            lbPlayers.add(currentLabel);
            pnPlayers.add(currentLabel);
        }
        this.add(pnPlayers, BorderLayout.LINE_START);

        pnChat.setLayout(new BoxLayout(pnChat, BoxLayout.Y_AXIS));
        pnChat.setPreferredSize(new Dimension(300, 0));

        tpChatLog.setEditable(false);
        tpChatLog.setMargin(new Insets(5, 5, 0, 5));

        pnChatLog.setLayout(new BorderLayout());
        pnChatLog.add(tpChatLog, BorderLayout.CENTER);
        pnChat.add(new JScrollPane(pnChatLog, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));

        tfInput.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        tfInput.addActionListener(listener);

        pnChat.add(tfInput);
        this.add(pnChat, BorderLayout.LINE_END);

        lbGameStatus.setText("CONNECTING...");
        lbGameStatus.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(lbGameStatus, BorderLayout.PAGE_START);
    }

    public void setGameStatus(String gameStatus) {
        lbGameStatus.setText(gameStatus);
    }

    public void setDrawingAllowed(boolean isAllowed, String word) {
        if (isAllowed) {
            lbGameStatus.setText("You are drawing: " + word.toUpperCase());
            canvas.setBackground(Color.WHITE);
            canvas.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 0), 4));
        } else {
            canvas.setBackground(new Color(220, 220, 220));
            canvas.setBorder(BorderFactory.createLineBorder(Color.RED, 4));
        }
    }

    public void setGuessingAllowed(boolean isAllowed) {
        tfInput.setEditable(isAllowed);
        if (isAllowed) {
            lbGameStatus.setText("You are guessing.");
            tfInput.setBackground(Color.WHITE);
            tfInput.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 0), 2));
        } else {
            tfInput.setBackground(new Color(220, 220, 220));
            tfInput.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }
    }

    public void addMessage(String message) {
        StyledDocument document = tpChatLog.getStyledDocument();

        try {
            int offset = document.getLength();
            document.insertString(offset, "\n" + message, new SimpleAttributeSet());
            tpChatLog.setCaretPosition(offset + 1);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPointToCanvas(Point point) {
        canvas.addPoint(point);
    }

    public void clearCanvas() {
        canvas.clear();
    }

    public void updateScoreLabels(String[][] scores) {
        lbPlayers.forEach(l -> l.setText(""));
        for (int i = 0; i < scores.length; i++) {
            String[] score = scores[i];
            JLabel label = lbPlayers.get(i);
            label.setText(score[0] + ": " + score[1]);
        }
    }

    public String createInputDialog(String message) {
        return JOptionPane.showInputDialog(this, message, "Question", JOptionPane.QUESTION_MESSAGE);
    }

    public void createMessageDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }
}
