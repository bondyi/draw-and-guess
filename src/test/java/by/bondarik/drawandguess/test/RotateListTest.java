package by.bondarik.drawandguess.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RotateListTest {
    @Test
    public void rotate() {
        ArrayList<String> list = new ArrayList<>();
        list.add("among");
        list.add("us");

        Collections.rotate(list, -1);

        String toString = list.toString();

        assertEquals("[us, among]", toString);
    }
}
