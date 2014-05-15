package de.nx42.maps4cim.util.arr2d;


import static org.junit.Assert.fail;

import org.junit.Test;

public class GapInterpolatorTest {

    protected static final short g = -9;
    protected static final short[][] testArr = new short[][]{
        { g,1,1,2,2,2 },
        { 1,1,g,g,2,2 },
        { 1,1,g,2,2,2 },
        { 2,2,g,3,3,3 },
        { 2,2,2,3,g,3 },
        { 2,2,g,3,g,g }
    };

    protected static final short[][] testArr2 = new short[][]{
        { 1,1,1,1,1,1,2,2,2,2,2,2 },
        { 1,1,1,g,1,1,2,2,2,2,2,2 },
        { 1,1,1,g,1,1,2,2,2,2,2,2 },
        { 1,1,1,1,1,1,2,g,g,g,g,g },
        { g,1,1,1,1,1,2,2,2,2,2,2 },
        { g,g,1,1,1,1,2,2,2,2,2,2 },
        { g,g,2,2,2,2,3,3,3,3,3,3 },
        { g,g,2,2,2,2,3,3,3,3,3,3 },
        { g,g,g,2,2,2,3,g,g,g,g,3 },
        { g,g,2,2,2,2,g,g,g,g,g,g },
        { g,g,2,2,2,2,3,g,g,g,g,3 },
        { 2,2,2,2,2,2,3,3,g,g,3,3 }
    };

    protected static final short[][] testArr3 = new short[][]{
        { 1,1,1,1,1,1,2,2,2,2,2,2 },
        { 1,1,1,g,g,g,g,g,g,g,2,2 },
        { 1,1,1,g,g,g,g,g,g,g,2,2 },
        { 1,1,1,g,g,g,g,g,g,g,g,g },
        { g,1,g,g,g,g,g,g,g,2,2,2 },
        { g,g,g,g,g,g,g,g,g,2,2,2 },
        { g,g,g,g,g,g,g,g,g,3,3,3 },
        { g,g,g,g,g,g,g,g,g,3,3,3 },
        { g,g,g,g,g,g,g,g,g,g,g,3 },
        { g,g,g,g,g,g,g,g,g,g,g,3 },
        { g,g,2,2,2,2,3,g,g,g,g,3 },
        { 2,2,2,2,2,2,3,3,g,g,3,3 }
    };


    @Test
    public void testFullArray() {
        try {
            GapInterpolator gip = new GapInterpolator(g);
            float[][] result = copy(testArr3);
            for (int y = 0; y < result.length; y++) {
                for (int x = 0; x < result[0].length; x++) {
                    if(result[y][x] == g) {
                        result[y][x] = gip.star(testArr3, x, y);
                    }
                }
            }
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }

	@Test
	public void testStar() throws Exception {
		GapInterpolator gip = new GapInterpolator(g);
		float res = gip.star(testArr, 5, 5);
	}

    protected static float[][] copy(short[][] input) {
        float[][] output = new float[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                output[i][j] = input[i][j];
            }
        }
        return output;
    }

}
