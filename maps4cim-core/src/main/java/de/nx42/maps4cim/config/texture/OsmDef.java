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
package de.nx42.maps4cim.config.texture;

import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.oval.constraint.AssertValid;
import de.nx42.maps4cim.config.texture.osm.EntityDef;
import de.nx42.maps4cim.config.texture.osm.NodeDef;
import de.nx42.maps4cim.config.texture.osm.PolygonDef;
import de.nx42.maps4cim.config.texture.osm.WayDef;

@XmlRootElement(name = "osm")
public class OsmDef extends TextureDef {

    /**
     * Color definition: All colors that are referenced by the entities that
     * are supposed to be rendered are defined here.
     */
    @XmlElementWrapper(name = "colors")
    @XmlElement(name = "color")
    @AssertValid
    public Collection<ColorDef> colors;

    /**
     * Entity definition: All objects that shall be rendered (roads, buildings,
     * farms, forests) are defined here.
     * Order is paramount: The entities will be rendered in document order,
     * so it's best to place important stuff last and huge background textures
     * (e.g. meadow) first, or they're gonna hide all your nice roads and
     * buildings and stuff...
     * Valid entities are: Nodes (single dot), Ways (connected polyline) and
     * polygons (filled areas)
     */
    @XmlElements({
        @XmlElement(name="node", type=NodeDef.class),
        @XmlElement(name="way", type=WayDef.class),
        @XmlElement(name="polygon", type=PolygonDef.class)
    })
    @XmlElementWrapper
    @AssertValid
    public List<EntityDef> entities;

}
