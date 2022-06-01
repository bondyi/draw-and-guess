package by.bondarik.drawandguess.test;

import by.bondarik.drawandguess.model.dao.PlayerInfoDao;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class PlayerInfoDaoTest {

    @Test
    public void readingFile() {
        assertEquals(8, Objects.requireNonNull(PlayerInfoDao.getContext("Balakras")).getTotalScore());
    }
}
