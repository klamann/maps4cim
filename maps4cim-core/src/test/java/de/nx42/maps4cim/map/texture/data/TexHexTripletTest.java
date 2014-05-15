package de.nx42.maps4cim.map.texture.data;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TexHexTripletTest {

    protected static final int r = 0x42;
    protected static final int m = 0xff;
    protected static final int d = 0x11;
    protected static final String strExpected = "42ff11";
    protected static final int intExpected = 0x42ff11;
    protected static final TexHexTriplet texExpected = new TexHexTriplet(r, m, d);


    @Test
    public void testGetHexString() {
        assertEquals(strExpected, texExpected.getHexString());
    }

    @Test
    public void testEquals() {
        assertTrue(texExpected.equals(new TexHexTriplet(r, m, d)));
    }

    @Test
    public void testGetters() {
        assertEquals(r, texExpected.getRoughgrass());
        assertEquals(m, texExpected.getMud());
        assertEquals(d, texExpected.getDirt());
    }

    @Test
    public void testParseInt() {
        TexHexTriplet actual = TexHexTriplet.parse(0x42ff11);
        assertEquals(texExpected, actual);
    }

    @Test
    public void testParseString() {
        TexHexTriplet actual = TexHexTriplet.parse(strExpected);
        assertEquals(texExpected, actual);
    }

    @Test
    public void testParseString2() {
        TexHexTriplet actual = TexHexTriplet.parse("#" + strExpected);
        assertEquals(texExpected, actual);
    }

    @Test
    public void testParseString3() {
        try {
            TexHexTriplet.parse("2f11");
            fail("Should have thrown exception");
        } catch(NumberFormatException e) {
            // win! :)
        }
    }

}
