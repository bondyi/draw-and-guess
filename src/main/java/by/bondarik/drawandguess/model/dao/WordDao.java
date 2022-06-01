package by.bondarik.drawandguess.model.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordDao {
    private static final String PATH = new File("target/classes/data/words.txt").getAbsolutePath();

    private WordDao() {}

    public static List<String> getWords() {
        return new ArrayList<>(Arrays.stream(BaseDao.getData(PATH).split("\\r?\\n")).toList());
    }
}
