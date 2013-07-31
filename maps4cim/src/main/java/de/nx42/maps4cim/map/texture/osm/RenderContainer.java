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
package de.nx42.maps4cim.map.texture.osm;

import java.util.Collection;

import de.nx42.maps4cim.map.texture.osm.primitives.Point;
import de.nx42.maps4cim.map.texture.osm.primitives.Polygon;
import de.nx42.maps4cim.map.texture.osm.primitives.Polyline;
import de.nx42.maps4cim.map.texture.osm.primitives.RenderPrimitive;

public class RenderContainer {

    protected Collection<? extends RenderPrimitive> primitives;
    protected ElementType type;

    public RenderContainer(Collection<RenderPrimitive> primitives) {
        this.primitives = primitives;
        this.type = ElementType.of(primitives.iterator().next());
    }


    /**
     * @return the primitives to render
     */
    public Collection<? extends RenderPrimitive> getPrimitives() {
        return primitives;
    }

    /**
     * @return the type of the primitives
     */
    public ElementType getType() {
        return type;
    }


    public enum ElementType {
        POINT,
        POLYGON,
        POLYLINE;

        public static ElementType of(RenderPrimitive rp) {
            if(rp instanceof Polyline) {
                return POLYLINE;
            } else if(rp instanceof Polygon) {
                return POLYGON;
            } else if(rp instanceof Point) {
                return POINT;
            } else {
                throw new RuntimeException("RenderPrimitive Implementation not recognized!");
            }
        }

    }

}
