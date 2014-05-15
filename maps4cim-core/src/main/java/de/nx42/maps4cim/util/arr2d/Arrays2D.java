/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 Sebastian Straub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nx42.maps4cim.util.arr2d;

import java.util.Arrays;
import java.util.InputMismatchException;

/**
 * Several helper functions for the handling of two-dimensional arrays.
 *
 * If not mentioned otherwise, arrays are addressed in row-major order
 * <https://en.wikipedia.org/wiki/Row-major_order>
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Arrays2D {

    /**
     * Copies a subset of the specified array into a new array, with the defined
     * indices as boundaries. copies a quadratic subset with the same index
     * values for each corner.
     * For indices out of bounds, zero values are filled in
     * @param input the 2d-array to copy from
     * @param topleft the index of the top row and the left column to copy from
     *        (e.g. input[3][3])
     * @param bottomright the index of the bottom row and the right column to
     *        copy from (e.g. input[6][6])
     * @return a new 2d-array which is a subset of the input-array with the specified
     * boundaries.
     */
    public static float[][] copyOfRange(float[][] input, int topleft, int bottomright) {
        return copyOfRange(input, topleft, bottomright, topleft, bottomright);
    }

    /**
     * Copies a subset of the specified array into a new array, with the defined
     * indices as boundaries.
     * For indices out of bounds, zero values are filled in
     * @param input the 2d-array to copy from
     * @param top the index of the topmost row to copy (e.g. input[3][x])
     * @param right the index of the last column to the right to copy (e.g. input[y][5])
     * @param bottom the index of the lowest row to copy (e.g. input[6][x])
     * @param left the index of the first column to the left to copy (e.g. input[y][0])
     * @return a new 2d-array which is a subset of the input-array with the specified
     * boundaries.
     */
    public static float[][] copyOfRange(float[][] input,
            int top, int right, int bottom, int left) {

        int height = bottom - top;
        int width = right - left;

        float[][] result = new float[height][width];
        for (int i = 0; i < result.length; i++) {
            int iOuter = i+top;
            if(iOuter < input.length) {
                // copy array
                result[i] = Arrays.copyOfRange(input[iOuter], left, right);
            } else {
                // fill with 0s if index out of bounds (copy behaviour of Arrays.copyOfRange)
                result[i] = new float[width];
            }
        }

        return result;
    }


    /**
     * Combines the input 2d-arrays into a single 2d array.
     * @see Arrays2D#combine(short[][][][], int, int, int, boolean)
     * @param wrapper the wrapper for the actual 2d-arrays. Addressed as
     *                {@code wrapper[wrapperY][wrapperX][y][x] }
     * @return the single combined 2d-array of all input arrays
     * @throws InputMismatchException if the overlapping tiles do not match
     */
    public static short[][] combine(short[][][][] wrapper) throws InputMismatchException {
    	return combine(wrapper, 0, false);
    }

    /**
     * Combines the input 2d-arrays into a single 2d array.
     * @see Arrays2D#combine(short[][][][], int, int, int, boolean)
     * @param wrapper the wrapper for the actual 2d-arrays. Addressed as
     *                {@code wrapper[wrapperY][wrapperX][y][x] }
     * @param width the width (extent in x-direction) of the containing arrays,
     *              including overlap.
     * @param height the height (extent in y-direction) of the containing arrays,
     *               including overlap
     * @return the single combined 2d-array of all input arrays
     * @throws InputMismatchException if the overlapping tiles do not match
     */
    public static short[][] combine(short[][][][] wrapper, int width, int height)
            throws InputMismatchException {
    	return combine(wrapper, width,height, 0, false);
    }

    /**
     * Combines the input 2d-arrays into a single 2d array.
     * @see Arrays2D#combine(short[][][][], int, int, int, boolean)
     * @param wrapper the wrapper for the actual 2d-arrays. Addressed as
     *                {@code wrapper[wrapperY][wrapperX][y][x] }
     * @param overlap # of elements at the borders that overlap with neighboring
     *                arrays
     * @param validate throws an exception, if the overlapping parts of the arrays
     *                 do not match exactly. Otherwise, the value from the first
     *                 array (LTR, top to bottom) is chosen.
     * @return the single combined 2d-array of all input arrays
     * @throws InputMismatchException if the overlapping tiles do not match
     */
    public static short[][] combine(short[][][][] wrapper, int overlap, boolean validate)
            throws InputMismatchException {
    	return combine(wrapper, wrapper[0][0][0].length, wrapper[0][0].length,
    	               overlap, validate);
    }

    /**
     * Combines the 2d-arrays that are arranged in a 2d-array of arrays into a
     * single large 2d-array that contains all data points of it's components
     * at the respective position in the wrapper.
     * e.g.
     * <pre>
     * +---+---+       +-------+
     * | a | b |       | a   b |
     * +---+---+  -->  |       |
     * | c | d |       | c   d |
     * +---+---+       +-------+
     * </pre>
     * a,b,c,d are equally-sized 2d-arrays that are arranged in a 2x2 2d-array
     * (the wrapper). The contents of a,b,c,d are written in a single 2d-array
     * so that the values resemble the original position inside the wrapper
     * @param wrapper the wrapper for the actual 2d-arrays. Addressed as
     *                {@code wrapper[wrapperY][wrapperX][y][x] }
     * @param width the width (extent in x-direction) of the containing arrays,
     *              including overlap.
     * @param height the height (extent in y-direction) of the containing arrays,
     *               including overlap
     * @param overlap # of elements at the borders that overlap with neighboring
     *                arrays
     * @param validate throws an exception, if the overlapping parts of the arrays
     *                 do not match exactly. Otherwise, the value from the first
     *                 array (LTR, top to bottom) is chosen.
     * @return the single combined 2d-array of all input arrays
     * @throws InputMismatchException if the overlapping tiles do not match
     */
    public static short[][] combine(short[][][][] wrapper, int width, int height,
            int overlap, boolean validate) throws InputMismatchException {

    	int lenY = wrapper.length;
    	int lenX = wrapper[0].length;
    	int combHeight = lenY * (height - overlap) + overlap;
    	int combWidth  = lenX * (width  - overlap) + overlap;

    	short[][] combined = new short[combHeight][combWidth];

    	for (int wY = 0; wY < lenY; wY++) {
			for (int wX = 0; wX < lenX; wX++) {
				// get the current subarray from the wrapper
				short[][] subArr = wrapper[wY][wX];

				// calculate coordinates in source and destination array
				int top = calculateOffset(height, overlap, wY);
				int left = calculateOffset(width, overlap, wX);
				int extY = wY == 0 ? height : height-1;
				int extX = wX == 0 ? width  : width -1;
				int startY = wY == 0 ? 0 : overlap;
				int startX = wX == 0 ? 0 : overlap;

				// validate overlap
				if(validate && overlap > 0) {
					if(wY > 0) {
						validateAbove(subArr, wrapper[wY-1][wX], overlap);
					}
					if(wX > 0) {
						validateLeft(subArr, wrapper[wY][wX-1], overlap);
					}
				}

				// actual copy
				for (int y = 0; y < extY; y++) {
					for (int x = 0; x < extX; x++) {
						combined[top+y][left+x] = subArr[startY+y][startX+x];
					}
				}
			}
		}

    	return combined;
    }

    /**
     * Calculates the offset of a sub-array within the new large combined
     * array.
     * 
     * @param arrLen the overall length of the array
     * @param overlap the length of the overlapping parts between the arrays
     * @param idx the 0-based index of the current sub-array within the count
     * of all arrays that are to be combined
     * @return the starting index within the new combined array of an arbitrary
     * sub-array (defined by idx)
     */
    private static int calculateOffset(int arrLen, int overlap, int idx) {
    	return idx == 0 ? 0 : arrLen + (idx-1) * (arrLen - overlap);
    }

    /**
     * Validates the overlapping parts of two sub-arrays.
     * If arrays with an overlapping part are combined, this function can be
     * used to ensure, that the upper part of the current array is the same
     * as the lower part of the array above it.
     * Throws an InputMismatchException, if the overlapping parts do not match
     * exactly
     * @param current the current array (to validate)
     * @param above the array above the current array (y-index -1)
     * @param overlap the number of overlapping data rows
     */
    private static void validateAbove(short[][] current, short[][] above, int overlap)
            throws InputMismatchException {
    	for (int i = 0; i < overlap; i++) {
			short[] yAbove = above[above.length-1-i];
			short[] yCurrent = current[i];
			for (int j = 0; j < yCurrent.length; j++) {
				if(yAbove[j] != yCurrent[j]) {
					throw new InputMismatchException("The overlapping parts of "
					        + "the current array and the one above do not match!");
				}
			}
		}
    }

    /**
     * Validates the overlapping parts of two sub-arrays.
     * If arrays with an overlapping part are combined, this function can be
     * used to ensure, that the left side of the current array is the same
     * as the right side of the array left of it.
     * Throws an InputMismatchException, if the overlapping parts do not match
     * exactly
     * @param current the current array (to validate)
     * @param left the array to the left of the current array (x-index -1)
     * @param overlap the number of overlapping data columns
     */
    private static void validateLeft(short[][] current, short[][] left, int overlap) throws InputMismatchException {
    	for (int x = 0; x < overlap; x++) {
			for (int y = 0; y < current.length; y++) {
				if(current[y][x] != left[y][left[y].length-1-x]) {
					throw new InputMismatchException("The overlapping parts of "
					        + "the current array and the one to the left do not match!");
				}
			}
		}
    }

}
