package by.bondarik.drawandguess.model.dao;

import by.bondarik.drawandguess.model.game.PlayerContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PlayerContextDao extends BaseDao {
    private static final String PATH = new File("target/classes/data/players.txt").getAbsolutePath();

    public static ArrayList<PlayerContext> getContexts() {
        ArrayList<PlayerContext> contexts = new ArrayList<>();

        String[] playerContexts = getData(PATH).split("\\r?\\n");

        for (String playerContext : playerContexts) {
            String[] currentContext = playerContext.split(" ");
            contexts.add(new PlayerContext(currentContext[0], Integer.parseInt(currentContext[1])));
        }

        return contexts.size() != 0 ? contexts : null;
    }

    public static PlayerContext getContext(String playerName) {
        ArrayList<PlayerContext> contexts = getContexts();

        if (contexts != null) {
            for (PlayerContext context : contexts) {
                if (Objects.equals(context.getName(), playerName)) return context;
            }
        }

        return null;
    }

    public static boolean add(PlayerContext newContext) throws IOException {
        ArrayList<PlayerContext> contexts = getContexts();

        if (contexts != null) {
            for (PlayerContext context : contexts) {
                if (context.equals(newContext)) return false;
            }

            contexts.add(newContext);
            saveData(contexts);
        }

        return true;
    }

    public static void saveData(ArrayList<PlayerContext> playerContexts) throws IOException {
        FileWriter fileWriter = new FileWriter(PATH, false);
        for (PlayerContext context : playerContexts) {
            fileWriter.write(context.toString() + '\n');
        }
        fileWriter.close();
    }
}
