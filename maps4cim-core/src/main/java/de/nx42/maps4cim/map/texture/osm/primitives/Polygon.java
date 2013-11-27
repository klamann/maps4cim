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
package de.nx42.maps4cim.map.texture.osm.primitives;

import java.util.Collection;
import java.util.List;

import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.PolygonDef;
import de.nx42.maps4cim.util.gis.Coordinate;

public class Polygon extends RenderPrimitive {

    protected List<Coordinate> nodes;

    // basic constructors

    public Polygon(List<Coordinate> nodes) {
        super();
        this.nodes = nodes;
    }

    // Constructors with color

    public Polygon(List<Coordinate> nodes, int color) {
        super(color);
        this.nodes = nodes;
    }

    public Polygon(List<Coordinate> nodes, ColorDef color) {
        super(color);
        this.nodes = nodes;
    }

    public Polygon(List<Coordinate> nodes, Collection<ColorDef> colors, PolygonDef def) {
        super(colors, def);
        this.nodes = nodes;
    }

    // Getters

    /**
     * @return the nodes
     */
    public List<Coordinate> getNodes() {
        return nodes;
    }

}
