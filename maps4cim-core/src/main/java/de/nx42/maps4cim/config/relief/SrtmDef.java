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
package de.nx42.maps4cim.config.relief;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Strings;

import net.sf.oval.constraint.MatchPattern;


@XmlRootElement(name = "srtm")
public class SrtmDef extends ReliefDef {

    /** this regex decides, if the height value is valid (none, auto, or a decimal) */
    protected static final String complexTypeRegex = "^\\s*(auto|none|\\-?\\d+(\\.\\d+)?)\\s*$";
    
    /**
     * The height offset defines the virtual zero point of the map. Important
     * for any area that is not near sea-level, as any elevation above 500m
     * is likely to be drawn as snowed in mountain cap...
     *
     * This String is a complex type which contains either "none", "auto", or
     * a decimal offset value. defaults to "auto" (which sets the virtual 0
     * point to the lowest point on the visible map)
     */
    @XmlAttribute(name = "height-offset")
    @MatchPattern(pattern = { complexTypeRegex }, message = "Invalid value for height offset")
    public String heightOffset;     // default: auto

    /**
     * The height scale defines how much the elevations shall be stretched
     * relative to reality. Note that in order to get realistic results for
     * maps that do not match the ingame map scale, the heightscale must also
     * be adjusted.
     * Large maps need a lower scale, while very small maps need a higher one.
     * Also, the scale can be increased to create a more impressive environment
     * in rather flat areas, or decreased to make very large height differences
     * in mountainous areas fit in the bounds of the game engine.
     *
     * This String is a complex type which contains either "none", "auto", or
     * a decimal offset value. defaults to "auto" (which sets the height scale
     * according to the map's extent)
     * 
     * Note that this field does not hold percent values, so 1.0 would mean
     * 100%, 0.0 indicates no elevation at all and 100.0 will give an
     * exaggerated result...
     */
    @XmlAttribute(name = "height-scale")
    @MatchPattern(pattern = { complexTypeRegex }, message = "Invalid value for height scale")
    public String heightScale;      // default: auto

    // Getters

    /**
     * @return true, iff the height offset is set to auto
     */
    public boolean isHeightOffsetAuto() {
        return getNoneAutoDecimalType(heightOffset) == NoneAutoDecimal.AUTO;
    }

    public boolean isHeighScaleAuto() {
        return getNoneAutoDecimalType(heightScale) == NoneAutoDecimal.AUTO;
    }

    /**
     * @return the user defined height offset (defaults to 0 if undefined, so
     *         it's safe to use)
     */
    public double getHeightOffset() {
        return getNoneAutoDecimalAsDouble(heightOffset, 0);
    }

    /**
     * @return the user defined height scale (defaults to 1.0 if undefined,
     *         so it's safe to use)
     */
    public double getHeightScale() {
        return getNoneAutoDecimalAsDouble(heightScale, 1.0);
    }

    // static helpers

    protected static NoneAutoDecimal getNoneAutoDecimalType(String field) {
        if(field != null) {
            return NoneAutoDecimal.parse(field);
        } else {
            return NoneAutoDecimal.NONE;
        }
    }

    protected static double getNoneAutoDecimalAsDouble(String field, double fallback) {
        if(Strings.isNullOrEmpty(field)) {
            return fallback;
        }
        try {
            return Double.parseDouble(field);
        } catch(NumberFormatException e) {
            return fallback;
        }
    }
    
    public static SrtmDef of(String heightOffset, String heightScale) {
        SrtmDef s = new SrtmDef();
        s.heightOffset = heightOffset;
        s.heightScale = heightScale;
        return s;
    }

    // datatype

    /**
     * Definition of a special data type which can point to
     * - none, which indicates the field is not defined
     * - auto, which lets the system decide which value fits best
     * - a custom static decimal as defined by the user
     */
    public enum NoneAutoDecimal {
        /** try to eliminate the effect of this variable, if possible */
        NONE,
        /** let the system determine the optimal value */
        AUTO,
        /** user defined, fixed decimal value */
        CUSTOM;

        /**
         * Parses the input string from the configuration
         * @param value the string to parse
         * @return the recognized type
         */
        public static NoneAutoDecimal parse(String value) {
            String val = value.toLowerCase().trim();
            if("none".equals(val)) {
                return NONE;
            } else if("auto".equals(val)) {
                return AUTO;
            } else {
                return CUSTOM;
            }
        }
    }

}
