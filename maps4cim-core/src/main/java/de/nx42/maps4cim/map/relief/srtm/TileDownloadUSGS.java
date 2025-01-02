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
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Table;
import com.google.common.io.Resources;

import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.util.Network;

/**
 * Download and cache required SRTM tiles
 */
public class TileDownloadUSGS extends TileDownload {

	private static final Logger log = LoggerFactory.getLogger(TileDownloadUSGS.class);

    /*
     * Base-URL: https://opendap.cr.usgs.gov/opendap/hyrax/DP109/SRTM/SRTMGL3.003/contents.html
     */

    protected final Cache cache = new Cache();

    protected static Table<Integer,Integer,DownloadURL> loadMapping() {
    	try(InputStream serialized = ResourceLoader.getMappingSRTM();) {
    	    Table<Integer,Integer,DownloadURL> mapping = ResourceLoader.deserializeObject(serialized);
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

    @Override
    public boolean exists(int lat, int lon) {
        throw new RuntimeException("website does not support HEAD requests...");
        // URL src = getDownloadURL(lat, lon);
        // return Network.exists(src);
    }

    @Override
    public File getTile(int lat, int lon) throws IOException {
        if(lat >= 59 || lat <= -59) {
            log.warn("Tile ({},{}) is close to the boundaries of the SRTM dataset, the elevations may be incorrect.", lat, lon);
        }

        // search cache first, then download file if necessary
    	String entry = DownloadURL.getFileName(lat, lon);
    	if(cache.has(entry)) {
    		log.debug("SRTM Tile ({},{}) has been loaded from cache.", lat, lon);
    		return cache.get(entry);
    	} else {
            try {
                log.debug("Downloading SRTM Tile for ({},{}). It will be stored in cache for later use.", lat, lon);
                return downloadTile(lat, lon);
            } catch (IOException e) {
                log.warn("Tile ({},{}) was not found. {}", lat, lon, e.getMessage());
                return null;
            }
    	}
    }

    protected File downloadTile(int lat, int lon) throws IOException {
        URL src = getDownloadURL(lat, lon);
        File temp = cache.allocate(DownloadURL.getFileName(lat, lon));
        Network.downloadToFile(src, temp);
        File dest = cache.moveToCache(temp, true);
        return dest;
    }

    protected URL getDownloadURL(int lat, int lon) {
    	try {
			return DownloadURL.getUrl(lat, lon);
		} catch (MalformedURLException e) {
			log.error(String.format("Could not create a valid SRTM download URL " +
					"for (%s,%s).", lat, lon), e);
			throw new RuntimeException("Creating of SRTM tile download URL failed");
		}
    }

    protected static class DownloadURL {

        protected static final String protocol = "https";
        protected static final String host = "opendap.cr.usgs.gov";
        protected static final String fileStart = "/opendap/hyrax/DP109/SRTM/SRTMGL3.003/2000.02.11/";
        protected static final String ext = ".SRTMGL3.hgt.zip";

        public static URL getUrl(int lat, int lon) throws MalformedURLException {
            return new URL(protocol, host, fileStart + getNonationNSEW(lat, lon) + ext);
        }

        public static URL getIndexURL() throws MalformedURLException {
            return new URL(protocol, host, fileStart);
        }

        public static String getFileName(int lat, int lon) {
        	return getNonationNSEW(lat, lon) + ext;
        }
    }

}
