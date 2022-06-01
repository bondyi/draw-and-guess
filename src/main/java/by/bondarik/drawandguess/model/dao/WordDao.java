package by.bondarik.drawandguess.model.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class WordDao extends BaseDao {
    private static final String PATH = new File("target/classes/data/words.txt").getAbsolutePath();

    public static ArrayList<String> getWords() {
        return new ArrayList<>(Arrays.stream(getData(PATH).split("\\r?\\n")).toList());
    }
}
