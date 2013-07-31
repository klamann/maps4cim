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
package de.nx42.maps4cim.config.texture;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * This is an abstract definition of the objects that shall be rendered.
 * That could be roads, buildings, landscapes, rivers, ...
 *
 * The data for these objects comes from the OpenStreetMap, therefore
 * the keys and values used to retrieve this data resemble the tags used
 * in the OpenStreetMap dataset.
 *
 * Each entity needs at least a key and a color. The key determines the objects
 * to draw (which can be further specialized by defining a set of allowed
 * values for this key) and the color defines the name of a color definition
 * (see {@link ColorDef}).
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlTransient
@XmlType(propOrder = { "key", "value", "rvalue", "color" })
public abstract class EntityDef {

	/**
	 * The key of the object to retrieve. This key resembles a tag from the
	 * OpenStreetMap. For a list of valid tags, see
	 * {@link http://wiki.openstreetmap.org/wiki/Map_Features}.
	 * Only exact name matches will be used.
	 */
    @XmlAttribute(name = "key", required = true)
    public String key;

    /**
     * The allowed value to the previously defined {@link EntityDef#key}.
     * Only exact name matches will be used.
     * A value set to "*" will be treated same as no value at all -> any
     * value is allowed (wildcard)
     */
    @XmlAttribute(name = "value")
    public String value;

    /**
     * The allowed value to the previously defined {@link EntityDef#key}.
     * This value will be parsed as regex. Note that any match will cause
     * the entity to be accepted, so if a simple string is specified here,
     * it will match any value that contains this string.
     * Use "^Foo$" to find only exact matches of "Foo".
     */
    @XmlAttribute(name = "rvalue")
    public String rvalue;

    /**
     * The name of the color to use. The color is defined separately in the
     * {@link ColorDef}.
     */
    @XmlAttribute(name = "color", required = true)
    public String color;


    /**
     * @return a detailed set of default entities, best not to use for large maps
     */
    public static List<EntityDef> getDefaults() {
        List<EntityDef> entities = new LinkedList<EntityDef>();

        // landuse
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="forest|wood"; color="wood"; }});
        entities.add(new PolygonDef() {{ key="natural"; rvalue="forest|wood"; color="wood"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="farm|farmland|orchard|vineyard"; color="farm"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="grass|meadow"; color="green"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="commercial|construction|industrial|residential|retail"; color="town"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="allotments|cemetery|village_green"; color="garden"; }});
        entities.add(new PolygonDef() {{ key="leisure"; value="*"; color="leisure"; }});

        // water
        entities.add(new WayDef() {{ key="waterway"; color="water"; strokeWidth=1.0; }});
        entities.add(new PolygonDef() {{ key="waterway"; value="riverbank"; color="water"; }});
        entities.add(new PolygonDef() {{ key="natural"; value="water"; color="water"; }});

        // buildings
        entities.add(new PolygonDef() {{ key="building"; color="building"; }});

        // roads
        entities.add(new WayDef() {{ key="highway"; color="road"; strokeWidth=0.3; }});

        entities.add(new WayDef() {{ key="highway"; value="track"; color="road"; strokeWidth=0.45; }});
        entities.add(new WayDef() {{ key="highway"; value="service"; color="road"; strokeWidth=0.6; }});
        entities.add(new WayDef() {{ key="highway"; rvalue="living_street|pedestrian"; color="road"; strokeWidth=0.65; }});
        entities.add(new WayDef() {{ key="highway"; value="residential"; color="road"; strokeWidth=0.8; }});

        entities.add(new WayDef() {{ key="railway"; color="railway"; strokeWidth=0.9; }});

        entities.add(new WayDef() {{ key="highway"; value="unclassified"; color="road"; strokeWidth=1.0; }});
        entities.add(new WayDef() {{ key="highway"; rvalue="_link"; color="road"; strokeWidth=0.9; }});
        entities.add(new WayDef() {{ key="highway"; value="tertiary"; color="road"; strokeWidth=1.2; }});
        entities.add(new WayDef() {{ key="highway"; value="secondary"; color="road"; strokeWidth=1.6; }});
        entities.add(new WayDef() {{ key="highway"; value="primary"; color="road"; strokeWidth=2.0; }});

        entities.add(new WayDef() {{ key="highway"; rvalue="motorway_link|trunk_link"; color="road"; strokeWidth=1.5; }});
        entities.add(new WayDef() {{ key="highway"; value="trunk"; color="road"; strokeWidth=2.5; }});
        entities.add(new WayDef() {{ key="highway"; value="motorway"; color="road"; strokeWidth=3.0; }});

        return entities;
    }

    /**
     * @return the Entity-Type (node or way)
     */
    public abstract String getType();

    /**
     * True, if there is no restriction concerning the value of this field,
     * so only the key needs to match
     * @return true, iff any value is allowed
     */
    public boolean allowsAnyValue() {
    	if(value != null) {
    		return value.trim().equals("*");
    	} else {
    		return rvalue == null;
    	}
    }

    /**
     * @return true, if this entity has an exact value matcher
     */
    public boolean hasExactValue() {
    	return value != null;
    }

    /**
     * @return true, if this entity has a regex value matcher
     */
    public boolean hasRegexValue() {
    	return rvalue != null;
    }

    /**
     * @return the value of the exact match or regex or null, if none applicable
     */
    public String getValue() {
    	if(value != null) {
    		return value;
    	} else {
    		return rvalue;	// can be null, this is intended behaviour
    	}
    }

    @Override
    public String toString() {
    	if(value == null && rvalue == null) {
    		return String.format("Entity with [\"%s\"] (any value)", key);
    	} else if(rvalue == null) {
    		return String.format("Entity with [\"%s\"=\"%s\"] (exact match)", key, value);
    	} else {
    		return String.format("Entity with [\"%s\"~\"%s\"] (regex match)", key, value);
    	}
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((rvalue == null) ? 0 : rvalue.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

}
