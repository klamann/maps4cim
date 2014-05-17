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
package de.nx42.maps4cim.config.texture.osm;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.oval.constraint.Range;

/**
 * A single circular node, determined by color and radius.
 * @see EntityDef
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlRootElement(name = "node")
public class NodeDef extends EntityDef {

	/** the radius of the point to draw. Defaults to 1.0 pixel (which is about
	    4m on a true to scale map) */
    @XmlAttribute(name = "radius")
    @Range(min=0.0, max=1000.0, message="the draw radius of a point must be 0 px or larger, but not unreasonably large (>1000 px)")
    public Double radius;       // default: 1.0

    /**
     * @return the drawing radius of the node
     */
    public Double getRadius() {
        return radius != null ? radius : 1.0;
    }

	@Override
	public String getType() {
		return "node";
	}

	
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((radius == null) ? 0 : radius.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        NodeDef other = (NodeDef) obj;
        if (radius == null) {
            if (other.radius != null)
                return false;
        } else if (!radius.equals(other.radius))
            return false;
        return true;
    }

}
