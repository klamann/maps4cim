package de.nx42.maps4cim.util.arr2d;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ImageJInterpolationTest {

    private static final float delta = 0.0000001f;

    protected static final float[][] arr2d = new float[][] {
        { 0, 1, 2, 3 },
        { 4, 5, 6, 7 },
        { 8, 9, 0xA, 0xB }
    };

    protected static final float[] arr1d = new float[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xA, 0xB
    };



    @Test
    public void testLinearize2DArray() throws Exception {
        float[] expecteds = arr1d;
        float[] actuals = ImageJInterpolation.linearize2DArray(arr2d);
        assertArrayEquals(expecteds, actuals, delta);
    }

    @Test
    public void testLinearizedArrayTo2D() throws Exception {
        float[][] expecteds = arr2d;
        float[][] actuals = ImageJInterpolation.linearizedArrayTo2D(arr1d, 4, 3);
        assertArray2dEquals(expecteds, actuals);
    }



    public static void assertArray2dEquals(float[][] expecteds, float[][] actuals) {
        if (expecteds.length != actuals.length || expecteds[0].length != actuals[0].length) {
            fail("Arrays are of different size!");
        }
        for (int i = 0; i < actuals.length; i++) {
            assertArrayEquals(expecteds[i], actuals[i], delta);
        }
    }


}
