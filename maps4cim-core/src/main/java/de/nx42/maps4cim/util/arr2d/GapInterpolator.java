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

/**
 * The gap interpolator shall provide functions to fill gaps within arbitrary
 * two-dimensional data sets.
 * It is a rudimentary solution designed to remove small gaps from the SRTM
 * data set, mixing bicubic interpolation with some custom hacks to create
 * an algorithm that works pretty well on small gaps surrounded by high quality
 * data points.
 * For larger gaps, strange results can appear. As this gap interpolator is
 * based on trial and error rather than solid math, it is best to use it with
 * caution or to read some papers and write something better... ;)
 *
 * If not mentioned otherwise, arrays are addressed in row-major order
 * <https://en.wikipedia.org/wiki/Row-major_order>
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class GapInterpolator {

    /** Interpolation implementation, used to fill gaps */
    protected static final Interpolation igap = new Bicubic(new float[1][1]);

    /**
     * value that indicates a gap. static access required for enums
     * (yeah, that's an ugly hack...)
     */
    protected static short gap;

    /** value that is returned when no other valid values are found */
    protected static final int notfound = -1;

    /**
     * max iterations while searching new values for interpolation. Needs to
     * be capped for sources with very large gaps...
     */
    protected int limit = 64;

    /**
     * Creates a new gap interpolator with the specified value indicating
     * a gap in the dataset
     * @param gap the value that indicates a gap
     */
    public GapInterpolator(short gap) {
        GapInterpolator.gap = gap;
    }

    /**
     * Area-Algorithm to derive the value of a missing data point from
     * surrounding data.
     * Creates a 4x4-array of valid values around the missing data point and
     * uses these values as input for the bicubic interpolation algorithm,
     * which calculates the value for the missing data point.
     *
     * {@link GapInterpolator#star(short[][], int, int)} should be preferred
     * over this function, as it creates more realistic results...
     *
     * @param arr the full array where missing data can be interpoalted from
     * @param x x-index of the missing data point (second/inner array)
     * @param y y-index of the missing data point (first/outer array)
     * @return interpolated value for the missing data point
     */
    @Deprecated
    public float area(short[][] arr, int x, int y) {

        /*
         * Goal: get a 4x4 grid to apply bicubic interpolation
         * - get the first existing sample before and after (x1, x2, y1, y2)
         * - find the corners of the area that is defined by these four values
         *   -> use the findNearestValidValue function
         * - calculate the other 8 surrounding values and use the
         *   findNearestValidValue function
         * - use this as input for bicubic interpolation
         *
         * -> samples unstable, creates too much noise
         */

        float[][] samples = new float[4][4];

        // indices of next valid samples
        int xLow = findNextValidIndex(arr, Direction.X_MINUS, x, y, limit);
        int xHigh = findNextValidIndex(arr, Direction.X_PLUS, x, y, limit);
        int yLow = findNextValidIndex(arr, Direction.Y_MINUS, x, y, limit);
        int yHigh = findNextValidIndex(arr, Direction.Y_PLUS, x, y, limit);
        // shortest distance between valid values
        int xWidth = xHigh - xLow;
        int yWidth = yHigh - yLow;
        int maxX = arr[0].length - 1;
        int maxY = arr.length - 1;

        // find sample values
        for (int iy = 0; iy < samples.length; iy++) {
            for (int ix = 0; ix < samples[0].length; ix++) {
                // calculate index positions
                int xPos = ix < 2 ?
                        ix == 1 ? xLow : xLow - xWidth :
                        ix == 2 ? xHigh : xHigh + xWidth;
                int yPos = iy < 2 ?
                        iy == 1 ? yLow : yLow - yWidth :
                        iy == 2 ? yHigh : yHigh + yWidth;
                // ensure bounds
                xPos = xPos < 0 ? 0 : xPos > maxX ? maxX : xPos;
                yPos = yPos < 0 ? 0 : yPos > maxY ? maxY : yPos;
                // retrieve value
                samples[iy][ix] = findNearestValidValue(arr, xPos, yPos, 32);
            }
        }

        // fractions
        float fracX = xWidth == 0 ? 0 : (x-xLow) / (float) xWidth;
        float fracY = yWidth == 0 ? 0 : (y-yLow) / (float) yWidth;

        // interpolate (bicubic)
        return igap.interpolateSample(samples, fracX, fracY);
    }

    /**
     * Star-Algorithm to derive the value of a missing data point from
     * surrounding data.
     * Finds the first available data points in each direction of each axis
     * and weighs the value of the missing point according to the distance
     * from the missing to the first existing point.
     *
     * @param arr the full array where missing data can be interpoalted from
     * @param x x-index of the missing data point (second/inner array)
     * @param y y-index of the missing data point (first/outer array)
     * @return interpolated value for the missing data point
     */
    public float star(short[][] arr, int x, int y) {

        /*
         * for each x and y:
         * - get the first existing sample before and after (x1, x2, y1, y2)
         * - calculate the relative position within these bounds (fracX, fracY)
         * - get the remaining samples 0 and 3, so that the distance to 1 and 2
         *   is as far as the distance between 1 and 2
         *   * get y1, y2 for x0, x3
         *   * get x1, x2 for y0, y3
         *   * get x0y0, x3y0, x0y3 and x3y3 based on the found values
         *
         * handle special cases:
         * - x/y1/2 out of bounds:
         */

        float[] samplesX = new float[4];
        float[] samplesY = new float[4];

        // indices of next valid samples
        int x1 = findNextValidIndex(arr, Direction.X_MINUS, x, y, limit);
        int x2 = findNextValidIndex(arr, Direction.X_PLUS, x, y, limit);
        int y1 = findNextValidIndex(arr, Direction.Y_MINUS, x, y, limit);
        int y2 = findNextValidIndex(arr, Direction.Y_PLUS, x, y, limit);

        // decide whether valid indices were found for x and y
        boolean useX = x1 >= 0;
        boolean useY = y1 >= 0;

        float interpX = 0;
        float interpY = 0;

        // calculate intepolated x value
        if(useX) {
            // shortest distance between valid values
            int xWidth = x2 - x1;

            // outer samples
            int maxX = arr[0].length - 1;
            int x0 = x1 - xWidth < 0 ? 0 : x1 - xWidth;
            int x3 = x2 + xWidth > maxX ? maxX : x2 + xWidth;

            // fill samples (get inner and calculate outer values)
            samplesX[0] = findNearestValidValue(arr, x0, y, limit);
            samplesX[1] = arr[y][x1];
            samplesX[2] = arr[y][x2];
            samplesX[3] = findNearestValidValue(arr, x3, y, limit);

            // interpolate
            float fracX = xWidth == 0 ? 0 : (x-x1) / (float) xWidth;
            interpX = igap.interpolateSampleX(samplesX, fracX);
        }

        // calculate interpolated y value
        if(useY) {
            // shortest distance between valid values
            int yWidth = y2 - y1;

            // outer samples
            int maxY = arr.length - 1;
            int y0 = y1 - yWidth < 0 ? 0 : y1 - yWidth;
            int y3 = y2 + yWidth > maxY ? maxY : y2 + yWidth;

            // fill samples (get inner and calculate outer values)
            samplesY[0] = findNearestValidValue(arr, x, y0, limit);
            samplesY[1] = arr[y1][x];
            samplesY[2] = arr[y2][x];
            samplesY[3] = findNearestValidValue(arr, x, y3, limit);

            // interpolate
            float fracY = yWidth == 0 ? 0 : (y-y1) / (float) yWidth;
            interpY = igap.interpolateSampleY(samplesY, fracY);
        }

        // return interpolated value
        if(useX && useY) {
            // return average of x and y interpolation
            return (interpX + interpY) / 2f;
        } else if(useX) {
            return interpX;
        } else if(useY) {
            return interpY;
        } else {
            return 0;
        }
    }

    /**
     * Finds the next index in the specified direction that holds a valid
     * value (which must not be a {@link GapInterpolator#gap})
     *
     * @param arr the array to search for valid values in
     * @param dir the direction to search in
     * @param x x-index of the missing data point (second/inner array)
     * @param y y-index of the missing data point (first/outer array)
     * @param limit distance after which the search is aborted
     * (returns {@link GapInterpolator#notfound})
     * @return the next valid index in the specified direction, or the opposite
     * direction, if nothing is found in that direction
     */
    protected int findNextValidIndex(short[][] arr, Direction dir, int x, int y, int limit) {
        int idx = dir.nextIndex(arr, x, y, limit);
        if(idx == notfound) {
            // find in opposite direction
            idx = dir.getOpposite().nextIndex(arr, x, y, limit);
        }
        // if nothing is found, -1 is returned (value of "notfound")
        return idx;
    }

    /**
     * Finds the nearest valid value starting at a illegal data point in a
     * 2d array. The functions does only search on each axis, not in between
     * (star-search), so the actual nearest value might be missed, but at
     * least it's fast that way.
     *
     * @param arr the array to search for valid values in
     * @param x x-index of the missing data point (second/inner array)
     * @param y y-index of the missing data point (first/outer array)
     * @return the next valid value using star-search
     */
    protected short findNearestValidValue(short[][] arr, int x, int y) {
        return findNearestValidValue(arr, x, y, Integer.MAX_VALUE);
    }

    /**
     * Finds the nearest valid value starting at a illegal data point in a
     * 2d array. The functions does only search on each axis, not in between
     * (star-search), so the actual nearest value might be missed, but at
     * least it's fast that way.
     *
     * @param arr the array to search for valid values in
     * @param x x-index of the missing data point (second/inner array)
     * @param y y-index of the missing data point (first/outer array)
     * @param limit distance after which the search is aborted (returns 0)
     * @return the next valid value using star-search
     */
    protected short findNearestValidValue(short[][] arr, int x, int y, int limit) {
        // shortcut
        if(arr[y][x] != gap) {
            return arr[y][x];
        }

        // star-search ;)
        int lenY = arr.length;
        int lenX = arr[0].length;
        int pos, dist = 1;
        while(dist <= limit && (x-dist >= 0 || x+dist < lenX || y-dist >= 0 || y+dist < lenY)) {
            pos = x-dist;
            if(pos >= 0 && arr[y][pos] != gap) {
                return arr[y][pos];
            }
            pos = x+dist;
            if(pos < lenX && arr[y][pos] != gap) {
                return arr[y][pos];
            }
            pos = y-dist;
            if(pos >= 0 && arr[pos][x] != gap) {
                return arr[pos][x];
            }
            pos = y+dist;
            if(pos < lenY && arr[pos][x] != gap) {
                return arr[pos][x];
            }
            dist++;
        }
        return 0;   // if this happens, map is pretty empty...
    }


    /**
     * Defines the four directions available in a 2D-Array.
     *
     * Provides a function for each direction to search for the next index in
     * the specified direction whose value does not indicate a gap.
     *
     * Naming conventions follow row-major order
     * <https://en.wikipedia.org/wiki/Row-major_order>
     */
    protected enum Direction {
        Y_MINUS {
            @Override
            public int nextIndex(short[][] arr, int x, int y, int limit) {
                int yIdx = y;
                int count = 0;
                while(count < limit && yIdx > 0) {
                    if(arr[--yIdx][x] != gap) {
                        return yIdx;
                    }
                    count++;
                }
                return notfound;
            }
            @Override
            public Direction getOpposite() {
                return Y_PLUS;
            }
        },
        Y_PLUS {
            @Override
            public int nextIndex(short[][] arr, int x, int y, int limit) {
                int yIdx = y;
                int maxIdx = arr.length-2;  // -2 for decrement AFTER comparison
                int count = 0;
                while(count < limit && yIdx <= maxIdx) {
                    if(arr[++yIdx][x] != gap) {
                        return yIdx;
                    }
                    count++;
                }
                return notfound;
            }
            @Override
            public Direction getOpposite() {
                return Y_MINUS;
            }
        },
        X_MINUS {
            @Override
            public int nextIndex(short[][] arr, int x, int y, int limit) {
                int xIdx = x;
                int count = 0;
                while(count < limit && xIdx > 0) {
                    if(arr[y][--xIdx] != gap) {
                        return xIdx;
                    }
                    count++;
                }
                return notfound;
            }
            @Override
            public Direction getOpposite() {
                return X_PLUS;
            }
        },
        X_PLUS {
            @Override
            public int nextIndex(short[][] arr, int x, int y, int limit) {
                int xIdx = x;
                int maxIdx = arr[0].length-2;  // -2 for decrement AFTER comparison
                int count = 0;
                while(count < limit && xIdx <= maxIdx) {
                    if(arr[y][++xIdx] != gap) {
                        return xIdx;
                    }
                    count++;
                }
                return notfound;
            }
            @Override
            public Direction getOpposite() {
                return X_MINUS;
            }
        };

        /**
         * Finds the next index in the specified direction that holds a valid
         * value (which must not be a {@link GapInterpolator#gap})
         *
         * @param arr the array to search for valid values in
         * @param x x-index of the missing data point (second/inner array)
         * @param y y-index of the missing data point (first/outer array)
         * @return the next valid index in the specified direction
         */
        public int nextIndex(short[][] arr, int x, int y) {
            return nextIndex(arr, x, y, Integer.MAX_VALUE);
        };

        /**
         * Finds the next index in the specified direction that holds a valid
         * value (which must not be a {@link GapInterpolator#gap})
         *
         * @param arr the array to search for valid values in
         * @param x x-index of the missing data point (second/inner array)
         * @param y y-index of the missing data point (first/outer array)
         * @param limit distance after which the search is aborted
         * (returns {@link GapInterpolator#notfound})
         * @return the next valid index in the specified direction
         */
        public abstract int nextIndex(short[][] arr, int x, int y, int limit);

        /**
         * @return the opposite of the current direction
         */
        public abstract Direction getOpposite();
    }


}
