package by.bondarik.drawandguess.model.dao;

import by.bondarik.drawandguess.model.game.PlayerInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerInfoDao {
    private static final String PATH = new File("target/classes/data/players.txt").getAbsolutePath();

    private PlayerInfoDao() {}

    public static List<PlayerInfo> getInfos() {
        ArrayList<PlayerInfo> infos = new ArrayList<>();

        String[] playerInfos = BaseDao.getData(PATH).split("\\r?\\n");
        if (Objects.equals(playerInfos[0], "")) return infos;

        for (String playerInfo : playerInfos) {
            String[] currentInfo = playerInfo.split(" ");
            infos.add(new PlayerInfo(currentInfo[0], Integer.parseInt(currentInfo[1])));
        }

        return infos;
    }

    public static PlayerInfo getInfo(String playerName) {
        ArrayList<PlayerInfo> infos = new ArrayList<>(getInfos());

        for (PlayerInfo info : infos) {
            if (Objects.equals(info.getName(), playerName)) return info;
        }

        return null;
    }

    public static boolean add(PlayerInfo newInfo) throws IOException {
        ArrayList<PlayerInfo> infos = new ArrayList<>(getInfos());


        for (PlayerInfo info : infos) {
            if (info.equals(newInfo)) return false;
        }

        infos.add(newInfo);
        saveData(infos);

        return true;
    }

    public static void savePlayer(PlayerInfo playerInfo) throws IOException {
        ArrayList<PlayerInfo> infos = new ArrayList<>(Objects.requireNonNull(getInfos()));

        int size = infos.size();
        for (int i = 0; i < size; i++) {
            if (infos.get(i).getName().equals(playerInfo.getName())) {
                infos.set(i, playerInfo);
                break;
            }
        }

        saveData(infos);
    }

    public static void saveData(List<PlayerInfo> playerInfos) throws IOException {
        try (FileWriter fileWriter = new FileWriter(PATH, false)) {
            for (PlayerInfo info : playerInfos) {
                fileWriter.write(info.toString() + '\n');
            }
        }
    }
}
