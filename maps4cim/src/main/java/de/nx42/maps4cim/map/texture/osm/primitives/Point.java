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
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;

import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.NodeDef;
import de.nx42.maps4cim.util.gis.Coordinate;


public class Point extends RenderPrimitive {

    protected final Coordinate coord;

    protected double radius;

    // basic constructors

    public Point(Coordinate coord) {
        super();
        this.coord = coord;
        this.radius = 1.0;
    }

    public Point(NodeDef def, Node node) {
        super();
        this.coord = new Coordinate(node);
        this.radius = def.getRadius();
    }

    // Constructors with color

    public Point(NodeDef def, Node node, ColorDef color) {
        super(color);
        this.coord = new Coordinate(node);
        this.radius = def.getRadius();
    }

    public Point(NodeDef def, Node node, Collection<ColorDef> colors) {
        super(colors, def);
        this.coord = new Coordinate(node);
        this.radius = def.getRadius();
    }

    public Point(Coordinate coord, int color, double radius) {
        super(color);
        this.coord = coord;
        this.radius = radius;
    }

    public Point(Coordinate coord, int color) {
        this(coord, color, 1.0);
    }

    public Point(Coordinate coord, ColorDef color) {
        super(color);
        this.coord = coord;
        this.radius = 1.0;
    }

    public Point(Coordinate coord, Collection<ColorDef> colors, NodeDef def) {
        super(colors, def);
        this.coord = coord;
        this.radius = def.getRadius();
    }

    // getters

    /**
     * @return the coord
     */
    public Coordinate getCoord() {
        return coord;
    }

    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    // helpers

    public static List<Point> getPoints(NodeDef def, Collection<Node> xmlNodes) {
        List<Point> points = new LinkedList<Point>();
        for (Node node : xmlNodes) {
            points.add(new Point(def, node));
        }
        return points;
    }

}
