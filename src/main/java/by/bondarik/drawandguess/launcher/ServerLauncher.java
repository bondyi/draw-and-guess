package by.bondarik.drawandguess.launcher;

import by.bondarik.drawandguess.model.server.Server;
import by.bondarik.drawandguess.view.ServerView;

public class ServerLauncher {
    public static void main(String[] args) {
        Server server = new Server(2022);
        server.setView(new ServerView(server));

        server.start();
    }
}
