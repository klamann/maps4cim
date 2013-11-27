package de.nx42.maps4cim.util.math;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathExtTest {

    @Test
    public void testRoundfDouble() throws Exception {
        String actual = MathExt.roundf(1.1234567);
        assertEquals("1.12", actual);
    }

    @Test
    public void testRoundfDouble2() throws Exception {
        String actual = MathExt.roundf(1.10);
        assertEquals("1.1", actual);
    }

    @Test
    public void testRoundfDoubleInt() throws Exception {
        String actual = MathExt.roundf(1.1234567, 3);
        assertEquals("1.123", actual);
    }

    @Test
    public void testRoundfDoubleInt2() throws Exception {
        String actual = MathExt.roundf(1.66666666666666, 6);
        assertEquals("1.666667", actual);
    }

    @Test
    public void testRoundfDoubleInt3() throws Exception {
        String actual = MathExt.roundf(15030.5, -3);
        assertEquals("15000.0", actual);
    }

}
