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
 * Implementation of the bilinear interpolation algorithm.
 * 
 * @see Interpolation
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Bilinear extends Interpolation {

    public Bilinear(float[][] input) {
        super(input);
    }

    public Bilinear(float[][] input, int lenX, int lenY) {
        super(input, lenX, lenY);
    }

    /**
     * Performs horizontal interpolation on a 1-dimensional array of
     * floating-point samples representing a row of samples.
     * 
     * Implementation of a linear interpolation algorithm on an array of four
     * samples (creates bilinear results for 2D-arrays). Returns a linear mixture
     * of samples 1 and 2 using the fracX value as weight between 1 and 2.
     * All other samples are ignored.
     */
    @Override
    public float interpolateSampleX(float[] samples, float fracX) {
        return samples[1] + (samples[2] - samples[1]) * fracX;
    }

}
