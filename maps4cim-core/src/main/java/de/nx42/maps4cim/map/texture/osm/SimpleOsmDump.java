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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

import de.nx42.maps4cim.map.ex.OsmXmlFormatException;

/**
 * Stores the contents of a OSM XML file in a well accessible object structure.
 * No magic happens here, but all contents are available for further processing
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class SimpleOsmDump implements Sink {

	/** All nodes in this OSM XML */
    protected final List<Node> nodes = new LinkedList<Node>();
    /** All ways in this OSM XML */
    protected final List<Way> ways = new LinkedList<Way>();
    /** All relations in this OSM XML */
    protected final List<Relation> relations = new LinkedList<Relation>();

    /** Mapping: Node ID -> Node object. Useful to resolve Way -> Node dependencies */
    protected Map<Long,Node> nodeById  = new HashMap<Long, Node>();

    // getters

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Way> getWays() {
        return ways;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public Node getNodeById(long id) {
        return nodeById.get(id);
    }

    public Node getNodeById(WayNode wayNode) {
        return nodeById.get(wayNode.getNodeId());
    }

    public List<Node> getNodesById(Way way) throws OsmXmlFormatException {
        List<Node> wayNodes = new ArrayList<Node>(way.getWayNodes().size());
        for (WayNode wn : way.getWayNodes()) {
            Node n = getNodeById(wn);
            if(n == null) {
                throw new OsmXmlFormatException(String.format(
                        "Error while parsing OSM XML: Node %s in Way %s " +
                        "(length: %s) is not declared in the document!",
                        wn.getNodeId(), way.getId(), way.getWayNodes().size()));
            }
            wayNodes.add(n);
        }
        return wayNodes;
    }

    // Sink implementation

    @Override
    public void process(EntityContainer entityContainer) {
        Entity entity = entityContainer.getEntity();
        if (entity instanceof Node) {
            Node node = (Node) entity;
            this.nodes.add(node);
            this.nodeById.put(node.getId(), node);
        } else if (entity instanceof Way) {
            Way way = (Way) entity;
            ways.add(way);
        } else if (entity instanceof Relation) {
            Relation rel = (Relation) entity;
            relations.add(rel);
        }
    }

    protected void processRelations() {
        // TODO add relation support or find a lib that handles this
        for (Relation rel : relations) {
            List<RelationMember> members = rel.getMembers();
            members.get(0).getMemberId();
        }
    }

    @Override
    public void initialize(Map<String, Object> metaData) { /* unused */ }
    @Override
    public void complete() { /* unused */ }
    @Override
    public void release() { /* unused */ }

    // Instantiation

    public static SimpleOsmDump readOsmXml(File osmxml) {

        // Defines the interface for tasks consuming OSM data types.
        SimpleOsmDump sink = new SimpleOsmDump();

        // compression (if any)
        CompressionMethod compression = CompressionMethod.None;
        if (osmxml.getName().endsWith(".gz")) {
            compression = CompressionMethod.GZip;
        } else if (osmxml.getName().endsWith(".bz2")) {
            compression = CompressionMethod.BZip2;
        }

        // read source file (into sink)
        XmlReader reader = new XmlReader(osmxml, false, compression);
        reader.setSink(sink);
        reader.run();   // just run, no threading

        return sink;
    }

    // print helpers

    public static void print(Node node) {
        System.out.format("%s: %10s (%-10s, %-10s), version %2s by %s\n",
                "Node", node.getId(),
                node.getLatitude(), node.getLongitude(),
                String.valueOf(node.getVersion()), node.getUser().getName());
        printTags(node.getTags());
    }

    public static void print(Way way) {
        System.out.format("%s: %10s, version %2s by %s with %s waypoints\n",
                "Way", way.getId(), way.getVersion(), way.getUser().getName(),
                way.getWayNodes().size());
        printTags(way.getTags());
    }

    public static void printTags(Collection<Tag> tags) {
        if(tags.size() > 0) {
            System.out.format("\tTags: %s\n", formatTags(tags));
        }
    }

    public static String formatTags(Collection<Tag> tags) {
        StringBuilder sb = new StringBuilder(tags.size() * 20);
        for (Tag tag : tags) {
            sb.append(", ");
            sb.append(tag.getKey());
            sb.append('=');
            sb.append(tag.getValue());
        }
        if(sb.length() > 2) {
            sb.delete(0, 2);
        }
        return sb.toString();
    }

}