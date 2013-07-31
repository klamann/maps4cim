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
package de.nx42.maps4cim.map.relief.srtm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;

import com.google.common.collect.Table;
import com.google.common.io.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.util.Network;
import de.nx42.maps4cim.util.gis.Area;

/**
 * Download and cache required SRTM tiles
 */
public class TileDownload {

	private static final Logger log = LoggerFactory.getLogger(TileDownload.class);

    /*
     * Map: http://dds.cr.usgs.gov/srtm/version2_1/Documentation/Continent_def.gif
     * Base-URL: http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/
     */

	/**
	 * Maps from <Lat,Lon> to <DownloadURL>. Nonexisting tiles are not contained
	 * in this data structure.
	 */
    protected final Table<Integer,Integer,DownloadURL> downloadMapping;
    protected final Cache cache = new Cache();

    public TileDownload() {
    	this.downloadMapping = loadMapping();
    }

    protected static Table<Integer,Integer,DownloadURL> loadMapping() {
    	InputStream serialized = ResourceLoader.getMappingSRTM();
    	try {
    	    Table<Integer,Integer,DownloadURL> mapping = ResourceLoader.deserializeObject(serialized);
    	    serialized.close();
    	    return mapping;
		} catch (FileNotFoundException e) {
			log.error("The serialized srtm-mapping was not found in the classpath!", e);
		} catch (ClassNotFoundException e) {
			log.error("The srtm-mapping could not be casted to the guava Table datastructure.", e);
		} catch (IOException e) {
			log.error("Error while accessing the serialized srtm-mapping.", e);
		}
    	throw new RuntimeException("Could not load SRTM Download-URL mapping!");
    }

    public boolean exists(double lat, double lon) {
        return downloadMapping.contains(floor(lat), floor(lon));
    }

    public boolean exists(int lat, int lon) {
    	return downloadMapping.contains(lat, lon);
    }

    public File[][] getTiles(Area ar) throws SocketTimeoutException, IOException, UnknownHostException {

        // fst: lat: bottom to top [-90;90] or S90 to N90
        // snd: lon: left to right [-180;180] or W180 to E180
        // [0][0] is bottom left (south west) [n][n] is top right (north east)

        int minLat = floor(ar.getMinLat());
        int maxLat = (int) Math.ceil(ar.getMaxLat());
        int minLon = floor(ar.getMinLon());
        int maxLon = (int) Math.ceil(ar.getMaxLon());

        int sizeLat = maxLat - minLat;
        int sizeLon = maxLon - minLon;

        // special case: area overlaps the 180° longitude
        if(minLon > maxLon) {   // e.g. min: 175, max: -178
            // TODO implement this...
            log.warn("Your selection overlaps the east/west border at " +
            		"E180, W180 -> this is currently not supported!");
        }

        File[][] tiles = new File[sizeLat][sizeLon];
        for (int i = 0; i < sizeLat; i++) {
            for (int j = 0; j < sizeLon; j++) {
                File tile = getTile(minLat + i, minLon + j);
                tiles[i][j] = tile;
            }
        }
    	return tiles;
    }

    public File getTile(double lat, double lon) throws SocketTimeoutException, IOException {
    	return getTile(floor(lat), floor(lon));
    }

    protected int floor(double a) {
        return (int) StrictMath.floor(a);
    }

    public File getTile(int lat, int lon) throws SocketTimeoutException, IOException, UnknownHostException {

        // return null reference, if tile does not exist (only sea level)
        if(!exists(lat, lon)) {
            log.warn("Tile ({},{}) is not covered in the SRTM dataset, so this part of the map will be flat.", lat, lon);
            return null;
        }

        // search cache first, then download file if necessaray
    	String entry = DownloadURL.getFileName(lat, lon);
    	if(cache.has(entry)) {
    		log.debug("SRTM Tile ({},{}) has been loaded from cache.", lat, lon);
    		return cache.get(entry);
    	} else {
    		log.debug("Downloading SRTM Tile for ({},{}). It will be stored in cache for later use.", lat, lon);
    		URL src = getDownloadURL(lat, lon);
    		File dest = cache.allocate(entry);
    		Network.downloadToFile(src, dest, 5, 2);
    		return dest;
    	}

    }

    protected URL getDownloadURL(int lat, int lon) {
    	DownloadURL dl = downloadMapping.get(lat, lon);
    	String url = dl.getUrl(lat, lon);
    	try {
			return new URL(url);
		} catch (MalformedURLException e) {
			log.error(String.format("Could not create a valid SRTM download URL " +
					"for (%s,%s). Result was %s", lat, lon, url), e);
			throw new RuntimeException("Creating of SRTM tile download URL failed");
		}
    }

