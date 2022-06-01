package by.bondarik.drawandguess.test;

import by.bondarik.drawandguess.model.dao.WordDao;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WordDaoTest {

    @Test
    public void readingFile() {
        assertEquals("Angel", WordDao.getWords().get(0));
    }
}
