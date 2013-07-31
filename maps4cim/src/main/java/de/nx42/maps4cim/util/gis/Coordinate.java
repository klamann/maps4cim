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

import static de.nx42.maps4cim.util.MathExt.parseDoubleAggressive;
import static de.nx42.maps4cim.util.MathExt.parseDoubleValues;
import static de.nx42.maps4cim.util.MathExt.roundf;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Coordinate {

    /**
     * Holds the Geodetic Latitude (WGS84 Ellipsoid).
     */
    protected final double latitudeWGS84;
    /**
     * Holds the Geodetic Longitude (WGS84 Ellipsoid).
     */
    protected final double longitudeWGS84;


    public Coordinate(double latitude, double longitude) {
        this.latitudeWGS84 = latitude;
        this.longitudeWGS84 = longitude;
    }

    public Coordinate(String latitude, String longitude) {
        this.latitudeWGS84 = parseDoubleAggressive(latitude);
        this.longitudeWGS84 = parseDoubleAggressive(longitude);
    }

    public Coordinate(Node node) {
        this.latitudeWGS84 = node.getLatitude();
        this.longitudeWGS84 = node.getLongitude();
    }


    public double getLatitude() {
        return latitudeWGS84;
    }

    public double getLongitude() {
        return longitudeWGS84;
    }


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

    public static List<Coordinate> convert(List<Node> wayNodes) {
        List<Coordinate> nodes = new ArrayList<Coordinate>(wayNodes.size());
        for (Node node : wayNodes) {
            nodes.add(new Coordinate(node));
        }
        return nodes;
    }


    public RelativeCoord relativeWithinArea(Area area) {
        double relLat = (this.latitudeWGS84 - area.getMinLat()) / area.getHeightDeg();
        double relLon = (this.longitudeWGS84 - area.getMinLon()) / area.getWidthDeg();
        return new RelativeCoord(relLat, relLon);
    }

    public class RelativeCoord {
        public final double x;
        public final double y;
        public RelativeCoord(double latitude, double longitude) {
            this.y = 1.0 - latitude;
            this.x = longitude;
        }
    }



}