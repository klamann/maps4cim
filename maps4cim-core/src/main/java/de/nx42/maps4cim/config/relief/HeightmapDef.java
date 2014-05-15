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
package de.nx42.maps4cim.config.relief;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.Range;
import net.sf.oval.constraint.ValidateWithMethod;


@XmlRootElement(name = "heightmap")
@ValidateWithMethod(methodName="isValid", parameterType = HeightmapDef.class, message="Heightmap: min height must be less than or equal to max height")
public class HeightmapDef extends ReliefDef {

    /**
     * Path to the heightmap image to use
     */
    @XmlAttribute(name = "file")
    @NotNull(message="Heightmap file path must be defined")
    @NotBlank(message="Heightmap file path must not be empty")
    public String heightMapPath;

    /**
     * The height that the lowest (darkest) pixel of the heightmap image
     * maps to. This will be the lowest elevation of the map (in meters).
     * Note that CiM 2 maps can only use heights within [-1024, 1024] meters.
     */
    @XmlAttribute(name = "min-height")
    @NotNull(message="Heightmap: mininum height must be defined")
    @Range(min=-1024, max=1024, message="Heightmap: minimum height (meters) is not in the range -1024 through 1024")
    public Double heightMapMinimum;

    /**
     * The height that the highest (lightest) pixel of the heightmap image
     * maps to. This will be the highest elevation of the map (in meters).
     * Note that CiM 2 maps can only use heights within [-1024, 1024] meters.
     */
    @XmlAttribute(name = "max-height")
    @NotNull(message="Heightmap: maximum height must be defined")
    @Range(min=-1024, max=1024, message="Heightmap: maximum height (meters) is not in the range -1024 through 1024")
    public Double heightMapMaximum;
    
    
    public boolean isValid() {
        return heightMapMinimum != null && heightMapMaximum != null
                && heightMapMinimum <= heightMapMaximum;
    }
    
    public static boolean isValid(HeightmapDef h) {
        return h.isValid();
    }


    public static HeightmapDef of(String heightMapPath, Double heightMapMinimum,
            Double heightMapMaximum) {
        HeightmapDef h = new HeightmapDef();
        h.heightMapPath = heightMapPath;
        h.heightMapMinimum = heightMapMinimum;
        h.heightMapMaximum = heightMapMaximum;
        return h;
    }
    
}