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
package de.nx42.maps4cim.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Strings;

/**
 * Settings concerning the relief of the map (elevation data)
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlRootElement(name = "relief")
public class ReliefDef {
    
    /**
     * The source determines the data source to use to render the relief of the
     * map. The recommended way is to use the SRTM dataset, which creates
     * quite accurate real-world results, but it is also possible to create
     * no relief at all or to use other sources
     * @see ReliefSource
     */
    @XmlAttribute(name = "source")
    public ReliefSource source;

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
     */
    @XmlAttribute(name = "height-scale")
    public Double heightScale;      // default: 1.0

    /**
     * @return the data source for the relief to render
     */
    public ReliefSource getSource() {
        return source == null ? ReliefSource.srtm : source;
    }

    /**
     * @return the height offset type. Can be custom (user defined, fixed),
     * auto (determine based on relief data) or none (do not apply any offset)
     */
    public HeightOffset getHeightOffsetType() {
        if(heightOffset != null) {
            return HeightOffset.parse(heightOffset);
        } else {
            return HeightOffset.NONE;
        }
    }

    /**
     * @return true, iff the height offset is set to auto
     */
    public boolean isHeightOffsetAuto() {
        return getHeightOffsetType() == HeightOffset.AUTO;
    }

    /**
     * Only use this getter, if HeightOffsetType == CUSTOM!
     * @return the user defined height offset (defaults to 0 if undefined, so
     *         it's safe to use)
     * @see ReliefDef#getHeightOffsetType()
     *         have a look at the offset type first before blindly trusting
     *         this value!
     */
    public double getHeightOffset() {
        if(Strings.isNullOrEmpty(heightOffset)) {
            return 0;
        }
        try {
        	return Double.parseDouble(heightOffset);
        } catch(NumberFormatException e) {
        	return 0;
        }
    }

    /**
     * @return the user defined height scale. defaults to 1.0 if undefined,
     *         so it's safe to use at any time
     */
    public Double getHeightScale() {
        if(heightScale != null && heightScale > 0.01 && heightScale <= 100) {
            return heightScale;
        } else {
            return 1.0;
        }
    }

    /**
     * @return a new ReliefDef with data source: none (just a flat map)
     */
    public static ReliefDef none() {
        return new ReliefDef() {{
                source = ReliefSource.none;
        }};
    }
    
    /**
     * Creates a new ReliefDef with data source: srtm, using the specified values
     * @param ho the height offset to use (if applicable)
     * @param hoAuto true, if the height offset is set to "auto"
     * @param hs the height scale to use
     * @return a new ReliefDef with data source: srtm
     */
	public static ReliefDef srtm(final double ho, final boolean hoAuto, final double hs) {
		return new ReliefDef() {{
		        source = ReliefSource.srtm;
				heightOffset = hoAuto ? "auto" : String.valueOf(ho);
				heightScale = hs;
		}};
	}

    /**
     * The height offset type as defined by the user.
     */
    public enum HeightOffset {
    	/** do not apply any offset */
        NONE,
        /** determine the optimal offset based on relief data (default) */
        AUTO,
        /** user defined, fixed value > 0 */
        CUSTOM;

        /**
         * Parses the height offset string from the configuration
         * @param value the string to parse
         * @return the height offset type
         */
        public static HeightOffset parse(String value) {
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
    
    /**
     * The relief data source
     */
    public enum ReliefSource {
        /** no relief is created, just a flat map */
        none,
        /** use the SRTM dataset to render maps */
        srtm,
        /** mandelbrot-relief ;) */
        mandelbrot;
    }

}
