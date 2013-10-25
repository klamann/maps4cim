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
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.texture.EntityDef;
import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.map.ex.TextureProcessingException;
import de.nx42.maps4cim.util.Compression;
import de.nx42.maps4cim.util.Network;
import de.nx42.maps4cim.util.gis.Area;

/**
 * This class serves as bridge to the Overpass API.
 * It parses the selectors stored in the config, transforms them into valid
 * OverpassQL, executes the queries, caches the result and passes it on.
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class OverpassBridge {

	private static final Logger log = LoggerFactory.getLogger(OverpassBridge.class);

	/*
	 * Overpass allows
	 * - exact matches ["key"="value"]
	 * - regex matches ["key"~"value"]
	 *   -> beware: backslashes must be escaped before sending, e.g.
	 *      ["name"~"^St\."] -> ["name"~"^St\\."]
	 *      this is a OverpassQL special...
	 *
	 * How to implement this?
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
	 */



	/** known public Overpass servers */
	protected static final String[] servers = new String[] {
		"http://overpass.osm.rambler.ru/cgi/interpreter?data=",
		"http://overpass-api.de/api/interpreter?data=",
		"http://api.openstreetmap.fr/oapi/interpreter?data="
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
	protected Cache cache = new Cache();
	protected boolean caching = true;


	public OverpassBridge(Config conf) {
		this.bounds = Area.of(conf.bounds);
		this.entities = conf.texture.entities;
	}


	public File getResult() throws TextureProcessingException {
		String hash = getQueryHash();

		if(isCached(hash)) {
			log.debug("Retrieving Overpass query result from cache.");
			try {
				return getCached(hash);
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

	protected File downloadAndCache(String hash) throws TextureProcessingException {
		Exception inner = null;
		for (String server : servers) {
			try {
			    Stopwatch stopwatch = new Stopwatch();
	            stopwatch.start();

				// generate Query and store result in temp
				URL query = buildURL(server);
				File dest = Cache.temporaray(getXmlFileName(hash));
				downloadQueryResult(query, dest);

				// zip result and store in cache
				if(caching) {
					storeInCache(dest, hash);
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

	public File downloadQueryResult(URL query, File dest) throws SocketTimeoutException, IOException {
		// 5 seconds connection timeout, 90 seconds for the server to execute the query
		// (so after this time, the download must start, or a timeout occurs)
		Network.downloadToFile(query, dest, 5, 90);
		return dest;
	}

	protected URL buildURL(String server) {
		try {
			return new URL(server + URLEncoder.encode(buildQuery(), "UTF-8"));
		} catch (Exception e) {
			String error = "Creating of Overpass-Query URL failed";
			log.error(error, e);
			throw new RuntimeException(error, e);
		}
	}

	/**
	 * Writes a query in compact Overpass-QL that can be used in URLs
	 * @return
	 */
	public String buildQuery() {
		StringBuilder sb = new StringBuilder(64*entities.size());

		sb.append(queryBegin);
		for (EntityDef entity : entities) {
			buildSingleEntityQuery(entity, sb);
		}
		sb.append(queryEnd);

		return sb.toString();
	}

	protected StringBuilder buildSingleEntityQuery(EntityDef entity, StringBuilder sb) {
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
	 * of a overpass query.
	 * @param query the query to escape
	 * @return the clean values
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

	protected String getQueryHash() {
		HashFunction hf = Hashing.md5();
		Hasher h = hf.newHasher();

		// add bounds with up to 4 significant digits (more precision not required)
		h.putInt((int) (bounds.getMinLat() * 10000))
			.putInt((int) (bounds.getMaxLat() * 10000))
			.putInt((int) (bounds.getMinLon() * 10000))
			.putInt((int) (bounds.getMaxLon() * 10000));

		// add query
		for (EntityDef def : entities) {
			h.putInt(def.hashCode());
		}

		// just return an absolute long value (that should suffice to avoid collisions)
		return String.valueOf(Math.abs(h.hash().asLong()));
	}

	protected boolean isCached(String hash) {
		return cache.has(getCacheFileName(hash));
	}

	protected File getCached(String hash) throws IOException {
		File zipped = cache.get(getCacheFileName(hash));
		File unzipped = Cache.temporaray(getXmlFileName(hash));
		return Compression.readFirstZipEntry(zipped, unzipped);
	}

	protected void storeInCache(File xml, String hash) throws IOException {
		File zip = cache.allocate(getCacheFileName(hash));
		Compression.storeAsZip(xml, zip);
	}


	protected static String getXmlFileName(String hash) {
		return "osm-" + hash + ".xml";
	}

	protected static String getCacheFileName(String hash) {
		return "osm-" + hash + ".xml.zip";
	}


}
