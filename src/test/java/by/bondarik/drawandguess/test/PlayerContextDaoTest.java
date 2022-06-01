package by.bondarik.drawandguess.test;

import by.bondarik.drawandguess.model.dao.PlayerContextDao;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class PlayerContextDaoTest {

    @Test
    public void readingFile() {
        assertEquals(8, Objects.requireNonNull(PlayerContextDao.getContext("Balakras")).getTotalScore());
    }
}
