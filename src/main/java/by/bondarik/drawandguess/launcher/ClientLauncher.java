package by.bondarik.drawandguess.launcher;

import by.bondarik.drawandguess.model.client.Client;

public class ClientLauncher {
    public static void main(String[] args) {
        Client client = new Client();
        client.setView();

        client.start();
        client.close();
    }
}
