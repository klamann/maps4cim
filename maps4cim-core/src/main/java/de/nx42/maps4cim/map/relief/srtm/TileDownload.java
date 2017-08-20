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
import java.io.IOException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.util.gis.Area;

/**
 * Download and cache required SRTM tiles
 */
public abstract class TileDownload {
    
    private static final Logger log = LoggerFactory.getLogger(TileDownload.class);

    protected final Cache cache = new Cache();

    public boolean exists(double lat, double lon) {
        return exists(floor(lat), floor(lon));
    }

    public abstract boolean exists(int lat, int lon);

    public File[][] getTiles(Area ar) throws IOException {
        CoordinateInt[][] coords = getCoordinates(ar);
        File[][] tiles = new File[coords.length][coords[0].length];
        for (int i = 0; i < coords.length; i++) {
            for (int j = 0; j < coords[i].length; j++) {
                tiles[i][j] = getTile(coords[i][j]);
            }
        }
        return tiles;
    }
    
    static CoordinateInt[][] getCoordinates(Area ar) {
        int minLat = floor(ar.getMinLat());
        int maxLat = (int) Math.ceil(ar.getMaxLat());
        int minLon = floor(ar.getMinLon());
        int maxLon = (int) Math.ceil(ar.getMaxLon());
        int sizeLat = maxLat - minLat;
        int sizeLon = minLon > maxLon ? ((180-minLon) + (180+maxLon)) : maxLon - minLon;
        
        CoordinateInt[][] coords = new CoordinateInt[sizeLat][sizeLon];
        for (int i = 0; i < sizeLat; i++) {
            int gap = 180-minLon;
            for (int j = 0; j < sizeLon; j++) {
                int lon = minLon+j < 180 ? minLon+j : (-180+j-gap);
                coords[i][j] = new CoordinateInt(minLat + i, lon);
            }
        }
        return coords;
    }
    
    public File getTile(CoordinateInt c) throws IOException {
        return getTile(c.lat, c.lon);
    }

    public File getTile(double lat, double lon) throws IOException {
        return getTile(floor(lat), floor(lon));
    }

    public abstract File getTile(int lat, int lon) throws IOException;

    protected static CoordinateInt parseCoordinate(String hgtFileName) throws ParseException {

        /*
         * lat: - Format: [N|S]dd - N -> positive - S -> negative lon: - Format:
         * [E|W]ddd - E -> positive - W -> negative
         */

        String parse = hgtFileName.trim();
        int lat = Integer.MIN_VALUE;
        int lon = Integer.MIN_VALUE;

        // parse
        for (int i = 0; i < parse.length(); i++) {
            char c = parse.charAt(i);
            if (c == 'N') {
                lat = getNumAfterIndex(parse, ++i);
            } else if (c == 'S') {
                lat = -(getNumAfterIndex(parse, ++i));
            } else if (c == 'E') {
                lon = getNumAfterIndex(parse, ++i);
            } else if (c == 'W') {
                lon = -(getNumAfterIndex(parse, ++i));
            }
        }

        // check results
        if (lat < -90 || lat > 90) {
            throw new ParseException(String.format("Latitude must be between " + "[-90;+90], but is %s", lat), -1);
        } else if (lon < -180 || lon > 180) {
            throw new ParseException(String.format("Longitude must be between " + "[-180;+180], but is %s", lon), -1);
        }

        // return coordinate
        return new CoordinateInt(lat, lon);
    }

    /**
     * Returns the biggest number that can be parsed in a string after a
     * specified index, e.g.: "number25 be it" for index 6 (which is the
     * position of '2') this method will return 25, for index 7 it will be just
     * 5, all other indices will throw NumberFormatException (no number found)
     * or some IndexOutOfBounds.
     * 
     * @param s
     *            the string to look for entries in
     * @param start
     *            the first index of the number to parse
     * @return
     */
    protected static int getNumAfterIndex(String s, int start) throws ParseException {
        if (!Character.isDigit(s.charAt(start))) {
            throw new ParseException(String.format("No digit recognized at "
                    + "index %s in %s, just '%s'", start, s, s.charAt(start)), start);
        }

        int last = start + 1;
        search: while (last < s.length()) {
            if (Character.isDigit(s.charAt(last))) {
                last++;
            } else {
                break search;
            }
        }
        return Integer.parseInt(s.substring(start, last));
    }

    static int floor(double a) {
        return (int) StrictMath.floor(a);
    }
    
    /**
     * Transforms lat=(+/-)n, lon=(+/-)m to "[N|S]dd[E|W]ddd" e.g. lat=47,
     * lon=11 becomes "N47E011" and lat=-11, lon=-123 becomes "S11W123"
     * 
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

    protected static class CoordinateInt {
        public final int lat;
        public final int lon;
        public CoordinateInt(int lat, int lon) {
            this.lat = lat;
            this.lon = lon;
        }
        @Override
        public int hashCode() {
            return lat + 97 * lon;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CoordinateInt other = (CoordinateInt) obj;
            if (lat != other.lat)
                return false;
            if (lon != other.lon)
                return false;
            return true;
        }
        @Override
        public String toString() {
            return "CoordinateInt [lat=" + lat + ", lon=" + lon + "]";
        }
    }

}
