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

/**
 * The map is defined by a bounding box, with each two values for latitude
 * and longitude. For a valid bounding box, all values are mandatory.
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlRootElement(name = "bbox")
@XmlType(propOrder = { "minLat", "minLon", "maxLat", "maxLon" })
public class BBoxDef extends BoundsDef {

	/** the lower latitude value, or southern bound */
    @XmlAttribute(name = "minlat", required = true)
    public Double minLat;  // S

    /** the lower longitude value, or western bound */
    @XmlAttribute(name = "minlon", required = true)
    public Double minLon;  // W

    /** the higher latitude value, or northern bound */
    @XmlAttribute(name = "maxlat", required = true)
    public Double maxLat;  // N

    /** the higher longitude value, or eastern bound */
    @XmlAttribute(name = "maxlon", required = true)
    public Double maxLon;  // E

}
