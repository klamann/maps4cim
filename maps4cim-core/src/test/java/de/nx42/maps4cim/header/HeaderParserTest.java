package de.nx42.maps4cim.header;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HeaderParserTest {


    @Test
    public void testParseRoundTrip() throws Exception {
        CustomHeader header = new CustomHeader();
        byte[] expecteds = header.generateHeader();
        byte[] actuals = HeaderParser.parse(expecteds).generateHeader();
        assertArrayEquals(expecteds, actuals);
    }


    protected static final byte[] inputGap = { 1, 5, 0, 0, 3, 4, 0, 0, 0, 1, 2};

    @Test
    public void testReadToGap() throws Exception {
        int expected = 2;
        int actual = HeaderParser.readToGap(inputGap, 0, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void testReadToGap2() throws Exception {
        int expected = 6;
        int actual = HeaderParser.readToGap(inputGap, 0, 3);
        assertEquals(expected, actual);
    }

    @Test
    public void testReadToGap3() throws Exception {
        int expected = 6;
        int actual = HeaderParser.readToGap(inputGap, 3, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void testReadToGap4() throws Exception {
        int expected = -1;
        int actual = HeaderParser.readToGap(inputGap, 0, 4);
        assertEquals(expected, actual);
    }

    @Test
    public void testReadAfterGap() throws Exception {
        int expected = 4;
        int actual = HeaderParser.readAfterGap(inputGap, 0, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void testReadAfterGap2() throws Exception {
        int expected = 9;
        int actual = HeaderParser.readAfterGap(inputGap, 3, 2);
        assertEquals(expected, actual);
    }



    /** þþ....P.l.a.y.e.r.D.a.t.a....0.1.-.E.m.p.t.y..Œ‰ */
    protected static final byte[] inputString = {
        (byte)0xFE, (byte)0xFE, (byte)0x00, (byte)0x00, (byte)0x0A, (byte)0x00,
        (byte)0x50, (byte)0x00, (byte)0x6C, (byte)0x00, (byte)0x61, (byte)0x00,
        (byte)0x79, (byte)0x00, (byte)0x65, (byte)0x00, (byte)0x72, (byte)0x00,
        (byte)0x44, (byte)0x00, (byte)0x61, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x61, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x30,
        (byte)0x00, (byte)0x31, (byte)0x00, (byte)0x2D, (byte)0x00, (byte)0x45,
        (byte)0x00, (byte)0x6D, (byte)0x00, (byte)0x70, (byte)0x00, (byte)0x74,
        (byte)0x00, (byte)0x79, (byte)0x00, (byte)0x06, (byte)0x8C, (byte)0x89
    };

    @Test
    public void testReadToString() throws Exception {
        int expected = 2;
        int actual = HeaderParser.readToString(inputString, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void testReadToString2() throws Exception {
        int expected = 25;
        int actual = HeaderParser.readToString(inputString, 7);
        assertEquals(expected, actual);
    }

    @Test
    public void testReadAfterStringByteArrayInt() throws Exception {
        int expected = 25;
        int actual = HeaderParser.readAfterString(inputString, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void testReadAfterStringByteArrayInt2() throws Exception {
        int expected = 44;
        int actual = HeaderParser.readAfterString(inputString, 7);
        assertEquals(expected, actual);
    }

    @Test
    public void testReadAfterStringByteArrayIntString() throws Exception {
        int expected = 25;
        int actual = HeaderParser.readAfterString(inputString, 0, "PlayerData");
        assertEquals(expected, actual);
    }

    @Test
    public void testReadAfterStringByteArrayIntString2() throws Exception {
        int expected = 44;
        int actual = HeaderParser.readAfterString(inputString, 0, "01-Empty");
        assertEquals(expected, actual);
    }

    @Test
    public void testReadAfterStringByteArrayIntString3() throws Exception {
        int expected = -1;
        int actual = HeaderParser.readAfterString(inputString, 15, "PlayerData");
        assertEquals(expected, actual);
    }

    @Test
    public void testReadAfterBytes() throws Exception {
        int expected = 23;
        int actual = HeaderParser.readAfterBytes(inputString, 0, new byte[]{ (byte)0x61, (byte)0x00, (byte)0x74 });
        assertEquals(expected, actual);
    }



    @Test
    public void testInt24parse() throws Exception {
        byte[] input = { (byte) 0x0f, (byte) 0x35, (byte) 0xa2 };
        int expected = 996770;
        int actual = HeaderParser.int24parse(input);
        assertEquals(expected, actual);
    }

    @Test
    public void testInt24roundtrip() throws Exception {
        int expected = 42 * 1337 + 0xF0000;
        byte[] int24 = CustomHeader.int24write(expected);
        int actual = HeaderParser.int24parse(int24);
        assertEquals(expected, actual);
    }

    @Test
    public void parseHeaderString() throws Exception {
        String expected = "test";
        byte[] converted = CustomHeader.formatHeaderString(expected);
        String actual = HeaderParser.parseHeaderString(converted, 0);
        assertEquals(expected, actual);
    }

    @Test
    public void parseHeaderString2() throws Exception {
        String expected = "PlayerData";
        String actual = HeaderParser.parseHeaderString(inputString, 2);
        assertEquals(expected, actual);
    }


}
