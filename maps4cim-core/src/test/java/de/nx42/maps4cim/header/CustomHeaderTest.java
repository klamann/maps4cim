package de.nx42.maps4cim.header;


import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class CustomHeaderTest {

    @Test
    public void testHex() throws Exception {
        String input = "fd 77 fc b6 e8 fe fe";
        byte[] expecteds = { (byte) 0xfd, (byte) 0x77, (byte) 0xfc, (byte) 0xb6,
                            (byte) 0xe8, (byte) 0xfe, (byte) 0xfe };
        byte[] actuals = CustomHeader.hex(input);
        assertArrayEquals(expecteds, actuals);
    }

    @Test
    public void testInt24write() throws Exception {
        byte[] expecteds = { (byte) 0x00, (byte) 0x00, (byte) 0x03 };
        byte[] actuals = CustomHeader.int24write(3);
        assertArrayEquals(expecteds, actuals);
    }

    @Test
    public void testInt24write2() throws Exception {
        byte[] expecteds = { (byte) 0x0f, (byte) 0x35, (byte) 0xa2 };
        byte[] actuals = CustomHeader.int24write(996770);
        assertArrayEquals(expecteds, actuals);
    }

}
