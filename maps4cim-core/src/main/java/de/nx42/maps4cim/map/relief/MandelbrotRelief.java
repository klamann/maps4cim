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
package de.nx42.maps4cim.map.relief;

import de.nx42.maps4cim.map.ReliefMap;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class MandelbrotRelief extends ReliefMap{

    protected int iterations = 32;
    protected float zeroValue = -25.0f;
    protected float scale = 100.0f;


    public MandelbrotRelief() {

    }

    public MandelbrotRelief(int iterations, float zeroValue, float scale) {
        this.iterations = iterations;
        this.zeroValue = zeroValue;
        this.scale = scale;
    }



    @Override
    public float[][] generateRelief() {
        return mandelArray();
    }


    protected float[][] mandelArray() {
        float[][] mandel = new float[edgeLength][edgeLength];

        for (int y = 0; y < edgeLength; y++) {
            float[] line = mandel[y];
            for (int x = 0; x < edgeLength; x++) {
                // squeeze (x, y) to the proper interval
                double dx = 2.5 * x / edgeLength - 2.0;
                double dy = 1.25 - 2.5 * y / edgeLength;
                // compute value
                int value = mandel(dx, dy);

                // writeTo value. allow different value for entries not in the mandelbrot set
                if(value == 0) {
                    line[x] = zeroValue;
                } else {
                    // normalize to [0.0;1.0], then scale as far as wished
                    line[x] = (scale * value) / iterations;
                }
            }
        }

        return mandel;
    }

    protected int mandel(double px, double py) {
        int value = 0;
        double zx = 0.0, zy = 0.0, zx2 = 0.0, zy2 = 0.0;
        while (value < iterations && zx2 + zy2 < 4.0) {
            zy = 2.0 * zx * zy + py;
            zx = zx2 - zy2 + px;
            zx2 = zx * zx;
            zy2 = zy * zy;
            value++;
        }
        return iterations - value;
    }

}
