/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nx42.maps4cim.util.arr2d;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import de.nx42.maps4cim.util.arr2d.Arrays2D;
import de.nx42.maps4cim.util.arr2d.Bicubic;
import de.nx42.maps4cim.util.arr2d.Interpolation;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class BicubicTest {

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
        instance = new Bicubic(test);
    }

    @Test
    public void testInterpolateSampleX() {
        float[] samples = new float[]{0,1,2,3};

        assertEquals(1.0, instance.interpolateSampleX(samples, 0.0f), 0.001);
        assertEquals(1.5, instance.interpolateSampleX(samples, 0.5f), 0.001);
        assertEquals(2.0, instance.interpolateSampleX(samples, 1.0f), 0.001);
        assertEquals(1.333, instance.interpolateSampleX(samples, 0.333f), 0.001);
    }

    @Test
    public void testInterpolateSampleX2() {
        float[] samples = new float[]{2,1,2,1};

        assertEquals(1.0, instance.interpolateSampleX(samples, 0.0f), 0.001);
        assertEquals(1.5, instance.interpolateSampleX(samples, 0.5f), 0.001);
        assertEquals(2.0, instance.interpolateSampleX(samples, 1.0f), 0.001);
        assertEquals(1.258, instance.interpolateSampleX(samples, 0.333f), 0.01);
    }

    @Test
    public void testResize() {
        float[][] roundtrip = instance.resize(test.length, test[0].length);
//        System.out.println("\nRoundtrip:\n" + Arrays2D.print(roundtrip));
        InterpolationTest.assertArray2dEquals(test, roundtrip, 0.01f);
    }

    @Test
    public void testResize2() {
        float[][] expResult = {
            {1.00f, 0.95f, 0.94f, 1.20f, 1.70f, 2.03f, 2.00f, 1.97f, 2.30f, 2.80f, 3.06f, 3.05f, 3.00f,},
            {0.85f, 0.80f, 0.79f, 1.05f, 1.55f, 1.88f, 1.85f, 1.82f, 2.14f, 2.64f, 2.91f, 2.90f, 2.85f,},
            {0.83f, 0.78f, 0.77f, 1.03f, 1.53f, 1.86f, 1.83f, 1.79f, 2.12f, 2.62f, 2.88f, 2.88f, 2.83f,},
            {1.61f, 1.56f, 1.55f, 1.81f, 2.31f, 2.64f, 2.61f, 2.58f, 2.91f, 3.41f, 3.67f, 3.66f, 3.61f,},
            {3.11f, 3.06f, 3.05f, 3.31f, 3.81f, 4.14f, 4.11f, 4.08f, 4.41f, 4.91f, 5.17f, 5.16f, 5.11f,},
            {4.10f, 4.04f, 4.04f, 4.30f, 4.80f, 5.13f, 5.10f, 5.06f, 5.39f, 5.89f, 6.15f, 6.15f, 6.10f,},
            {4.00f, 3.95f, 3.94f, 4.20f, 4.70f, 5.03f, 5.00f, 4.97f, 5.30f, 5.80f, 6.06f, 6.05f, 6.00f,},
            {3.90f, 3.85f, 3.85f, 4.11f, 4.61f, 4.94f, 4.90f, 4.87f, 5.20f, 5.70f, 5.96f, 5.96f, 5.90f,},
            {4.89f, 4.84f, 4.83f, 5.09f, 5.59f, 5.92f, 5.89f, 5.86f, 6.19f, 6.69f, 6.95f, 6.94f, 6.89f,},
            {6.39f, 6.34f, 6.33f, 6.59f, 7.09f, 7.42f, 7.39f, 7.36f, 7.69f, 8.19f, 8.45f, 8.44f, 8.39f,},
            {7.17f, 7.12f, 7.12f, 7.38f, 7.88f, 8.21f, 8.17f, 8.14f, 8.47f, 8.97f, 9.23f, 9.22f, 9.17f,},
            {7.15f, 7.10f, 7.09f, 7.36f, 7.86f, 8.18f, 8.15f, 8.12f, 8.45f, 8.95f, 9.21f, 9.20f, 9.15f,},
            {7.00f, 6.95f, 6.94f, 7.20f, 7.70f, 8.03f, 8.00f, 7.97f, 8.30f, 8.80f, 9.06f, 9.05f, 9.00f}
        };

        float[][] result = instance.resize(test.length*2+1, test[0].length*2+1);
//        System.out.println("\nDouble Size:\n" + Arrays2D.print(result));
        InterpolationTest.assertArray2dEquals(expResult, result, 0.01f);
    }
}
