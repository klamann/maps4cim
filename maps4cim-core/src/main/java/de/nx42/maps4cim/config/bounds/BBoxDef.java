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
package de.nx42.maps4cim.config.bounds;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.Range;
import net.sf.oval.constraint.ValidateWithMethod;

/**
 * The map is defined by a bounding box, with each two values for latitude
 * and longitude. For a valid bounding box, all values are mandatory.
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlRootElement(name = "bbox")
@XmlType(propOrder = { "maxLon", "maxLat", "minLon", "minLat" })  // reverse order
@ValidateWithMethod(methodName="isValid", parameterType = BBoxDef.class, message="minLat/minLon must be less than their respective counterparts maxLat/maxLon")
public class BBoxDef extends BoundsDef {

	/** the lower latitude value, or southern bound */
    @XmlAttribute(name = "minlat", required = true)
    @NotNull(message = "latitude (min) must be defined")
    @Range(min=-90, max=90, message="latitude (min) is not in the range -90 through 90")
    public Double minLat;  // S

    /** the lower longitude value, or western bound */
    @XmlAttribute(name = "minlon", required = true)
    @NotNull(message = "longitude (min) must be defined")
    @Range(min=-180, max=180, message="longitude (min) is not in the range -180 through 180")
    public Double minLon;  // W

    /** the higher latitude value, or northern bound */
    @XmlAttribute(name = "maxlat", required = true)
    @NotNull(message = "latitude (max) must be defined")
    @Range(min=-90, max=90, message="latitude (max) is not in the range -90 through 90")
    public Double maxLat;  // N

    /** the higher longitude value, or eastern bound */
    @XmlAttribute(name = "maxlon", required = true)
    @NotNull(message = "longitude (max) must be defined")
    @Range(min=-180, max=180, message="longitude (max) is not in the range -180 through 180")
    public Double maxLon;  // E
    
    
    public boolean isValid() {
        return minLat != null && maxLat != null
                && minLon != null && maxLon != null
                && minLat < maxLat && minLon < maxLon;
    }
    
    protected static boolean isValid(BBoxDef bb) {
        return bb.isValid();
    }
    

    public static BBoxDef of(double minLat, double minLon, double maxLat, double maxLon) {
        BBoxDef bb = new BBoxDef();
        bb.minLat = minLat;
        bb.minLon = minLon;
        bb.maxLat = maxLat;
        bb.maxLon = maxLon;
        return bb;
    }

}
