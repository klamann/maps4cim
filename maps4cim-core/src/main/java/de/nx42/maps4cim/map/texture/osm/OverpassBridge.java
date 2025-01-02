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
package de.nx42.maps4cim.map.texture.osm;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.osm.EntityDef;
import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.map.ex.TextureProcessingException;
import de.nx42.maps4cim.util.Network;
import de.nx42.maps4cim.util.gis.Area;

/**
 * This class serves as bridge to the Overpass API.
 * It parses the selectors stored in the config, transforms them into valid
 * OverpassQL, executes the queries, caches the result and passes it on.
 * 
 * <pre>
 * Overpass allows
 * - exact matches ["key"="value"]
 * - regex matches ["key"~"value"]
 *   -> beware: backslashes must be escaped before sending, e.g.
 *      ["name"~"^St\."] -> ["name"~"^St\\."]
 *      this is a OverpassQL special...
 *
 * Which operations are supported?
 * - Just support for exact match (key,value) and regex match (rvalue)
 *   -> no negation or stuff like that
 *
 * Formatting:
 * - null value: ["key"]
 * - value: ["key"="value"]
 * - rvalue: ["key"~"value"]
 *
 * Query-Building:
 * - base:   ( tags );(._;>;);out meta;
 *   -> this queries for all defined tags, follows their dependencies
 *      and prints the results with meta tags (required for osmosis)
 * - replace "tags" by following queries (simply append):
 *     node["key"="value"](50.6,7.0,50.8,7.3);
 *     way["key"="value"](50.6,7.0,50.8,7.3);
 *     rel["key"="value"](50.6,7.0,50.8,7.3);
 *
 * e.g.:
 * (way["highway"="tertiary"](50.6,7.0,50.8,7.3));(._;>;);out meta;
 * (way["highway"="primary"](50.6,7.0,50.8,7.3);way["highway"="secondary"](50.6,7.0,50.8,7.3););(._;>;);out meta;
 *
 * To get all data (reduces server load compared to very long queries):
 * (node(50.746,7.154,50.748,7.157);<;>;);out meta;
 * see also: http://wiki.openstreetmap.org/wiki/Overpass_API/Language_Guide#Completed_ways_and_relations
 * </pre>
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class OverpassBridge {

	private static final Logger log = LoggerFactory.getLogger(OverpassBridge.class);

	/** known public Overpass servers */
	protected static final String[] servers = new String[] {
	    "https://overpass-api.de/api/interpreter?data=",           // with gzip-support!
		"https://overpass.osm.ch/api/interpreter?data=",
		"https://overpass.private.coffee/api/interpreter?data=",
		"https://overpass.osm.jp/api/interpreter?data=",
	};

	protected static final String queryBegin = "(";
	protected static final String queryEnd = ");(._;>;);out meta;";
	protected static final ImmutableMap<Character,String> escapeChars =
			ImmutableMap.<Character,String>builder().put('\n', "\\n")
			                                        .put('\t', "\\t")
			                                        .put('\"', "\\\"")
			                                        .put('\'', "\\'")
			                                        .put('\\', "\\\\").build();

	protected Area bounds;
	protected List<EntityDef> entities;
	protected boolean caching = true;
	
	/**
	 * the maximum number of entities to query individually.
	 * all queries with more entities will cause a full download of the dataset
	 * within the selected {@link OverpassBridge#bounds} - this reduces server
	 * load and response time at the cost of a (minor) data overhead
	 */
	protected int entityQueryLimit = 15;   // medium preset

	public OverpassBridge(Config conf) {
		this.bounds = Area.of(conf.getBoundsTrans());
		this.entities = ((OsmDef) conf.getTextureTrans()).entities;
	}

	public OverpassBridge(Area bounds, OsmDef osm) {
        this.bounds = bounds;
        this.entities = osm.entities;
    }

	/**
	 * Retrieves data from the Overpass servers (or cache) and provides a
	 * link to the downloaded osm xml file.
	 * @return the resulting osm xml file
	 * @throws TextureProcessingException if anything goes wrong while
	 * downloading or retrieving data from cache
	 */
	public File getResult() throws TextureProcessingException {
	    OsmHash hash = new OsmHash(bounds, entities, exceedsQueryLimit());

		if(hash.isCached()) {
			log.debug("Retrieving Overpass query result from cache.");
			try {
				return hash.getCached();
			} catch (IOException e) {
				log.error("Error retrieving the OpenStreetMap-Result from cache.", e);
				throw new RuntimeException("Error reading Overpass-Result from cache", e);
			}
		} else {
			log.debug("Downloading OpenStreetMap data from the Overpass servers. This might take a few minutes...");
			return downloadAndCache(hash);
		}
	}


	// internal

	/**
	 * Downloads the requested data from the Overpass servers and stores
	 * the osm xml file on the disk cache, using the specified hash String
	 * for later retrieval
	 * @param hash the hash under which the file can be retrieved later
	 * @return the resulting osm xml file
	 * @throws TextureProcessingException if anything goes wrong while
     * downloading data from the Overpass servers
	 */
	protected File downloadAndCache(OsmHash hash) throws TextureProcessingException {
		Exception inner = null;
		for (String server : servers) {
			try {
			    final Stopwatch stopwatch = Stopwatch.createStarted();

				// generate Query and store result in temp
				URL query = buildQueryURL(server);
				File dest = Cache.temporaray(hash.getXmlFileName());
				
				// 5 seconds connection timeout, 90 seconds for the server to execute the query
		        // (so after this time, the download must start, or a timeout occurs)
		        Network.downloadToFile(query, dest, 10, 120);

				// zip result and store in cache
				if(caching) {
				    hash.storeInCache(dest);
				}

				stopwatch.stop();
				log.debug("Download from server {} finished in {}", query.getHost(), stopwatch.toString());
				// return plain text xml from temporary directory
				return dest;
			} catch(UnknownHostException e) {
			    inner = e;
			    log.error("The URL of Overpass-Server {} could not be resolved. Are you connected to the internet?", e.getMessage());
			} catch (SocketTimeoutException e) {
                inner = e;
                log.error("Error getting data from Overpass Server " + server + "\nTrying next ...", e);
			} catch (IOException e) {
                inner = e;
                log.error("I/O Exception while processing OpenStreetMap source data.", e);
            }
		}
		throw new TextureProcessingException("OpenStreetMap source data could " +
				"not be retrieved via Overpass API.", inner);
	}

	/**
	 * Generates the query URL for the specified server and the settings
	 * of this instance
	 * @param server the Overpass server to use
	 * @return the download URL for this query
	 */
	protected URL buildQueryURL(String server) {
		try {
			return new URL(server + URLEncoder.encode(buildOverpassQuery(), "UTF-8"));
		} catch (Exception e) {
			String error = "Creating of Overpass-Query URL failed";
			log.error(error, e);
			throw new RuntimeException(error, e);
		}
	}

	/**
	 * Generates the query in OverpassQL that can be appended to the Overpass
	 * server's URL (needs to be encoded correctly)
	 * For complex queries, all data is requested for the selected boundingbox,
	 * else a query for the individual entities is generated.
	 * @return the OverpassQL query for this instance
	 */
	public String buildOverpassQuery() {
	    if(exceedsQueryLimit()) {
	        // download all within bounds
	        return buildQueryFullyRecursive();
	    } else {
	        // download individual stuff
	        return buildQueryEntityConcat();
	    }
	}
	
	/**
	 * This query requests all data within the boundingbox. Causes some minor
	 * data overhead, but way faster than really long concatenations of
	 * individual requests
	 * @return OverpassQL query for all data in the boundingbox
	 */
	protected String buildQueryFullyRecursive() {
	    // example: (node(50.746,7.154,50.748,7.157);<;>;);out meta;
	    return "(node" + bounds.getStringOverpassBounds() + ";<;>;);out meta;";
	}
	
	/**
	 * This query requests a concatenation of all individual entities that
	 * were passed to this instance. Downloads only the data that is actually
	 * needed, but causes higher load on the servers (filter & join of data)
	 * @return OverpassQL query only for the selected entities
	 */
	protected String buildQueryEntityConcat() {
        StringBuilder sb = new StringBuilder(64*entities.size());

        sb.append(queryBegin);
        for (EntityDef entity : entities) {
            buildSingleEntityQueryPart(entity, sb);
        }
        sb.append(queryEnd);

        return sb.toString();
    }

	/**
	 * Builds the part of a OverpassQL-query that requests a single key with all
	 * values or a key-value pair, identified by the specified {@link EntityDef}
	 * @param entity the requested entity
	 * @param sb the StringBuilder to append the resulting query to
	 * @return the StringBuilder with the query appended (same as passed to
	 * this method)
	 */
	protected StringBuilder buildSingleEntityQueryPart(EntityDef entity, StringBuilder sb) {
		// type
		sb.append(entity.getType());

		// key
		sb.append("[\"");
		sb.append(escapeOverpassQuery(entity.key));
		sb.append('"');

		// value
		if(!entity.allowsAnyValue()) {
			if(entity.hasRegexValue()) {
				sb.append('~');
			} else {
				sb.append('=');
			}
			sb.append('"');
			sb.append(escapeOverpassQuery(entity.getValue()));
			sb.append('"');
		}
		sb.append(']');

		// bbox
		sb.append(bounds.getStringOverpassBounds());
		sb.append(';');

		return sb;
	}

	/**
	 * Escape characters that can't appear in a key- or value definition
	 * of a Overpass query.
	 * @param query the query to escape
	 * @return the escaped String
	 */
	protected static String escapeOverpassQuery(String query) {
		StringBuilder sb = new StringBuilder((int) (query.length() * 1.3));

		for (int i = 0; i < query.length(); i++) {
			char c = query.charAt(i);
			if(escapeChars.containsKey(c)) {
				sb.append(escapeChars.get(c));
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	protected boolean exceedsQueryLimit() {
	    return entityQueryLimit > 0 && entities.size() > entityQueryLimit;
	}
	
}