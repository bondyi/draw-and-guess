package by.bondarik.drawandguess.model.dao;

import java.io.FileReader;
import java.io.IOException;

public abstract class BaseDao {
    public static String getData(String path) {
        StringBuilder stringBuilder = new StringBuilder();

        try (FileReader fileReader = new FileReader(path)) {
            int symbol;
            while ((symbol = fileReader.read()) != -1) {
                stringBuilder.append((char)symbol);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }
}
