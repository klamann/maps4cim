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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.osm.EntityDef;
import de.nx42.maps4cim.config.texture.osm.NodeDef;
import de.nx42.maps4cim.config.texture.osm.PolygonDef;
import de.nx42.maps4cim.config.texture.osm.WayDef;
import de.nx42.maps4cim.map.ex.OsmXmlFormatException;
import de.nx42.maps4cim.map.texture.osm.primitives.Point;
import de.nx42.maps4cim.map.texture.osm.primitives.Polygon;
import de.nx42.maps4cim.map.texture.osm.primitives.Polyline;
import de.nx42.maps4cim.map.texture.osm.primitives.RenderPrimitive;
import de.nx42.maps4cim.util.gis.Coordinate;

/**
 * Filter and sort OSM Entities, convert them to Render Instructions using
 * the definitions in the program configuration.
 */
public class EntityConverter {

    private static Logger log = LoggerFactory.getLogger(EntityConverter.class);

    // assigned
    protected OsmDef osm;
    protected List<EntityDef> defs;
    protected Collection<ColorDef> colors;
    protected SimpleOsmDump sink;

    // derived
    List<OverpassTagMatcher> filters = new LinkedList<OverpassTagMatcher>();
    protected Map<OverpassTagMatcher, EntityDef> filterMap = new HashMap<OverpassTagMatcher, EntityDef>();
    protected Multimap<EntityDef,Entity> defToEntities;


    public EntityConverter(OsmDef config, SimpleOsmDump sink) {
        this.osm = config;
        this.defs = osm.entities;
        this.colors = osm.colors;
        this.sink = sink;
        createFilters();
    }

    protected void createFilters() {
        for (EntityDef def : defs) {
        	OverpassTagMatcher matcher = new OverpassTagMatcher(def);
            filters.add(matcher);
            filterMap.put(matcher, def);
        }
    }


    /*
     * 1. Filtern
     * 2. Gefilterte Objekte befüllen (Wege -> Nodes)
     * 3. Sortierte Sammlungen zurückgeben
     */

    public List<RenderContainer> buildRenderContainers() throws OsmXmlFormatException {
        // match filters
        matchAll();

        // create render containers from definitions
        // convert to renderable objects, keep original order (of definitions)
        List<RenderContainer> rc = new LinkedList<RenderContainer>();
        for (EntityDef def : defs) {
            Collection<Entity> osmEntities = defToEntities.get(def);
            Collection<RenderPrimitive> primitives = getRenderPrimitives(def, osmEntities);
            if(primitives.size() > 0) {
                rc.add(new RenderContainer(primitives));
            }
        }
        return rc;
    }

    protected void matchAll() {
        List<Node> nodes = sink.getNodes();
        List<Way> ways = sink.getWays();

        this.defToEntities = HashMultimap.create(filters.size(), (nodes.size() + ways.size()) / filters.size());

        matchAll(nodes);
        matchAll(ways);
    }

    protected void matchAll(List<? extends Entity> entities) {
        for (Entity entity : entities) {
            for (OverpassTagMatcher filter : filters) {
                if(filter.matches(entity)) {
                    defToEntities.put(filterMap.get(filter), entity);
                }
            }
        }
    }

    protected Collection<RenderPrimitive> getRenderPrimitives(EntityDef def,
            Collection<Entity> osmEntities) throws OsmXmlFormatException {

        List<RenderPrimitive> primitives = new LinkedList<RenderPrimitive>();
        for (Entity entitiy : osmEntities) {
            primitives.add(getRenderPrimitive(def, entitiy));
        }
        return primitives;
    }

    protected RenderPrimitive getRenderPrimitive(EntityDef def, Entity osmEntity) throws OsmXmlFormatException {
        try {
            // all type conversion errors in this method are unexpected
            if (def instanceof WayDef) {
                if (osmEntity instanceof Way) {
                    List<Node> wayNodes = sink.getNodesById((Way) osmEntity);
                    return new Polyline(Coordinate.convert(wayNodes), colors, (WayDef) def);
                } else {
                    throw new OsmXmlFormatException("Unexpected OSM Entity Type.");
                }
            } else if (def instanceof PolygonDef) {
                if (osmEntity instanceof Way) {
                    List<Node> wayNodes = sink.getNodesById((Way) osmEntity);
                    return new Polygon(Coordinate.convert(wayNodes), colors,
                            (PolygonDef) def);
                } else {
                    throw new OsmXmlFormatException("Unexpected OSM Entity Type.");
                }
            } else if (def instanceof NodeDef) {
                if (osmEntity instanceof Node) {
                    return new Point((NodeDef) def, (Node) osmEntity, colors);
                } else {
                    throw new OsmXmlFormatException("Unexpected OSM Entity Type.");
                }
            } else {
                throw new OsmXmlFormatException("Unsupported Entity-Type");
            }
        } catch(NullPointerException e) {
            throw new OsmXmlFormatException("Error while processing OSM XML. " +
            		"There is probably a dangling entity pointer, which " +
            		"means that the data is seriously messed up. The entity " +
            		"that caused this exception is " + osmEntity.toString(), e);
        } catch(RuntimeException e) {
            throw new OsmXmlFormatException("Unexpected exception while " +
            		"processing OSM XML.", e);
        }
    }

}
