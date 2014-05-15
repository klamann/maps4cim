package de.nx42.maps4cim.util.math;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class StatisticsTest {

    protected static final double delta = 0.00000001;

    @Test
    public void testStatistics() {
        Object input = new double[]{ 1.0, 1.0, 3.5, 4.2, 5.5, 6.0, 7.0, 8.3, 8.5 };
        Statistics s = new Statistics(input);

        assertEquals(s.samples, 9);
        assertEquals(s.max, 8.5, delta);
        assertEquals(s.min, 1.0, delta);
        assertEquals(s.mean, 5.0, delta);
        assertEquals(s.median, 5.5, delta);
        assertEquals(s.midrange, 4.75, delta);
        assertEquals(s.range, 7.5, delta);
        assertEquals(s.lowerQuartile, 3.5, delta);
        assertEquals(s.upperQuartile, 7.0, delta);
    }

    private static final byte maxByte = (byte) 0xff;
    private static final short maxShort = (short) 0xffff;
    private static final int maxInt = 0xffffffff;
    private static final long maxLong = 0xffffffffffffffffL;

    @Test
    public void testGetDoubleArrayDouble() {
        double[] expecteds = { 3.0, 1.0, 5.5, -3.8, Math.PI };
        Object input = expecteds;
        double[] actuals = Statistics.getDoubleArray(input);

        // the resulting array is always a deep copy
        assertFalse(expecteds == actuals);
        // yet they should be the same!
        assertArrayEquals(expecteds, actuals, 0.000000001);
    }

    @Test
    public void testGetDoubleArrayByte() {
        double[] expecteds = { 3, 1, 5, maxByte };
        Object input = new byte[]{ 3, 1, 5, maxByte };
        testGetDoubleArray(input, expecteds);
    }

    @Test
    public void testGetDoubleArrayShort() {
        double[] expecteds = { 3, 1, 5, maxShort };
        Object input = new short[]{ 3, 1, 5, maxShort };
        testGetDoubleArray(input, expecteds);
    }

    @Test
    public void testGetDoubleArrayInt() {
        double[] expecteds = { 3, 1, 5, maxInt };
        Object input = new int[]{ 3, 1, 5, maxInt };
        testGetDoubleArray(input, expecteds);
    }

    @Test
    public void testGetDoubleArrayLong() {
        double[] expecteds = { 3, 1, 5, maxLong };
        Object input = new long[]{ 3, 1, 5, maxLong };
        testGetDoubleArray(input, expecteds);
    }

    @Test
    public void testGetDoubleArrayFloat() {
        double[] expecteds = { 3.0, 1.0, 5.5, -3.8, Math.PI };
        Object input = new float[]{ 3.0f, 1.0f, 5.5f, -3.8f, (float) Math.PI };
        testGetDoubleArray(input, expecteds);
    }




    private void testGetDoubleArray(Object input, double[] expecteds) {
        double[] actuals = Statistics.getDoubleArray(input);
        assertArrayEquals(expecteds, actuals, 0.000001);
    }



}
