package by.bondarik.drawandguess.model.dao;

import java.io.FileReader;
import java.io.IOException;

public interface BaseDao {
    public static String getData(String path) {
        StringBuilder stringBuilder = new StringBuilder();

        try (FileReader fileReader = new FileReader(path)) {
            int symbol;
            while ((symbol = fileReader.read()) != -1) {
                stringBuilder.append((char)symbol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
