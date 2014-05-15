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

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;

import com.google.common.base.Splitter;

import de.nx42.maps4cim.config.bounds.BBoxDef;
import de.nx42.maps4cim.config.bounds.BoundsDef;
import de.nx42.maps4cim.config.bounds.CenterDef;
import de.nx42.maps4cim.util.math.MathExt;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Area {

    protected final double minLat;  // S
    protected final double minLon;  // W
    protected final double maxLat;  // N
    protected final double maxLon;  // E

    protected int precision = 8;
    protected static final String[] parseOrder = new String[] {
            "minlat", "minlon", "maxlat", "maxlon"
    };

    // latitude = north-south, e.g. N48°
    // longitude = east-west, e.g. E11°


    public Area(double minLat, double minLon, double maxLat, double maxLon) {
        this.minLat = store(minLat);
        this.minLon = store(minLon);
        this.maxLat = store(maxLat);
        this.maxLon = store(maxLon);
    }

    public Area(double[] bounds) {
        this(bounds[0],bounds[1],bounds[2],bounds[3]);
    }

    public Area(Coordinate boundNW, Coordinate boundSE) {
        this(
            boundSE.getLatitude(),
            boundNW.getLongitude(),
            boundNW.getLatitude(),
            boundSE.getLongitude()
        );
    }

    public Area(Coordinate center, double extent) {
        this(center, extent, UnitOfLength.DEGREE);
    }

    public Area(Coordinate center, double extent, UnitOfLength unit) {
        this(center, extent, extent, unit);
    }

    public Area(Coordinate center, double extentLat, double extentLon) {
        this(center, extentLat, extentLon, UnitOfLength.DEGREE);
    }

    public Area(Coordinate center, double extentLat, double extentLon, UnitOfLength unit) {
        double adjustedLat = unit.convert(extentLat, UnitOfLength.DEGREE);
        double adjustedLon = unit.convert(extentLon, UnitOfLength.DEGREE, center.getLatitude());

        this.minLat = store(center.latitudeWGS84 - (adjustedLat / 2));
        this.maxLat = store(center.latitudeWGS84 + (adjustedLat / 2));
        this.minLon = store(center.longitudeWGS84 - (adjustedLon / 2));
        this.maxLon = store(center.longitudeWGS84 + (adjustedLon / 2));
    }

    public static Area of(BoundsDef def) {
        if(def instanceof CenterDef) {
            return Area.of((CenterDef) def);
        } else if(def instanceof BBoxDef) {
            return Area.of((BBoxDef) def);
        } else {
            throw new IllegalArgumentException(
                    "BoundsDef implementation not supported!");
        }
    }

    public static Area of(BBoxDef def) {
        return new Area(def.minLat, def.minLon, def.maxLat, def.maxLon);
    }

    public static Area of(CenterDef def) {
        if(def.isExtentValid()) {
            Double extLat = def.extent;
            Double extLon = def.extent;
            if(def.extentLat != null)
                extLat = def.extentLat;
            if(def.extentLon != null)
                extLon = def.extentLon;

            if(def.unit != null) {
                return new Area(new Coordinate(def.centerLat, def.centerLon),
                        extLat, extLon, UnitOfLength.fromUnit(def.unit));
            } else {
                return new Area(new Coordinate(def.centerLat, def.centerLon),
                        extLat, extLon);
            }
        } else {
            String err = String.format("Extent not sufficiently defined in " +
            		"configuration: extent=%s, extent-lat=%s, extent-lon=%s",
                    def.extent, def.extentLat, def.extentLon);
            throw new IllegalArgumentException(err);
        }

    }


    protected double store(double d) {
        return round(d, precision).doubleValue();
    }

    protected static BigDecimal round(double d, int scale) {
        return BigDecimal.valueOf(d).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }


    // Getters
    
    
    public double getMinLat() {
        return minLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public Coordinate getBoundNW() {
        return new Coordinate(maxLat, minLon);
    }

    public Coordinate getBoundSE() {
        return new Coordinate(minLat, maxLon);
    }

    public double getWidth(UnitOfLength unit) {
        double widthDeg = this.maxLon - this.minLon;
        double medianLat = this.minLat + (this.maxLat - this.minLat) / 2;
        return UnitOfLength.DEGREE.convert(widthDeg, unit, medianLat);
    }

    public double getHeight(UnitOfLength unit) {
        double heightDeg = this.maxLat - this.minLat;
        return UnitOfLength.DEGREE.convert(heightDeg, unit);
    }

    public double getWidthDeg() {
        return this.maxLon - this.minLon;
    }

    public double getHeightDeg() {
        return this.maxLat - this.minLat;
    }

    public double getWidthKm() {
        return getWidth(UnitOfLength.KILOMETER);
    }

    public double getHeightKm() {
        return getHeight(UnitOfLength.KILOMETER);
    }

    public Coordinate getCenter() {
        return new Coordinate((minLat+maxLat)/2, (minLon+maxLon)/2);
    }

    public String getStringOsmUrl() {
    	return String.format("minlon=%s&minlat=%s&maxlon=%s&maxlat=%s", minLon, minLat, maxLon, maxLat);
    }

    public String getStringOverpassBounds() {
    	return String.format("(%s,%s,%s,%s)", minLat, minLon, maxLat, maxLon);
    }
    
    
    // java.lang.Object Overrides
    
    
    @Override
    public String toString() {
        return getStringOverpassBounds();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Area) {
            return this.hashCode() == obj.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.minLat) ^ (Double.doubleToLongBits(this.minLat) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.minLon) ^ (Double.doubleToLongBits(this.minLon) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.maxLat) ^ (Double.doubleToLongBits(this.maxLat) >>> 32));
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.maxLon) ^ (Double.doubleToLongBits(this.maxLon) >>> 32));
        return hash;
    }
    
    
    // String-Parser

    
    public static Area parse(String s) throws ParseException {
        String input = s.trim().toLowerCase();
        if(input.contains("minlon")) {
            return parseOsmStyle(input);
        } else {
            return parseSimple(input);
        }
    }



    /**
     * Example: "minlon=11.5&minlat=47.85&maxlon=11.6&maxlat=47.95"
     *
     * @param s
     * @return
     * @throws ParseException
     */
    public static Area parseOsmStyle(String s) throws ParseException {
        try {
            Map<String,String> entries = Splitter.on('&')
                    .trimResults()
                    .omitEmptyStrings()
                    .withKeyValueSeparator('=')
                    .split(s);

            double[] bounds = new double[4];
            for (int i = 0; i < parseOrder.length; i++) {
                String value = entries.get(parseOrder[i]);
                bounds[i] = MathExt.parseDoubleAggressive(value);
            }
            return new Area(bounds);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), -1);
        }
    }

    /**
     * Example:
     * "11.5, 47.85, 11.6, 47.95"
     * minlon, minlat, maxlon, maxlat
     *
     * @param s
     * @return
     * @throws ParseException
     */
    public static Area parseSimple(String s) throws ParseException {
        try {
            double[] parsed = MathExt.parseDoubleValues(s, ",");
            return new Area(parsed);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), -1);
        }
    }

    /**
     * "11.5,47.5,0.15,0.10"     // lon,lat,extLon,extLat
     * "11.5,47.5,0.12"     // lon,lat,ext
     *
     * @param s
     * @return
     * @throws NumberFormatException
     */
    public static Area parseCenter(String s) throws ParseException {
        try {
            double[] parsed = MathExt.parseDoubleValues(s, ",");
            if(parsed.length >= 4) {
                return new Area(new Coordinate(parsed[1], parsed[0]), parsed[3], parsed[2]);
            } else {
                return new Area(new Coordinate(parsed[1], parsed[0]), parsed[2]);
            }
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), -1);
        }
    }
    
}
