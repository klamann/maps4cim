package de.nx42.maps4cim.gui.window;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.nx42.maps4cim.gui.window.TextureChooser;

public class TextureChooserTest {

    @Test
    public void testGetPixelsForImageResource() throws Exception {
        int[] test = TextureChooser.getPixelsForImageResource(TextureChooser.resTextureGrass);
        assertEquals(5476, test.length);
    }

    @Test
    public void testPixelBlend() throws Exception {
        int red = 0xffff0000;
        int green = 0xff00ff00;
        int expected = 0xff808000;

        int actual = TextureChooser.pixelBlend(red, 0.5, green, 0.5, 0, 0, 0, 0);
        assertEquals(expected, actual);
    }

}
