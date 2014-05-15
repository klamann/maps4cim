package de.nx42.maps4cim.gui.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Font;

import org.junit.Test;

public class FontsTest {

    @Test
    public void test() throws Exception {

        assertFalse(Fonts.exists("I just made this up"));
        assertTrue(Fonts.exists(Font.DIALOG));
        assertTrue(Fonts.exists(Font.MONOSPACED));

        assertEquals(new Font(Font.DIALOG, Font.PLAIN, 12), Fonts.select(Font.PLAIN, 12, "i made this up", "this is not a font"));
        assertEquals(new Font(Font.MONOSPACED, Font.PLAIN, 10), Fonts.select(Font.PLAIN, 10, "i made this up", "Monospaced"));

        assertEquals(new Font(Font.DIALOG, Font.PLAIN, 10), Fonts.select(new Font(Font.DIALOG_INPUT, Font.PLAIN, 10), "i made this up", "this is not a font"));
        assertEquals(new Font(Font.MONOSPACED, Font.BOLD, 11), Fonts.select(new Font(Font.DIALOG_INPUT, Font.BOLD, 11), "i made this up", "Monospaced"));

    }

}
