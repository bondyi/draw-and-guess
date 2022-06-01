package by.bondarik.drawandguess.view;

import by.bondarik.drawandguess.model.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerView extends JFrame{
    private static Server server;
    private final JTextArea dialogWindow;

    public ServerView(Server server) {
        ServerView.server = server;
        dialogWindow = new JTextArea(10, 40);
    }

    public void initView() {
        this.setTitle("Draw and Guess SERVER");

        dialogWindow.setEditable(false);
        dialogWindow.setLineWrap(true);
        this.add(dialogWindow, BorderLayout.CENTER);
        this.add(new JScrollPane(dialogWindow), BorderLayout.EAST);

        this.pack();

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.stop();
                System.exit(0);
            }
        });

        this.setVisible(true);
    }

    public void appendMessage(String message) {
        dialogWindow.append(message + '\n');
    }
}
