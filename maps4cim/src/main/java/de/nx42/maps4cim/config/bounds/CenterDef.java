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
package de.nx42.maps4cim.config.bounds;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The map is defined by a single center coordinate and an extent with different
 * valid units allowed (degrees, meters, kilometers). For a valid definition,
 * at least a center and a single extent (which defaults to kilometers) is
 * required.
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlRootElement(name = "center")
public class CenterDef extends BoundsDef {

	/** the latitude of the center coordinate */
    @XmlAttribute(name = "lat", required = true)
    public Double centerLat;     // N-S

    /** the longitude of the center coordinate */
    @XmlAttribute(name = "lon", required = true)
    public Double centerLon;     // E-W

    /**
     * the extent in both directions. If unit is undefined, this value will be
     * interpreted as kilometers. This field is not marked as required, as it
     * can be replaced by extent-lat and extent-lon, but the program will fail,
     * if a sufficient definition is missing
     */
    @XmlAttribute(name = "extent")
    public Double extent;

    /** the extent on the North-South axis (latitude). Overrides extent */
    @XmlAttribute(name = "extent-lat")
    public Double extentLat;     // overrides extent

    /** the extent on the East-West axis (longitude). Overrides extent */
    @XmlAttribute(name = "extent-lon")
    public Double extentLon;     // overrides extent

    /** the unit of the extent. Defaults to kilometers */
    @XmlAttribute(name = "unit")
    public Unit unit;            // default: Unit.KM

    /**
     * Decides if the current instance contains enough values to return valid
     * results. This can not be solved by xml schema, as the values extent-*
     * can override the backup-value extent and therefore none of these
     * can be marked as required.
     * @return true, iff extent is defined or both extent-lat and extent-lon
     *         are defined
     */
    public boolean isValid() {
        return extent != null || (extentLat != null && extentLon != null);
    }

    /**
     * Creates a new CenterDef from the specified parameters
     * @param lat latitude of the center
     * @param lon longitude of the center
     * @param ext extent of the map (edge length)
     * @param unt the unit of the extent
     * @return a new CenterDef with the specified values
     */
	public static CenterDef of(final double lat, final double lon,
			final double ext, final Unit unt) {
		return new CenterDef() {{
				centerLat = lat;
				centerLon = lon;
				extent = ext;
				unit = unt;
		}};
	}

    /**
     * Valid Units that may be used in the configuration.
     */
    @XmlType
    @XmlEnum(String.class)
    public enum Unit {
    	/** decimal degree */
        @XmlEnumValue("deg") DEG,
        /** kilometer */
        @XmlEnumValue("km") KM,
        /** meter */
        @XmlEnumValue("m") M;
    }

}
