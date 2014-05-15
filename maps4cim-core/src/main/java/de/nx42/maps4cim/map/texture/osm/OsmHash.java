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
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import org.apache.commons.codec.binary.Base64;

import com.github.jinahya.io.bit.BitInput;
import com.github.jinahya.io.bit.BitOutput;

import de.nx42.maps4cim.config.texture.osm.EntityDef;
import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.map.ex.TextureProcessingException;
import de.nx42.maps4cim.util.Compression;
import de.nx42.maps4cim.util.gis.Area;

/**
 * A hash code for a OSM XML dataset. Consists of two individual hashes, one
 * for the bounding box (reversible) and one for the selected entities
 * (irreversible).
 */
public class OsmHash {
    
    protected static final HashFunction hf = Hashing.md5();
    protected static final Cache cache = new Cache();
    
    /** precision of a single coordinate (including 1 bit sign */
    protected static final int locationPrecision = 18;
    /** coordinates are converted to int by multiplying with this value */
    protected static final double locationMulti = 720;
    
    /** the hash code of the location (reversible) */
    protected final String locationHash;
    /** the hash code for the entities (irreversible, except for full query) */
    protected final String entityHash;
    /** is all data for this area downloaded, or only specific entities? */
    protected final boolean fullDataSet;
    /** the computed hash code (combination of location- and entity-hash */
    protected final String queryHash;
    
    
    public OsmHash(Area bounds, List<EntityDef> entities, boolean fullDataSet) throws TextureProcessingException {
        this.fullDataSet = fullDataSet;
        this.entityHash = fullDataSet ? "all" : getQueryHashEntities(entities);
        try {
            this.locationHash = getQueryHashLocation(bounds);
            this.queryHash = locationHash + "-" + entityHash;
        } catch(IOException e) {
            throw new TextureProcessingException("Unexpected Exception: Could not generate hash for this location", e);
        }
    }
    
    public OsmHash(Area bounds, List<EntityDef> entities) throws TextureProcessingException {
        this(bounds, entities, false);
    }
    
    public OsmHash(Area bounds) throws TextureProcessingException {
        this(bounds, null, true);
    }
    
    // ----- getters -----

    /**
     * @return the queryHash
     */
    public String getQueryHash() {
        return queryHash;
    }

    /**
     * @return the fullDataSet
     */
    public boolean hasFullDataSet() {
        return fullDataSet;
    }
    
    public Area getHashedLocation() throws IOException {
        return parseLocationHash(locationHash);
    }

    // ----- actual calculations -----
    
    protected static String getQueryHashLocation(Area bounds) throws IOException {
        
        /*
         * - base64 encode, every char holds 6 bits (2^6 = 64)
         * - 3 chars per float = 18bit precision = enough for 3 sigificant digits
         *   for numbers <256 (which is the case in WGS84)
         * - 4 values -> 12 chars. 72bit or 8 byte of information
         * - use URL safe encoding, or file name failures are expected!
         */
        
        // calculate size:  4 values, 8 bits per byte
        int bufSize = (int) Math.ceil(4 * locationPrecision / 8.0);
        
        ByteBuffer byteBuf = ByteBuffer.allocate(bufSize);
        BitOutput bitOut = BitOutput.newInstance(byteBuf); // direct
        
        storeCoordinate(bounds.getMinLat(), bitOut);
        storeCoordinate(bounds.getMaxLat(), bitOut);
        storeCoordinate(bounds.getMinLon(), bitOut);
        storeCoordinate(bounds.getMaxLon(), bitOut);
        
        // get array, return as Base64 (URL safe)
        byte[] ar = byteBuf.array();
        return Base64.encodeBase64URLSafeString(ar);
        
    }
    
    protected static Area parseLocationHash(String locationHash) throws IOException {
        byte[] base64decode = Base64.decodeBase64(locationHash);
        ByteBuffer byteBuf = ByteBuffer.wrap(base64decode);
        final BitInput bitIn = BitInput.newInstance(byteBuf);
        
        double minLat = restoreCoordinate(bitIn);
        double maxLat = restoreCoordinate(bitIn);
        double minLon = restoreCoordinate(bitIn);
        double maxLon = restoreCoordinate(bitIn);
        
        return new Area(minLat, minLon, maxLat, maxLon);
    }

    protected static void storeCoordinate(double coordinate, BitOutput out) throws IOException {
        out.writeInt(locationPrecision, (int) Math.round(coordinate * locationMulti));
    }
    
    protected static double restoreCoordinate(BitInput in) throws IOException {
        return in.readInt(locationPrecision) / locationMulti;
    }
    
    protected static String getQueryHashEntities(List<EntityDef> entities) {
        Hasher h = hf.newHasher();
        
        // add query
        for (EntityDef def : entities) {
            h.putInt(def.hashCode());
        }
        
        // get hash, cap length
        byte[] hash = h.hash().asBytes();
        if(hash.length > 4) {
            hash = Arrays.copyOfRange(hash, 0, 3);
        }
        
        // return hash, max 4 byte (6 chars)
        return Base64.encodeBase64URLSafeString(hash);
    }

    // ----- cache -----

    public boolean isCached() {
        return cache.has(getCacheFileName(queryHash));
    }

    protected File getCached() throws IOException {
        File zipped = cache.get(getCacheFileName(queryHash));
        File unzipped = Cache.temporaray(getXmlFileName(queryHash));
        return Compression.readFirstZipEntry(zipped, unzipped);
    }
    
    protected String getXmlFileName() {
        return getXmlFileName(queryHash);
    }

    /**
     * Stores the file with a qualified file name and the given hash in the
     * user's cache directory, so it can be later retrieved again.
     * The file will be zipped.
     * @param xml the osm xml file to cache
     * @param hash the hash code that identifies this file
     * @throws IOException if the file cannot be moved to the cache
     */
    protected void storeInCache(File xml) throws IOException {
        File zip = cache.allocate(getCacheFileName(queryHash));
        Compression.storeAsZip(xml, zip);
        cache.moveToCache(zip, true);
    }

    protected static String getXmlFileName(String hash) {
        return "osm-" + hash + ".xml";
    }

    protected static String getCacheFileName(String hash) {
        return "osm-" + hash + ".xml.zip";
    }

    // ----- object overrides -----
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return queryHash.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OsmHash other = (OsmHash) obj;
        if (queryHash == null) {
            if (other.queryHash != null)
                return false;
        } else if (!queryHash.equals(other.queryHash))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return queryHash;
    }
    
}
