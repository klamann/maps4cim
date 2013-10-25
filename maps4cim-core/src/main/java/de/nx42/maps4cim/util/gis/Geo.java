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

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Geo {

    /** the earths radius, idealized (WGS84) */
    public static final double earthRadius = 6378.137;
    /** the earth's circumference, idealized */
    public static final double earthCircumference = earthRadius * 2 * Math.PI;
    /** the length of one degree of latitude (in km) */
    public static final double degreeOfLatitudeLength = earthCircumference / 360.0;

    /**
     * The length of one degree of longitude (in km) at the specified latitude.
     * These values are larges at the equator and decrease to 0 at the poles
     * @param latitude the Geodetic Latitude (WGS84 Ellipsoid)
     * @return the length of one degree of longitude (in km)
     */
    public static double degreeOfLongitudeLength(double latitude) {
        return circleAtLatitude(latitude) / 360;
    }

    /**
     * The circumference of the latitude ring at the specified position
     * in kilometers (km).
     * @param latitude the Geodetic Latitude (WGS84 Ellipsoid)
     * @return circumference of the latitude ring in km
     */
    public static double circleAtLatitude(double latitude) {
        return 2 * Math.PI * earthRadius * Math.cos(Math.toRadians(latitude));
    }

}
