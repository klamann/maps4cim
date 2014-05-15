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

import java.io.IOException;
import java.io.OutputStream;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.relief.PlanarReliefDef;
import de.nx42.maps4cim.map.ReliefMap;
import de.nx42.maps4cim.map.ex.MapGeneratorException;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class PlanarRelief extends ReliefMap {

    protected float height = 0.0f;

    public PlanarRelief() {
        // default constructor
    }

    public PlanarRelief(float height) {
        this.height = height;
    }

    public PlanarRelief(Config conf) {
        PlanarReliefDef def = (PlanarReliefDef) conf.getReliefTrans();
        this.height = (float) def.getHeight();
    }

    @Override
    public float[][] generateRelief() {
        float[][] plain = new float[edgeLength][edgeLength];
        for (int y = 0; y < edgeLength; y++) {
            float[] line = plain[y];
            for (int x = 0; x < edgeLength; x++) {
                line[x] = height;
            }
        }
        return plain;
    }

    public static void write(OutputStream out) throws MapGeneratorException, IOException {
        ReliefMap planar = new PlanarRelief();
        planar.writeTo(out);
    }


}
