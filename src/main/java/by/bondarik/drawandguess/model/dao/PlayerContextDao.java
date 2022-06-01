package by.bondarik.drawandguess.model.dao;

import by.bondarik.drawandguess.model.game.PlayerInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerContextDao extends BaseDao {
    private static final String PATH = new File("target/classes/data/players.txt").getAbsolutePath();

    public static ArrayList<PlayerInfo> getContexts() {
        ArrayList<PlayerInfo> contexts = new ArrayList<>();

        String[] playerContexts = getData(PATH).split("\\r?\\n");

        for (String playerContext : playerContexts) {
            String[] currentContext = playerContext.split(" ");
            contexts.add(new PlayerInfo(currentContext[0], Integer.parseInt(currentContext[1])));
        }

        return contexts.size() != 0 ? contexts : null;
    }

    public static PlayerInfo getContext(String playerName) {
        ArrayList<PlayerInfo> contexts = getContexts();

        if (contexts != null) {
            for (PlayerInfo context : contexts) {
                if (Objects.equals(context.getName(), playerName)) return context;
            }
        }

        return null;
    }

    public static boolean add(PlayerInfo newContext) throws IOException {
        ArrayList<PlayerInfo> contexts = getContexts();

        if (contexts != null) {
            for (PlayerInfo context : contexts) {
                if (context.equals(newContext)) return false;
            }

            contexts.add(newContext);
            saveData(contexts);
        }

        return true;
    }

    public static void saveData(ArrayList<PlayerInfo> playerInfos) throws IOException {
        FileWriter fileWriter = new FileWriter(PATH, false);
        for (PlayerInfo context : playerInfos) {
            fileWriter.write(context.toString() + '\n');
        }
        fileWriter.close();
    }
}
