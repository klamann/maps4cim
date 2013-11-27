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
package de.nx42.maps4cim.util.gis;

import static de.nx42.maps4cim.util.math.MathExt.parseDoubleAggressive;
import static de.nx42.maps4cim.util.math.MathExt.parseDoubleValues;
import static de.nx42.maps4cim.util.math.MathExt.roundf;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;

/**
 * Definition of a single coordinate on earth, using the
 * World Geodetic System 1984 (WGS 84)
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Coordinate {

    /** Holds the Geodetic Latitude (WGS84 Ellipsoid). */
    protected final double latitudeWGS84;
    /** Holds the Geodetic Longitude (WGS84 Ellipsoid). */
    protected final double longitudeWGS84;

    /**
     * Creates a new coordinate from decimal degrees defined as doubles
     * @param latitude the geodetic latitude (decimal degrees)
     * @param longitude the geodetic longitude (decimal degrees)
     */
    public Coordinate(double latitude, double longitude) {
        this.latitudeWGS84 = latitude;
        this.longitudeWGS84 = longitude;
    }

    /**
     * Creates a new coordinate from decimal degrees defined as Strings,
     * parsed into double values
     * @param latitude the geodetic latitude (decimal degrees)
     * @param longitude the geodetic longitude (decimal degrees)
     */
    public Coordinate(String latitude, String longitude) {
        this.latitudeWGS84 = parseDoubleAggressive(latitude);
        this.longitudeWGS84 = parseDoubleAggressive(longitude);
    }

    /**
     * Creates a new coordinate from a single OpenStreetMap node
     * @param node the OSM-Node to copy latitude and longitude from
     */
    public Coordinate(Node node) {
        this.latitudeWGS84 = node.getLatitude();
        this.longitudeWGS84 = node.getLongitude();
    }

    /**
     * @return geodetic latitude (decimal degrees) of this coordinate point
     */
    public double getLatitude() {
        return latitudeWGS84;
    }

    /**
     * @return geodetic longitude (decimal degrees) of this coordinate point
     */
    public double getLongitude() {
        return longitudeWGS84;
    }

    /**
     * Creates a String representation of this coordinate in the form of
     * "{lat}°, {lon}°", e.g. "48.401°, 11.744°"
     */
    @Override
    public String toString() {
        return String.format("%s°, %s°", roundf(latitudeWGS84, 4), roundf(longitudeWGS84, 4));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitudeWGS84);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitudeWGS84);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
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
        Coordinate other = (Coordinate) obj;
        if (Double.doubleToLongBits(latitudeWGS84) != Double
                .doubleToLongBits(other.latitudeWGS84))
            return false;
        if (Double.doubleToLongBits(longitudeWGS84) != Double
                .doubleToLongBits(other.longitudeWGS84))
            return false;
        return true;
    }

    /**
     * Creates a coordinate object, ideally from a String that was created
     * using {@link Coordinate#toString()}. Note that this function tries parse
     * way more aggressively, though the results might be unpredictable for
     * different inputs.
     * @param s the string to parse. Must be in some for of "lat, lon"
     * @return the recognized Coordinate
     * @throws ParseException if the String cannot be parsed
     */
    public static Coordinate parse(String s) throws ParseException {
        try {
            double[] parsed = parseDoubleValues(s, ",");
            if (parsed.length < 2) {
                throw new ParseException(String.format(
                        "No coordinates were recognized in the string \"%s\"", s), -1);
            } else {
                return new Coordinate(parsed[0], parsed[1]);
            }
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), -1);
        }
    }

    /**
     * Converts a list of OSM-Nodes into a List of corresponding Coordinates
     * @param wayNodes the List of Nodes to convert
     * @return a list of Coordinates, same order and positions
     */
    public static List<Coordinate> convert(List<Node> wayNodes) {
        List<Coordinate> nodes = new ArrayList<Coordinate>(wayNodes.size());
        for (Node node : wayNodes) {
            nodes.add(new Coordinate(node));
        }
        return nodes;
    }

    /**
     * Calculates the relative position of this Coordinate within a specified
     * Area and returns it as RelativeCoord-Object, with latitude and longitude
     * stored as values between 0.0 and 1.0
     * @param area the area to use as reference
     * @return the relative coordinates within the area
     */
    public RelativeCoord relativeWithinArea(Area area) {
        double relLat = (this.latitudeWGS84 - area.getMinLat()) / area.getHeightDeg();
        double relLon = (this.longitudeWGS84 - area.getMinLon()) / area.getWidthDeg();
        return new RelativeCoord(relLat, relLon);
    }

    /**
     * A relative coordinate (x and y are between 0.0 and 1.0)
     */
    public class RelativeCoord {

        /** x-coordinate (latitude) */
        public final double x;
        /** y-coordinate (longitude) */
        public final double y;

        public RelativeCoord(double latitude, double longitude) {
            this.y = 1.0 - latitude;
            this.x = longitude;
        }

    }

}