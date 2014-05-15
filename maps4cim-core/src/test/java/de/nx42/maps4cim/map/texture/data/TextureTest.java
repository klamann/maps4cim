package de.nx42.maps4cim.map.texture.data;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.nx42.maps4cim.map.texture.data.Texture;

public class TextureTest {

    @Test
    public void testMixAddIntInt() throws Exception {
        int expected = 0x22334455;
        int actual = Texture.mixAdd(0x11223344, 0x11111111);
        assertEquals(expected, actual);
    }

    @Test
    public void testMixAddIntIntInt() throws Exception {
        int expected = 0x44556677;
        int actual = Texture.mixAdd(0x11223344, 0x11111111, 0x22222222);
        assertEquals(expected, actual);
    }

    @Test
    public void testMixAddIntIntUnsigned() throws Exception {
        int expected = 0x1177aaff;
        int actual = Texture.mixAdd(0x006611bb, 0x11119944);
        assertEquals(expected, actual);
    }

    @Test
    public void testMixAddIntIntIntUnsigned() throws Exception {
        int expected = 0x1177aaff;
        int actual = Texture.mixAdd(0x006611bb, 0x11111111, 0x00008833);
        assertEquals(expected, actual);
    }

}