    protected static SimpleCoord parseCoordinate(String hgtFileName) throws ParseException {

    	/*
    	 * lat:
    	 * - Format: [N|S]dd
    	 * - N -> positive
    	 * - S -> negative
    	 * lon:
    	 * - Format: [E|W]ddd
    	 * - E -> positive
    	 * - W -> negative
    	 */

    	String parse = hgtFileName.trim();
    	int lat = Integer.MIN_VALUE;
    	int lon = Integer.MIN_VALUE;

    	// parse
    	for (int i = 0; i < parse.length(); i++) {
            char c = parse.charAt(i);
            if(c == 'N') {
            	lat = getNumAfterIndex(parse, ++i);
            } else if(c == 'S') {
            	lat = -(getNumAfterIndex(parse, ++i));
            } else if(c == 'E') {
            	lon = getNumAfterIndex(parse, ++i);
            } else if(c == 'W') {
            	lon = -(getNumAfterIndex(parse, ++i));
            }
        }

    	// check results
    	if(lat < -90 || lat > 90) {
    		throw new ParseException(String.format("Latitude must be between " +
    				"[-90;+90], but is %s", lat), -1);
    	} else if(lon < -180 || lon > 180) {
    		throw new ParseException(String.format("Longitude must be between " +
    				"[-180;+180], but is %s", lon), -1);
    	}

    	// return coordinate
        return new SimpleCoord(lat, lon);
    }

    /**
     * Returns the biggest number that can be parsed in a string after a specified
     * index, e.g.:
     * "number25 be it"
     * for index 6 (which is the position of '2') this method will return 25,
     * for index 7 it will be just 5, all other indices will throw
     * NumberFormatException (no number found) or some IndexOutOfBounds.
     * @param s the string to look for entries in
     * @param start the first index of the number to parse
     * @return
     */
    protected static int getNumAfterIndex(String s, int start) throws ParseException {
    	if(!Character.isDigit(s.charAt(start))) {
    		throw new ParseException(String.format("No digit recognized at" +
    				" index %s in %s, just '%s'", start, s, s.charAt(start)), start);
    	}

    	int last = start+1;
    	search:
    	while(last < s.length()) {
    		if(Character.isDigit(s.charAt(last))) {
    			last++;
        	} else {
        		break search;
        	}
    	}
    	return Integer.parseInt(s.substring(start, last));
    }

    // data structures specific for this class

    protected enum DownloadURL {

        Africa("Africa"),
        Australia("Australia"),
        Eurasia("Eurasia"),
        Islands("Islands"),
        North_America("North_America"),
        South_America("South_America");

        protected static final String base = "http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/";
        protected static final String ext = ".hgt.zip";

        protected final String folder;

        DownloadURL(String folder) {
            this.folder = folder;
        }

        public String getFolder() {
            return folder;
        }

        public String getUrl(int lat, int lon) {

            StringBuilder sb = new StringBuilder(80);
            sb.append(base);
            sb.append(this.folder);
            sb.append('/');
            sb.append(getNonationNSEW(lat, lon));
            sb.append(ext);

            return sb.toString();
        }

        public String getIndexURL() {
        	return base + getFolder();
        }

        public File getIndexLocal() {
        	try {
				return  new File(Resources.getResource("srtm/" + folder + ".html").toURI());
			} catch (URISyntaxException e) {
				log.error("Could not resolve path to local file", e);
				return null;
			}
        }

        /**
         * Transforms lat=(+/-)n, lon=(+/-)m to "[N|S]dd[E|W]ddd"
         * e.g. lat=47, lon=11 becomes "N47E011"
         * and  lat=-11, lon=-123 becomes "S11W123"
         * @param lat the geographic latitude
         * @param lon the geographic longitude
         * @return string representation using N,S,E,W notation
         */
        public static String getNonationNSEW(int lat, int lon) {
        	StringBuilder sb = new StringBuilder(16);

        	sb.append(lat >= 0 ? 'N' : 'S');
        	sb.append(String.format("%02d", Math.abs(lat)));
        	sb.append(lon >= 0 ? 'E' : 'W');
        	sb.append(String.format("%03d", Math.abs(lon)));

            return sb.toString();
        }

        public static String getFileName(int lat, int lon) {
        	return getNonationNSEW(lat, lon) + ext;
        }
    }



    protected static class SimpleCoord {
        public final int lat;
        public final int lon;
        public SimpleCoord(int lat, int lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }



   /*
    * The following code depends on JSoup (POM: org.jsoup).
    * Remove the comments to update the tile mapping (usually, this
    * is not required)
    * DO NOT REMOVE THIS CODE
    */

//    protected static void storeMapping(File f) {
//        try {
//            // generate mapping
//            Table<Integer, Integer, DownloadURL> mapping = generateMapping();
//            try {
//                // serialize
//                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
//                oos.writeObject(mapping);
//                oos.flush();
//                oos.close();
//            } catch (Exception e) {
//                log.error("Serializing of srtm-mapping failed.", e);
//            }
//        } catch (Exception e) {
//            log.error("Writing of srtm-mapping failed.", e);
//        }
//    }
//
//    protected static Table<Integer,Integer,DownloadURL> generateMapping() throws MalformedURLException, IOException, ParseException, SocketTimeoutException {
//
//        Table<Integer,Integer,DownloadURL> hits = TreeBasedTable.create();
//        for (DownloadURL url : DownloadURL.values()) {
//            //String index = url.getIndexURL();
//            File index = url.getIndexLocal();
//            //Document doc = Jsoup.connect(index).userAgent("Mozilla").timeout(8000).get();
//            Document doc = Jsoup.parse(index, null);
//            Elements links = doc.select("ul > li > a[href]");
//            for (Element link : links) {
//                String hit = link.attr("href");
//                if (hit.endsWith("hgt.zip")) {
//                    String name = hit.substring(hit.lastIndexOf('/'));
//                    SimpleCoord coord = parseCoordinate(name);
//                    hits.put(coord.lat, coord.lon, url);
//                }
//            }
//        }
//        return hits;
//    }

}
