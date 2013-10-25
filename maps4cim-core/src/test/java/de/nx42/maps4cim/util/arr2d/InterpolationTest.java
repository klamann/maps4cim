/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nx42.maps4cim.util.arr2d;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import de.nx42.maps4cim.util.arr2d.Arrays2D;
import de.nx42.maps4cim.util.arr2d.Interpolation;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class InterpolationTest {

    protected static Interpolation instance;
    protected static  float[][] test = new float[][] {
            { 1, 1, 2, 2, 3, 3 },
            { 1, 1, 2, 2, 3, 3 },
            { 4, 4, 5, 5, 6, 6 },
            { 4, 4, 5, 5, 6, 6 },
            { 7, 7, 8, 8, 9, 9 },
            { 7, 7, 8, 8, 9, 9 },
        };

    @BeforeClass
    public static void setUpClass() {
        instance = new InterpolationTest.InterpolationImpl(test);
    }

    // tests

    @Test
    public void testInterpolateSampleX() {
        float[] samples = new float[]{0,1,2,3};

        assertEquals(1.0, instance.interpolateSampleX(samples, 0.0f), 0.001);
        assertEquals(1.5, instance.interpolateSampleX(samples, 0.5f), 0.001);
        assertEquals(2.0, instance.interpolateSampleX(samples, 1.0f), 0.001);
        assertEquals(1.333, instance.interpolateSampleX(samples, 0.333f), 0.001);

    }

    /**
     * Test of interpolateX method, of class Interpolation.
     */
    @Test
    public void testInterpolateX() {
        float[] arr = new float[]{0,1,2,3,4,5,6};

        // allowed values
        assertEquals(0.0 , instance.interpolateX(arr, 0.0f , 0), 0.001);
        assertEquals(0.5 , instance.interpolateX(arr, 0.5f , 0), 0.001);
        assertEquals(1.3 , instance.interpolateX(arr, 0.3f , 1), 0.001);
        assertEquals(3.0 , instance.interpolateX(arr, 0.0f , 3), 0.001);
        assertEquals(4.99, instance.interpolateX(arr, 0.99f, 4), 0.001);
        assertEquals(5.5 , instance.interpolateX(arr, 0.5f , 5), 0.001);
        assertEquals(6.0 , instance.interpolateX(arr, 0.0f , 6), 0.001);

        // index out of bounds
        try {
            instance.interpolateX(arr, 0.5f, 6);
            fail("Method should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) { }
        try {
            instance.interpolateX(arr, 0.0f, 8);
            fail("Method should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException ex) { }

    }

    @Test
    public void testInterpolateSampleY() {
        testInterpolateSampleX();
    }

    @Test
    public void testInterpolateY() {
        testInterpolateX();
    }

    @Test
    public void testInterpolateSample() {
        float[][] samples = new float[][] {
            { 1, 1, 2, 2, },
            { 1, 1, 2, 2, },
            { 4, 4, 5, 5, },
            { 4, 4, 5, 5  }
        };

        assertEquals(1.0 ,instance.interpolateSample(samples, 0.0f, 0.0f), 0.001);
        assertEquals(2.0 ,instance.interpolateSample(samples, 1.0f, 0.0f), 0.001);
        assertEquals(4.0 ,instance.interpolateSample(samples, 0.0f, 1.0f), 0.001);
        assertEquals(5.0 ,instance.interpolateSample(samples, 1.0f, 1.0f), 0.001);

        assertEquals(3.0 ,instance.interpolateSample(samples, 0.5f, 0.5f), 0.001);

        assertEquals(2.0 ,instance.interpolateSample(samples, 0.25f, 0.25f), 0.001);
        assertEquals(2.5 ,instance.interpolateSample(samples, 0.75f, 0.25f), 0.001);
        assertEquals(3.5 ,instance.interpolateSample(samples, 0.25f, 0.75f), 0.001);
        assertEquals(4.0 ,instance.interpolateSample(samples, 0.75f, 0.75f), 0.001);

    }

    @Test
    public void testInterpolate_5args() {
        assertEquals(1.0 ,instance.interpolate(test, 0.0f, 0.0f, 0, 0), 0.001);
        assertEquals(3.0 ,instance.interpolate(test, 0.0f, 0.0f, 5, 0), 0.001);
        assertEquals(7.0 ,instance.interpolate(test, 0.0f, 0.0f, 0, 5), 0.001);
        assertEquals(9.0 ,instance.interpolate(test, 0.0f, 0.0f, 5, 5), 0.001);

        assertEquals(2.0 ,instance.interpolate(test, 0.0f, 0.0f, 2, 1), 0.001);
        assertEquals(8.0 ,instance.interpolate(test, 0.0f, 0.0f, 3, 4), 0.001);
        assertEquals(6.0 ,instance.interpolate(test, 0.0f, 0.0f, 5, 2), 0.001);

        assertEquals(5.0 ,instance.interpolate(test, 0.5f, 0.5f, 2, 2), 0.001);
        assertEquals(1.5 ,instance.interpolate(test, 0.5f, 0.5f, 1, 0), 0.001);
        assertEquals(3.0 ,instance.interpolate(test, 0.5f, 0.5f, 1, 1), 0.001);
        assertEquals(8.0 ,instance.interpolate(test, 0.75f, 0.75f, 3, 3), 0.001);

    }

    @Test
    public void testInterpolate_3args() {
        assertEquals(1.0 ,instance.interpolate(test, 0.0f, 0.0f), 0.001);
        assertEquals(3.0 ,instance.interpolate(test, 5.0f, 0.0f), 0.001);
        assertEquals(7.0 ,instance.interpolate(test, 0.0f, 5.0f), 0.001);
        assertEquals(9.0 ,instance.interpolate(test, 5.0f, 5.0f), 0.001);

        assertEquals(2.0 ,instance.interpolate(test, 2.0f, 1.0f), 0.001);
        assertEquals(8.0 ,instance.interpolate(test, 3.0f, 4.0f), 0.001);
        assertEquals(6.0 ,instance.interpolate(test, 5.0f, 2.0f), 0.001);

        assertEquals(5.0 ,instance.interpolate(test, 2.5f, 2.5f), 0.001);
        assertEquals(1.5 ,instance.interpolate(test, 1.5f, 0.5f), 0.001);
        assertEquals(3.0 ,instance.interpolate(test, 1.5f, 1.5f), 0.001);
        assertEquals(8.0 ,instance.interpolate(test, 3.75f, 3.75f), 0.001);
    }

    @Test
    public void testAdjustOffset() {
        assertEquals(1, Interpolation.adjustOffset(2, 5, 0));
        assertEquals(2, Interpolation.adjustOffset(2, 5, 1));
        assertEquals(3, Interpolation.adjustOffset(2, 5, 2));
        assertEquals(4, Interpolation.adjustOffset(2, 5, 3));

        assertEquals(0, Interpolation.adjustOffset(0, 5, 0));
        assertEquals(0, Interpolation.adjustOffset(0, 5, 1));
        assertEquals(1, Interpolation.adjustOffset(0, 5, 2));
        assertEquals(2, Interpolation.adjustOffset(0, 5, 3));

        assertEquals(3, Interpolation.adjustOffset(4, 5, 0));
        assertEquals(4, Interpolation.adjustOffset(4, 5, 1));
        assertEquals(5, Interpolation.adjustOffset(4, 5, 2));
        assertEquals(5, Interpolation.adjustOffset(4, 5, 3));
    }

    @Test
    public void testGetSigFig() {
        assertEquals(0.0, Interpolation.getSigFig(0.0f), 0.0001);
        assertEquals(0.0, Interpolation.getSigFig(5.0f), 0.0001);

        assertEquals(0.5, Interpolation.getSigFig(0.5f), 0.0001);
        assertEquals(0.5, Interpolation.getSigFig(5.5f), 0.0001);

        assertEquals(0.37125, Interpolation.getSigFig(7.37125f), 0.0001);
        assertEquals(Math.PI-3, Interpolation.getSigFig((float) Math.PI), 0.0001);
    }

    @Test
    public void testResize() {
        float[][] roundtrip = instance.resize(test.length, test[0].length);
//        System.out.println("\nRoundtrip:\n" + Arrays2D.print(roundtrip));
        assertArray2dEquals(test, roundtrip, 0.01f);

    }

    @Test
    public void testResize2() {
        float[][] expResult = {
            {1.00f, 1.00f, 1.00f, 1.25f, 1.67f, 2.00f, 2.00f, 2.00f, 2.33f, 2.75f, 3.00f, 3.00f, 3.00f,},
            {1.00f, 1.00f, 1.00f, 1.25f, 1.67f, 2.00f, 2.00f, 2.00f, 2.33f, 2.75f, 3.00f, 3.00f, 3.00f,},
            {1.00f, 1.00f, 1.00f, 1.25f, 1.67f, 2.00f, 2.00f, 2.00f, 2.33f, 2.75f, 3.00f, 3.00f, 3.00f,},
            {1.75f, 1.75f, 1.75f, 2.00f, 2.42f, 2.75f, 2.75f, 2.75f, 3.08f, 3.50f, 3.75f, 3.75f, 3.75f,},
            {3.00f, 3.00f, 3.00f, 3.25f, 3.67f, 4.00f, 4.00f, 4.00f, 4.33f, 4.75f, 5.00f, 5.00f, 5.00f,},
            {4.00f, 4.00f, 4.00f, 4.25f, 4.67f, 5.00f, 5.00f, 5.00f, 5.33f, 5.75f, 6.00f, 6.00f, 6.00f,},
            {4.00f, 4.00f, 4.00f, 4.25f, 4.67f, 5.00f, 5.00f, 5.00f, 5.33f, 5.75f, 6.00f, 6.00f, 6.00f,},
            {4.00f, 4.00f, 4.00f, 4.25f, 4.67f, 5.00f, 5.00f, 5.00f, 5.33f, 5.75f, 6.00f, 6.00f, 6.00f,},
            {5.00f, 5.00f, 5.00f, 5.25f, 5.67f, 6.00f, 6.00f, 6.00f, 6.33f, 6.75f, 7.00f, 7.00f, 7.00f,},
            {6.25f, 6.25f, 6.25f, 6.50f, 6.92f, 7.25f, 7.25f, 7.25f, 7.58f, 8.00f, 8.25f, 8.25f, 8.25f,},
            {7.00f, 7.00f, 7.00f, 7.25f, 7.67f, 8.00f, 8.00f, 8.00f, 8.33f, 8.75f, 9.00f, 9.00f, 9.00f,},
            {7.00f, 7.00f, 7.00f, 7.25f, 7.67f, 8.00f, 8.00f, 8.00f, 8.33f, 8.75f, 9.00f, 9.00f, 9.00f,},
            {7.00f, 7.00f, 7.00f, 7.25f, 7.67f, 8.00f, 8.00f, 8.00f, 8.33f, 8.75f, 9.00f, 9.00f, 9.00f}
        };

        float[][] result = instance.resize(test.length*2+1, test[0].length*2+1);
//        System.out.println("\nDouble Size:\n" + Arrays2D.print(result));
        assertArray2dEquals(expResult, result, 0.01f);

    }

    @Test
    public void testCrop() {
        float top = 1.0F;
        float right = 4.0F;
        float bottom = 3.5F;
        float left = 1.5F;

        float[][] expResult = {
            {1.00f, 2.00f, 2.00f,},
            {4.00f, 5.00f, 5.00f,},
            {4.00f, 5.00f, 5.00f,}
        };

        float[][] result = instance.crop(top, right, bottom, left);
//        System.out.println("\nCrop:\n" + Arrays2D.print(result));
        assertArray2dEquals(expResult, result, 0.0001f);
    }

    @Test
    public void testCropAndResize() {
        int lenX = 4;
        int lenY = 4;
        float minX = 1.0F;
        float minY = 1.0F;
        float maxX = 4.0F;
        float maxY = 4.0F;

        float[][] expResult = new float[][] {
            { 1, 2, 2, 3 },
            { 4, 5, 5, 6 },
            { 4, 5, 5, 6 },
            { 7, 8, 8, 9 }
        };

        float[][] result = instance.cropAndResize(lenX, lenY, minX, minY, maxX, maxY);
//        System.out.println("\nResized Subset:\n" + Arrays2D.print(result));
        assertArray2dEquals(expResult, result, 0.0001f);
    }

    @Test
    public void testCropAndResize2() {
        int lenX = 6;
        int lenY = 6;
        float minX = 1.5F;
        float minY = 1.5F;
        float maxX = 4.5F;
        float maxY = 4.5F;

        float[][] expResult = new float[][]{
            {3.0f, 3.5f, 3.5f, 3.8f, 4.4f, 4.5f,},
            {4.5f, 5.0f, 5.0f, 5.3f, 5.9f, 6.0f,},
            {4.5f, 5.0f, 5.0f, 5.3f, 5.9f, 6.0f,},
            {5.4f, 5.9f, 5.9f, 6.2f, 6.8f, 6.9f,},
            {7.2f, 7.7f, 7.7f, 8.0f, 8.6f, 8.7f,},
            {7.5f, 8.0f, 8.0f, 8.3f, 8.9f, 9.0f }
        };

        float[][] result = instance.cropAndResize(lenX, lenY, minX, minY, maxX, maxY);
//        System.out.println("\nResized Subset:\n" + Arrays2D.print(result));
        assertArray2dEquals(expResult, result, 0.0001f);
    }



	public static void assertArray2dEquals(float[][] expecteds, float[][] actuals, float delta) {
		if (expecteds.length != actuals.length || expecteds[0].length != actuals[0].length) {
			fail("Arrays are of different size!");
		}

		for (int i = 0; i < actuals.length; i++) {
			assertArrayEquals(expecteds[i], actuals[i], delta);
		}
	}

    public static class InterpolationImpl extends Interpolation {

        public InterpolationImpl(float[][] test) {
            super(test);
        }

        @Override
        public float interpolateSampleX(float[] samples, float fracX) {
            return samples[1] + (samples[2] - samples[1]) * fracX;
        }
    }
}
