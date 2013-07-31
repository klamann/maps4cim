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
 * Implementation of the nearest neighbor interpolation algorithm.
 * 
 * @see Interpolation
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class NearestNeighbor extends Interpolation {

    public NearestNeighbor(float[][] input) {
        super(input);
    }

    public NearestNeighbor(float[][] input, int lenX, int lenY) {
        super(input, lenX, lenY);
    }

    /**
     * Performs horizontal interpolation on a 1-dimensional array of
     * floating-point samples representing a row of samples.
     * 
     * Implementation of the nearest neighbor algorithm on an array of four
     * samples. Sample 0 and 3 are ignored and the fraction decides, if sample
     * 1 or 2 is returned.
     */
    @Override
    public float interpolateSampleX(float[] samples, float fracX) {
        return fracX <= 0.5f ? samples[1] : samples[2];
    }

}
