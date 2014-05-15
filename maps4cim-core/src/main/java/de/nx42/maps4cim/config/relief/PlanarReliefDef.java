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

import net.sf.oval.constraint.Range;


@XmlRootElement(name = "planar")
public class PlanarReliefDef extends ReliefDef {

    /**
     * the static height of the terrain for the entire map (in meters)
     */
    @XmlAttribute(name = "height")
    @Range(min=-1024, max=1024, message="Planar relief: height (meters) is not in the range -1024 through 1024")
    public Double height;

    /**
     * @return the defined height of the map or default value 0, if no height
     *         is defined
     */
    public double getHeight() {
        return height != null ? height : 0.0;
    }

}
