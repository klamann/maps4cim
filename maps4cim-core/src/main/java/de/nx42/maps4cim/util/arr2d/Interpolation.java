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

import java.lang.reflect.Constructor;

/**
 * Abstract base class that provides functions for the scaling of arbitrary
 * data sets using different interpolation algorithms.
 * Special focus was laid on the interpolation of 2D-arrays.
 * 
 * If not mentioned otherwise, arrays are addressed in row-major order
 * <https://en.wikipedia.org/wiki/Row-major_order>
 * 
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public abstract class Interpolation {

    // convention: float[y][x], top to bottom, left to right
    // x = width, horizontal length; y = height, vertical length
    protected float[][] input;

    // samples to use for interpolation
    protected int samplesX = 4;
    protected int samplesY = 4;
    // length of the input array
    protected int inputLenY;
    protected int inputLenX;
    // max indices of the input array
    protected int inputMaxIdxY;
    protected int inputMaxIdxX;
    // highes indices for which a sample can be calculated (usually maxIdx - 1)
    protected int inputMaxSampleY;
    protected int inputMaxSampleX;


    /**
     * Creates a new Interpolation object that works on the specified
     * 2D float array. Width and length of the array are retrieved from samples.
     * It is best practice to specify with and length manually, see
     * {@link Interpolation#Interpolation(float[][], int, int)}
     * @param input the array to interpolate
     */
    public Interpolation(float[][] input) {
        this(input, input[0].length, input.length);
    }

    /**
     * Creates a new Interpolation object that works on the specified
     * 2D float array, using the specified values for length and width
     * of the matrix.
     * @param input the array to interpolate
     * @param lenX the length in x-direction (width, horizontal length, second index)
     * @param lenY the length in y-direction (height, vertical length, first index)
     */
    public Interpolation(float[][] input, int lenX, int lenY) {
        this(input, lenX, lenY, 4);
    }

    /**
     * Creates a new Interpolation object that works on the specified
     * 2D float array, using the specified values for length and width
     * of the matrix.
     * Also specifies the number of samples that are to be used in the
     * implementation of the interpolation algorithm.
     * @param input the array to interpolate
     * @param lenX the length in x-direction (width, horizontal length, second index)
     * @param lenY the length in y-direction (height, vertical length, first index)
     * @param samples the number of samples in both x and y direction to use
     * (actual usage of these values depends on implementations)
     */
    public Interpolation(float[][] input, int lenX, int lenY, int samples) {
        this.input = input;
        this.inputLenX = lenX;
        this.inputLenY = lenY;
        this.samplesX = this.samplesY = samples;

        // derive other values
        inputMaxIdxX = inputLenX - 1;
        inputMaxIdxY = inputLenY - 1;
        inputMaxSampleX = inputMaxIdxX - 1;
        inputMaxSampleY = inputMaxIdxY - 1;

    }

    /**
     * Constructs an Interpolation object with no fields set.
     * This constructor is only invoked by subclasses which
     * will subsequently set all fields themselves. Use with caution.
     */
    protected Interpolation() {}


    // Instance shortcuts
    

    /**
     * Known interpolation types.
     * Functions are provided to instantiate known implementations.
     */
    public enum Type {
        /** nearest neighbor interpolation. simple, fast, ugly */
        NEAREST(NearestNeighbor.class),
        /** bilinear interpolation. fast with reasonable results */
        BILINEAR(Bilinear.class),
        /** bicubic interpolation. slower, but creates the best results */
        BICUBIC(Bicubic.class);

        protected Class<? extends Interpolation> clazz;

        Type(Class<? extends Interpolation> clazz) {
            this.clazz = clazz;
        }

        /**
         * Creates a new instance of an implementation of the selected
         * interpolation algorithm.
         * @param input the array to work on
         * @return an implementation of this interpolation type with the given
         * input array
         */
        public Interpolation getInstance(float[][] input) {
            try {
                Constructor<? extends Interpolation> ctor = clazz.getConstructor(float[][].class);
                return ctor.newInstance(new Object[]{input});
            } catch (Exception e) {
                throw new RuntimeException("Unexpected exception while "
                        + "instantiating class " + clazz.getName(), e);
            }
        }
    }

    /**
     * Creates a new instance of an implementation of the selected
     * interpolation algorithm.
     * @param type the interpolation algorithm to use
     * @param input the array to work on
     * @return an implementation of this interpolation type with the given
     * input array
     */
    public static Interpolation getInstance(Type type, float[][] input) {
        return type.getInstance(input);
    }

    
    // calculate single samples

    
    /**
     * Performs horizontal interpolation on a 1-dimensional array of
     * floating-point samples representing a row of samples.
     * The required and actually used amount of samples depends on the
     * implementation.
     *
     * @param samples an array of floats.
     * @param fracX the X subsample position, in the range [0.0F, 1.0F).
     * @return the interpolated value as a float.
     */
    public abstract float interpolateSampleX(float[] samples, float fracX);

    /**
     * Performs horizontal interpolation on a 1-dimensional array of
     * floating-point samples of any size. Only the required samples
     * around the specified offset are considered.
     *
     * Nonexisting samples at the borders of the array are interpolated
     * as well, making it possible to retrieve any value from the input
     * array, including the outer bounds 0 and arr.length-1 (though the
     * last value can only be retrieved, if fracX==0.0, or it will point
     * outside of the array's bounds)
     *
     * @param arr an array of floats.
     * @param fracX the X subsample position, in the range [0.0F, 1.0F).
     * @param offsetX the offset of the value to retrieve.
     *        E.g. offset=2 means that the value between index position
     *        2 and 3 is calculated for the specified subsample position.
     * @return the interpolated value as a float.
     */
    public float interpolateX(float[] arr, float fracX, int offsetX) {
        // shortcut: fast return exact matching values
        // allows last value (arr.length-1) to be used
        if(fracX == 0)
            return arr[offsetX];

        // regular case: get the required values with the specified
        // offset from the input array
        float[] interpX = new float[samplesX];
        for (int i = 0; i < samplesX; i++) {
            interpX[i] = arr[adjustOffset(offsetX, arr.length, i)];
        }
        return interpolateSampleX(interpX, fracX);
    }

    /**
     * Performs vertical (Y-achsis) interpolation on a 1-dimensional array of
     * floating-point samples representing a column of samples.
     *
     * By default, vertical interpolation is identical to
     * horizontal interpolation.  Subclasses may choose to implement
     * them differently.
     *
     * @param samples an array of floats.
     * @param fracY the Y subsample position, in the range [0.0F, 1.0F).
     * @return the interpolated value as a float.
     */
    public float interpolateSampleY(float[] samples, float fracY) {
        return interpolateSampleX(samples, fracY);
    }

    /**
     * Performs vertical (Y-achsis) interpolation on a 1-dimensional array of
     * floating-point samples representing a column of samples.
     *
     * Nonexisting samples at the borders of the array are interpolated
     * as well, making it possible to retrieve any value from the input
     * array, including the outer bounds 0 and arr.length-1 (though the
     * last value can only be retrieved, if fracX==0.0, or it will point
     * out of the array)
     *
     * By default, vertical interpolation is identical to
     * horizontal interpolation.  Subclasses may choose to implement
     * them differently.
     *
     * @param arr an array of floats.
     * @param fracY the Y subsample position, in the range [0.0F, 1.0F).
     * @param offsetY the offset of the value to retrieve.
     *        E.g. offset=2 means that the value between index position
     *        2 and 3 is calculated for the specified subsample position.
     * @return the interpolated value as a float.
     */
    public float interpolateY(float[] arr, float fracY, int offsetY) {
        return interpolateX(arr, fracY, offsetY);
    }

    /**
     * Performs interpolation on a 2-dimensional array of
     * floating-point samples. By default, this is implemented using
     * a two-pass approach.
     *
     * @param samples an array of floats.
     * @param fracX the X subsample position, in the range [0.0F, 1.0F).
     * @param fracY the Y subsample position, in the range [0.0F, 1.0F).
     * @return the interpolated value as a float.
     */
    public float interpolateSample(float[][] samples, float fracX, float fracY) {
        float[] interpY = new float[samplesY];
        for (int i = 0; i < samplesY; i++) {
            float val = interpolateSampleX(samples[i], fracX);
            interpY[i] = val;
        }
        return interpolateSampleY(interpY, fracY);
    }

    /**
     * Performs interpolation on an arbitraray 2-dimensional array of
     * floating-point samples. By default, this is implemented using
     * a two-pass approach.
     *
     * Arrays of any size >= 2 in x- and y-direction can be used with this
     * function, as long as the specified offset is within the range of the
     * input array.
     *
     * @param arr an array of floats.
     * @param fracX the X subsample position, in the range [0.0F, 1.0F).
     * @param fracY the Y subsample position, in the range [0.0F, 1.0F).
     * @param offsetX the offset of the x-value to retrieve (equals the index
     *                position on the x-achsis)
     * @param offsetY the offset of the y-value to retrieve (equals the index
     *                position on the y-achsis)
     * @return the interpolated value as a float.
     */
    public float interpolate(float[][] arr, float fracX, float fracY, int offsetX, int offsetY) {
        // shortcut: fast return exact matching values
        // allows last value (arr.length-1) to be used
        if(fracY == 0)
            return interpolateX(arr[offsetY], fracX, offsetX);

        // regular case: get the required values with the specified
        // offset from the input array
        float[] interpY = new float[samplesY];
        for (int i = 0; i < samplesY; i++) {
            int line = adjustOffset(offsetY, arr[i].length, i);
            interpY[i] = interpolateX(arr[line], fracX, offsetX);
        }
        return interpolateSampleY(interpY, fracY);
    }

    /**
     * Performs interpolation on an arbitraray 2-dimensional array of
     * floating-point samples. By default, this is implemented using
     * a two-pass approach.
     *
     * Arrays of any size >= 2 in x- and y-direction can be used with this
     * function, as long as the specified offset is within the range of the
     * input array.
     *
     * @param arr an array of floats.
     * @param inputX the index position, including the subsample value, on the
     *               x-achsis of the input array
     * @param inputY the index position, including the subsample value, on the
     *               y-achsis of the input array
     * @return the interpolated value as a float.
     */
    public float interpolate(float[][] arr, float inputX, float inputY) {
        return interpolate(arr, getSigFig(inputX), getSigFig(inputY),
                (int) inputX, (int) inputY);
    }

    
    // libraray functions

    
    /**
     * Retrieves four samples from an input array that are as near as
     * possible to the specified offset.
     * This method is required to get sample values, even when the
     * offset is near 0 or array.length.
     *
     * @param offset the offset to get sample values for
     * @param arrLen the length of the input array
     * @param i the current sample position (allowed values: 0 to 3)
     * @return the offset value for the specified offset and index position
     */
    protected static int adjustOffset(int offset, int arrLen, int i) {
        if(i==0 & offset==0) {
            return 0;
        } else if(i==3 && offset>=arrLen-2) {
            return offset+1;
        } else {
            return offset - 1 + i;
        }
    }

    /**
     * Retrieves the significant figures for a float, e.g. 3.75 -> 0.75
     * @param value the float to get the sig figs from
     * @return the sig figs for the specified float
     */
    public static float getSigFig(float value) {
        return (float) (value - Math.floor(value));
    }

    
    // full array interpolation
    

    /**
     * Resizes the current array to the given dimensions.
     * Note that a new array is created and the result is not stored in place.
     * Therefore, any following operation will always use the original input array
     * @param lenX the desired length in x-direction (with, horizontal length)
     * @param lenY the desired length in y-direction (height, vertical length)
     * @return a new 2d float array with the specified dimensions, and values
     * interpolated from the input array.
     */
    public float[][] resize(int lenX, int lenY) {
        float[][] output = new float[lenY][lenX];
        float outputMaxIdxX = lenX - 1;
        float outputMaxIdxY = lenY - 1;

        for (int y = 0; y < lenY; y++) {
            for (int x = 0; x < lenX; x++) {
                float xSrc = (x / outputMaxIdxX) * inputMaxIdxX;
                float ySrc = (y / outputMaxIdxY) * inputMaxIdxY;

                output[y][x] = interpolate(input, xSrc, ySrc);
            }
        }
        return output;
    }

    /**
     * Copies a subset of the existing array into a new array, using the specified
     * indices as borders. decimals are rounded to the next lower or higher
     * integer, so that all specified values are included - or slightly more.
     * @param top the top index (minY, lower y index)
     * @param right the right index (maxX, higher x index)
     * @param bottom the bottom index (maxY, higher y index)
     * @param left the left index (minX, lower x index)
     * @return a new cropped version of the input array
     */
    public float[][] crop(float top, float right, float bottom, float left) {
        return Arrays2D.copyOfRange(input, (int) top, (int) Math.ceil(right),
                (int) Math.ceil(bottom), (int) left);
    }

    /**
     * Combination of {@link Interpolation#crop(float, float, float, float)} and
     * {@link Interpolation#resize(int, int)}. Crops and resizes the input array
     * into a new output array in a single operation.
     * @param lenX the desired length in x-direction (with, horizontal length)
     * @param lenY the desired length in y-direction (height, vertical length)
     * @param minX the lower index in x-direction
     * @param minY the lower index in y-direction
     * @param maxX the higher index in x-direction
     * @param maxY the higher index in y-direction
     * @return a new cropped and resized version of the input array
     */
    public float[][] cropAndResize(int lenX, int lenY,
            float minX, float minY, float maxX, float maxY) {

        float[][] output = new float[lenY][lenX];

        float outputMaxIdxX = lenX - 1;
        float outputMaxIdxY = lenY - 1;
        float cropLenX = maxX - minX;
        float cropLenY = maxY - minY;

        for (int y = 0; y < lenY; y++) {
            for (int x = 0; x < lenX; x++) {
                float xSrc = (x / outputMaxIdxX) * cropLenX + minX;
                float ySrc = (y / outputMaxIdxY) * cropLenY + minY;

                output[y][x] = interpolate(input, xSrc, ySrc);
            }
        }
        return output;
    }

}
