package de.nx42.maps4cim.util.arr2d;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.InputMismatchException;
import java.util.Locale;

import org.junit.Test;

public class Arrays2DTest {

	protected static final short[][] arr00 = new short[][] {
		{ 1, 1, 1, 1, 1 },
		{ 4, 5, 5, 5, 9 },
		{ 4, 5, 5, 5, 9 },
		{ 4, 5, 5, 5, 9 },
		{ 4, 9, 9, 9, 9 }
	};

	protected static final short[][] arr01 = new short[][] {
		{ 1, 1, 1, 1, 2 },
		{ 9, 6, 6, 6, 2 },
		{ 9, 6, 6, 6, 2 },
		{ 9, 6, 6, 6, 2 },
		{ 9, 9, 9, 9, 2 }
	};

	protected static final short[][] arr10 = new short[][] {
		{ 4, 9, 9, 9, 9 },
		{ 4, 7, 7, 7, 9 },
		{ 4, 7, 7, 7, 9 },
		{ 4, 7, 7, 7, 9 },
		{ 4, 3, 3, 3, 3 }
	};

	protected static final short[][] arr11 = new short[][] {
		{ 9, 9, 9, 9, 2 },
		{ 9, 8, 8, 8, 2 },
		{ 9, 8, 8, 8, 2 },
		{ 9, 8, 8, 8, 2 },
		{ 3, 3, 3, 3, 3 }
	};

	protected static final short[][] arr11fail = new short[][] {
		{ 9, 9, 8, 9, 2 },
		{ 9, 8, 8, 8, 2 },
		{ 9, 8, 8, 8, 2 },
		{ 9, 8, 8, 8, 2 },
		{ 3, 3, 3, 3, 3 }
	};

	protected static final short[][][][] wrap = new short[][][][] {
		{ arr00, arr01 },
		{ arr10, arr11 }
	};

	protected static final short[][][][] wrapFail = new short[][][][] {
		{ arr00, arr01 },
		{ arr10, arr11fail }
	};

	protected static final short[][] exp = new short[][] {
		{ 1, 1, 1, 1, 1, 1, 1, 1, 2 },
		{ 4, 5, 5, 5, 9, 6, 6, 6, 2 },
		{ 4, 5, 5, 5, 9, 6, 6, 6, 2 },
		{ 4, 5, 5, 5, 9, 6, 6, 6, 2 },
		{ 4, 9, 9, 9, 9, 9, 9, 9, 2 },
		{ 4, 7, 7, 7, 9, 8, 8, 8, 2 },
		{ 4, 7, 7, 7, 9, 8, 8, 8, 2 },
		{ 4, 7, 7, 7, 9, 8, 8, 8, 2 },
		{ 4, 3, 3, 3, 3, 3, 3, 3, 3 },
	};

	@Test
	public void testCombineShortArrayArrayArrayArrayIntBoolean() {
		short[][] actual = Arrays2D.combine(wrap, 1, true);
		assertArray2dEquals(exp, actual);
	}

	@Test
	public void testCombineShortArrayArrayArrayArrayIntIntIntBoolean() {
		try {
			short[][] actual = Arrays2D.combine(wrap, 5, 5, 1, true);
//			System.out.println(Arrays2D.print(actual));
			assertArray2dEquals(exp, actual);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testCombineShortArrayArrayArrayArrayIntIntIntBoolean2() {
		try {
			short[][] actual = Arrays2D.combine(wrapFail, 5, 5, 1, true);
			fail("Should have thrown InputMismatchException!");
		} catch(InputMismatchException e) {
			// exception caught, success!
		}
	}


	public static void assertArray2dEquals(short[][] expected, short[][] actual) {
		if (expected.length != actual.length || expected[0].length != actual[0].length) {
			fail("Arrays are of different size!");
		}
		for (int i = 0; i < actual.length; i++) {
			assertArrayEquals(expected[i], actual[i]);
		}
	}



	// print 2D arrays



    /**
     * Prints a 2D array to a simple String representation
     * @param arr2D the array to print
     * @return the String representation of this array
     */
    public static String print(short[][] arr2D) {
        StringBuilder sb = new StringBuilder(arr2D.length * arr2D[0].length * 3);
        for (short[] arr : arr2D) {
            for (short s : arr) {
                sb.append(s);
                sb.append(" ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Prints a 2D array to a simple String representation. Decimals are rounded
     * to two significant digits.
     * @param arr2D the array to print
     * @return the String representation of this array
     */
    public static String print(float[][] arr2D) {
        DecimalFormat f = new DecimalFormat("#0.00");
        StringBuilder sb = new StringBuilder(arr2D.length * arr2D[0].length * 3);
        for (float[] arr : arr2D) {
            for (float d : arr) {
                sb.append(f.format(d));
                sb.append("  ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }


    public static String printFloatArrInit(float[][] arr2D) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern("#0.00");

        StringBuilder sb = new StringBuilder(arr2D.length * arr2D[0].length * 3);

        sb.append("{\n");
        for (float[] arr : arr2D) {
            sb.append("\t{ ");
            for (float d : arr) {
                sb.append(df.format(d));
                sb.append("f, ");
            }
            sb.append("},\n");
        }
        sb.append("};");
        return sb.toString();
    }


}
