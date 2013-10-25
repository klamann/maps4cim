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
 * Implementation of the bicubic interpolation algorithm.
 * 
 * @see Interpolation
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Bicubic extends Interpolation {

    public Bicubic(float[][] input) {
        super(input);
    }

    public Bicubic(float[][] input, int lenX, int lenY) {
        super(input, lenX, lenY);
    }

    /**
     * src: http://www.paulinternet.nl/?page=bicubic
     * interpolate between the values at index 1 and 2 in a float array of length 4
     * @param p the samples to use for interpolation. Four samples are expected.
     * @param x the subsample position, in the range [0.0F, 1.0F).
     * @return the interpolated value
     */
    @Override
    public float interpolateSampleX(float[] p, float x) {
        return (float) (p[1] + 0.5 * x * (p[2] - p[0] + x * (2.0 * p[0] -
                5.0 * p[1] + 4.0 * p[2] - p[3] + x * (3.0 * (p[1] - p[2]) +
                p[3] - p[0]))));
    }

}
