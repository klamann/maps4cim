/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 - 2014 Sebastian Straub
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

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class ImageJInterpolation {

    /**
     * Resizes the current array to the given dimensions.
     * @param input the array to resize
     * @param lenX the desired length in x-direction (with, horizontal length)
     * @param lenY the desired length in y-direction (height, vertical length)
     * @return a new 2d float array with the specified dimensions, and values
     * interpolated from the input array.
     */
    public static float[][] resize(float[][] input, int lenX, int lenY) {
        float[] linearized = linearize2DArray(input);

        ImageStack is = ImageStack.create(input.length, input[0].length, 1, 32);
        is.setPixels(linearized, 1);

        ImageProcessor ip = new ImagePlus("", is).getProcessor();
        ip.setInterpolationMethod(ImageProcessor.BICUBIC);
        float[] resized = (float[]) ip.resize(lenX, lenY).getPixels();

        return linearizedArrayTo2D(resized, lenX, lenY);
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
    public static float[][] crop(float[][] input, float top, float right, float bottom, float left) {
        return Arrays2D.copyOfRange(input, (int) top, (int) Math.ceil(right),
                (int) Math.ceil(bottom), (int) left);
    }

    /**
     * Combination of {@link ImageJInterpolation#crop(float[][], float, float, float, float)}
     * and {@link ImageJInterpolation#resize(float[][], int, int)}.
     * Crops and resizes the input array into a new output array.
     * @param input the array to work on
     * @param lenX the desired length in x-direction (with, horizontal length)
     * @param lenY the desired length in y-direction (height, vertical length)
     * @param minX the lower index in x-direction
     * @param minY the lower index in y-direction
     * @param maxX the higher index in x-direction
     * @param maxY the higher index in y-direction
     * @return a new cropped and resized version of the input array
     */
    public static float[][] cropAndResize(float[][] input, int lenX, int lenY,
            float minX, float minY, float maxX, float maxY) {

        float[] linearized = linearize2DArray(input);

        ImageStack is = ImageStack.create(input[0].length, input.length, 1, 32);
        is.setPixels(linearized, 1);

        ImageProcessor ip = new ImagePlus("", is).getProcessor();
        ip.setInterpolationMethod(ImageProcessor.BICUBIC);
        ip.setRoi((int) minX, (int) minY, (int) Math.ceil(maxX - minX), (int) Math.ceil(maxY - minY));

        float[] resized = (float[]) ip.crop().resize(lenX, lenY).getPixels();
        
        return linearizedArrayTo2D(resized, lenX, lenY);
    }


    public static float[] linearize2DArray(float[][] input) {
        float[] linear = new float[input.length * input[0].length];
        int lenX = input[0].length;
        int lenY = input.length;
        for (int y = 0; y < lenY; y++) {
            for (int x = 0; x < lenX; x++) {
                linear[y * lenX + x] = input[y][x];
            }
        }
        return linear;
    }

    public static float[][] linearizedArrayTo2D(float[] input, int lenX, int lenY) {
        float[][] img2d = new float[lenY][lenX];
        for (int i = 0; i < input.length; i++) {
            img2d[i / lenX][i % lenX] = input[i];
        }
        return img2d;
    }

}
