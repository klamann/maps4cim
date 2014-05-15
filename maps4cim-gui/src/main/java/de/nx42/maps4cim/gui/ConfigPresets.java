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
package de.nx42.maps4cim.gui;

import java.util.LinkedList;
import java.util.List;

import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.OsmFileDef;
import de.nx42.maps4cim.config.texture.TextureDef;
import de.nx42.maps4cim.config.texture.TextureDef.TextureDefNone;
import de.nx42.maps4cim.config.texture.osm.EntityDef;
import de.nx42.maps4cim.config.texture.osm.NodeDef;
import de.nx42.maps4cim.config.texture.osm.PolygonDef;
import de.nx42.maps4cim.config.texture.osm.WayDef;
import de.nx42.maps4cim.gui.MainWindow.TextureDetail;

public class ConfigPresets {

    public static TextureDef get(TextureDetail td) {
        return get(td, new OsmDef());
    }
    
	public static TextureDef get(TextureDetail td, OsmDef def) {
		switch (td) {
			case off:   return new TextureDefNone();
			case min:   return getTextureConfig(getLimitedColors(), getEntitiesLowest(), def);
			case low:   return getTextureConfig(getLimitedColors(), getEntitiesLow(),    def);
			case med:   return getTextureConfig(getLimitedColors(), getEntitiesMed(),    def);
			case high:  return getTextureConfig(getAllColors(),     getEntitiesHigh(),   def);
			case ultra: return getTextureConfig(getAllColors(),     getEntitiesUltra(),  def);
			default:    return new TextureDefNone();
		}
	}

	public static TextureDef get(final TextureDetail td, final String osmXmlFile) {
	    TextureDef def = get(td, new OsmFileDef());

        if(def instanceof OsmFileDef) {
            // add path, if actually an osm file def
            OsmFileDef defO = (OsmFileDef) def;
            defO.osmXmlFilePath = osmXmlFile;
            return defO;
        } else {
            return def;
        }
    }

    protected static TextureDef getTextureConfig(List<ColorDef> colors,
            List<EntityDef> entities, OsmDef def) {
        def.colors = colors;
        def.entities = entities;
        return def;
    }
    
    // ----- colors -----

    /**
     * Colors used in the medium preset or lower
     * @return
     */
    protected static List<ColorDef> getLimitedColors() {
        List<ColorDef> colors = new LinkedList<ColorDef>();

        colors.add(new ColorDef() {{ name="wood"; roughGrass=0.4; }});
        colors.add(new ColorDef() {{ name="farm"; roughGrass=0.15; dirt=0.3; }});
        colors.add(new ColorDef() {{ name="town"; dirt=0.1; mud=0.1;  }});
        colors.add(new ColorDef() {{ name="water"; dirt=0.5;  }});
        colors.add(new ColorDef() {{ name="railway"; mud=0.8;  }});
        colors.add(new ColorDef() {{ name="road"; dirt=0.6; mud=0.4;  }});
        
        return colors;
    }

    /**
     * All colors that are used by the presets in this class
     * @return
     */
	protected static List<ColorDef> getAllColors() {
		List<ColorDef> colors = new LinkedList<ColorDef>();

        colors.add(new ColorDef() {{ name="wood"; roughGrass=0.4; }});
        colors.add(new ColorDef() {{ name="sand"; dirt=0.6; mud=0.1; }});
        colors.add(new ColorDef() {{ name="green"; roughGrass=0.08;  }});
        colors.add(new ColorDef() {{ name="farm"; roughGrass=0.15; dirt=0.3; }});
        colors.add(new ColorDef() {{ name="gravel"; dirt=0.2; mud=0.3; roughGrass=0.3; }});
        colors.add(new ColorDef() {{ name="garden"; roughGrass=0.15; dirt=0.1; }});
        colors.add(new ColorDef() {{ name="leisure"; roughGrass=0.2; mud=0.1; }});
        colors.add(new ColorDef() {{ name="building"; mud=0.3; }});
        colors.add(new ColorDef() {{ name="road"; dirt=0.6; mud=0.4;  }});
        colors.add(new ColorDef() {{ name="town"; dirt=0.1; mud=0.1;  }});
        colors.add(new ColorDef() {{ name="military"; dirt=0.2; mud=0.1; roughGrass=0.2; }});
        colors.add(new ColorDef() {{ name="water"; dirt=0.5;  }});
        colors.add(new ColorDef() {{ name="railway"; mud=0.8;  }});
        colors.add(new ColorDef() {{ name="aerialway"; dirt=0.4; mud=0.25; }});
        colors.add(new ColorDef() {{ name="aeroway"; dirt=0.5; mud=0.3; }});
        colors.add(new ColorDef() {{ name="aeroway2"; dirt=0.5; mud=0.1; roughGrass=0.2; }});
        colors.add(new ColorDef() {{ name="barrier"; mud=0.8; }});
        colors.add(new ColorDef() {{ name="oneway"; roughGrass=0.5; mud=0.4;  }});
        colors.add(new ColorDef() {{ name="bridge"; roughGrass=0.8; }});

        return colors;
	}

	// ----- entities -----
	
	/**
	 * Most basic dataset: Only railways and large roads
	 */
	protected static List<EntityDef> getEntitiesLowest() {
        List<EntityDef> entities = new LinkedList<EntityDef>();

        entities.add(new WayDef() {{ key="railway"; value="rail"; color="railway"; strokeWidth=0.6; }});
        entities.add(new WayDef() {{ key="highway"; value="primary"; color="road"; strokeWidth=1.0; }});

        entities.add(new WayDef() {{ key="highway"; value="trunk"; color="road"; strokeWidth=1.3; }});
        entities.add(new WayDef() {{ key="highway"; value="motorway"; color="road"; strokeWidth=1.5; }});

        return entities;
    }

	/**
	 * limited dataset: large rivers, water, railways and major roads
	 */
	protected static List<EntityDef> getEntitiesLow() {
        List<EntityDef> entities = new LinkedList<EntityDef>();

        // water
        entities.add(new PolygonDef() {{ key="waterway"; value="riverbank"; color="water"; }});
        entities.add(new PolygonDef() {{ key="natural"; value="water"; color="water"; }});

        // roads & rails
        entities.add(new WayDef() {{ key="railway"; value="rail"; color="railway"; strokeWidth=0.6; }});
        entities.add(new WayDef() {{ key="highway"; value="secondary"; color="road"; strokeWidth=0.8; }});
        entities.add(new WayDef() {{ key="highway"; value="primary"; color="road"; strokeWidth=1.0; }});

        entities.add(new WayDef() {{ key="highway"; rvalue="motorway_link|trunk_link"; color="road"; strokeWidth=0.8; }});
        entities.add(new WayDef() {{ key="highway"; value="trunk"; color="road"; strokeWidth=1.3; }});
        entities.add(new WayDef() {{ key="highway"; value="motorway"; color="road"; strokeWidth=1.5; }});

        return entities;
    }

	/**
	 * Medium quality dataset: Simple landuse, water, rivers, railways and
	 * roads except for residential or smaller
	 */
	protected static List<EntityDef> getEntitiesMed() {
		List<EntityDef> entities = new LinkedList<EntityDef>();

        // landuse
        entities.add(new PolygonDef() {{ key="landuse"; value="forest"; color="wood"; }});
        entities.add(new PolygonDef() {{ key="natural"; value="wood"; color="wood"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="farm|farmland"; color="farm"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="commercial|industrial|residential|retail"; color="town"; }});

        // water
        entities.add(new WayDef() {{ key="waterway"; color="water"; strokeWidth=1.0; }});
        entities.add(new PolygonDef() {{ key="waterway"; value="riverbank"; color="water"; }});
        entities.add(new PolygonDef() {{ key="natural"; value="water"; color="water"; }});

        // roads & rails
        entities.add(new WayDef() {{ key="railway"; value="rail"; color="railway"; strokeWidth=0.6; }});

        entities.add(new WayDef() {{ key="highway"; value="unclassified"; color="road"; strokeWidth=0.4; }});
        entities.add(new WayDef() {{ key="highway"; value="tertiary"; color="road"; strokeWidth=0.6; }});
        entities.add(new WayDef() {{ key="highway"; value="secondary"; color="road"; strokeWidth=0.9; }});
        entities.add(new WayDef() {{ key="highway"; value="primary"; color="road"; strokeWidth=1.2; }});

        entities.add(new WayDef() {{ key="highway"; rvalue="motorway_link|trunk_link"; color="road"; strokeWidth=1.0; }});
        entities.add(new WayDef() {{ key="highway"; value="trunk"; color="road"; strokeWidth=1.5; }});
        entities.add(new WayDef() {{ key="highway"; value="motorway"; color="road"; strokeWidth=1.8; }});

        return entities;
	}

	/**
	 * High quality preset: detailed landuse, waterways, landing strips,
	 * railways and roads including residential roads
	 */
	protected static List<EntityDef> getEntitiesHigh() {
		List<EntityDef> entities = new LinkedList<EntityDef>();

        // landuse
        entities.add(new PolygonDef() {{ key="landuse"; value="forest"; color="wood"; }});
        entities.add(new PolygonDef() {{ key="natural"; value="wood"; color="wood"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="farm|farmland"; color="farm"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="commercial|construction|industrial|residential|retail"; color="town"; }});

        // water
        entities.add(new WayDef() {{ key="waterway"; color="water"; strokeWidth=1.0; }});
        entities.add(new PolygonDef() {{ key="waterway"; value="riverbank"; color="water"; }});
        entities.add(new PolygonDef() {{ key="natural"; value="water"; color="water"; }});

        // special road-like entities
        entities.add(new PolygonDef() {{ key="aeroway"; color="aeroway"; }});
        entities.add(new WayDef() {{ key="aeroway"; color="aeroway"; strokeWidth=3.0; }});

        // roads & rails
        entities.add(new WayDef() {{ key="highway"; value="residential"; color="road"; strokeWidth=0.8; }});

        entities.add(new WayDef() {{ key="railway"; value="rail"; color="railway"; strokeWidth=0.8; }});

        entities.add(new WayDef() {{ key="highway"; value="unclassified"; color="road"; strokeWidth=1.0; }});
        entities.add(new WayDef() {{ key="highway"; rvalue="_link"; color="road"; strokeWidth=0.9; }});
        entities.add(new WayDef() {{ key="highway"; value="tertiary"; color="road"; strokeWidth=1.2; }});
        entities.add(new WayDef() {{ key="highway"; value="secondary"; color="road"; strokeWidth=1.6; }});
        entities.add(new WayDef() {{ key="highway"; value="primary"; color="road"; strokeWidth=2.0; }});

        entities.add(new WayDef() {{ key="highway"; rvalue="motorway_link|trunk_link"; color="road"; strokeWidth=1.3; }});
        entities.add(new WayDef() {{ key="highway"; value="trunk"; color="road"; strokeWidth=2.5; }});
        entities.add(new WayDef() {{ key="highway"; value="motorway"; color="road"; strokeWidth=3.0; }});

        // modificators (additional line in different color)
        entities.add(new WayDef() {{ key="bridge"; value="yes"; color="bridge"; strokeWidth=0.4; }});
        entities.add(new WayDef() {{ key="oneway"; value="yes"; color="oneway"; strokeWidth=0.2; }});

        return entities;
	}

    /*
     * available colors
     *
     * nature
     * - wood
     * - sand
     * - farm
     * - green
     * - gravel
     * - water
     *
     * town
     * - garden
     * - town
     * - military
     * - leisure
     * - building
     *
     * traffic
     * - aerialway
     * - aeroway (for regular ground structures)
     * - aeroway2 (for terminals and the like)
     * - barrier
     * - road
     * - oneway
     * - bridge
     *
     */

	/**
	 * Preset with the highest available detail:
	 * - landuse: forest, beach, farmland, grass, commercial, residential, military, ...
	 * - water: rivers, canals, lakes, pools
	 * - building shapes
	 * - railways
	 * - all roads including pedestrian ways, tracks and service roads
	 * - road barriers and bridges
	 * - landing strips and aerial passenger lines
	 */
	protected static List<EntityDef> getEntitiesUltra() {
        List<EntityDef> entities = new LinkedList<EntityDef>();

        // landuse
        entities.add(new PolygonDef() {{ key="landuse"; value="forest"; color="wood"; }});
        entities.add(new PolygonDef() {{ key="natural"; value="wood"; color="wood"; }});
        entities.add(new PolygonDef() {{ key="natural"; rvalue="sand|beach|coastline"; color="sand"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="farm|farmland|orchard|vineyard"; color="farm"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="grass|meadow"; color="green"; }});
        entities.add(new PolygonDef() {{ key="landuse"; value="quarry"; color="gravel"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="commercial|construction|industrial|residential|retail"; color="town"; }});
        entities.add(new PolygonDef() {{ key="landuse"; rvalue="allotments|cemetery|village_green|recreation_ground"; color="garden"; }});
        entities.add(new PolygonDef() {{ key="landuse"; value="military"; color="military"; }});
        entities.add(new PolygonDef() {{ key="leisure"; color="leisure"; }});

        // water
        entities.add(new WayDef() {{ key="waterway"; color="water"; strokeWidth=0.5; }});
        entities.add(new WayDef() {{ key="waterway"; rvalue="river|canal"; color="water"; strokeWidth=1.5; }});
        entities.add(new PolygonDef() {{ key="waterway"; value="riverbank"; color="water"; }});
        entities.add(new PolygonDef() {{ key="natural"; value="water"; color="water"; }});
        entities.add(new PolygonDef() {{ key="landuse"; value="reservoir"; color="water"; }});
        entities.add(new PolygonDef() {{ key="leisure"; rvalue="fishing|swimming_pool"; color="water"; }});

        // buildings
        entities.add(new PolygonDef() {{ key="building"; color="building"; }});

        // barriers
        entities.add(new PolygonDef() {{ key="barrier"; rvalue="ditch"; color="sand"; }});
        entities.add(new WayDef() {{ key="barrier"; rvalue="city_wall|ditch|retaining_wall"; color="barrier"; strokeWidth=0.5; }});

        // special road-like entities
        entities.add(new WayDef() {{ key="aerialway"; color="aerialway"; strokeWidth=0.5; }});
        entities.add(new PolygonDef() {{ key="aeroway"; rvalue="apron|helipad|runway"; color="aeroway"; }});
        entities.add(new PolygonDef() {{ key="aeroway"; rvalue="hangar|terminal"; color="aeroway2"; }});
        entities.add(new WayDef() {{ key="aeroway"; color="aeroway"; strokeWidth=3.0; }});

        // roads & rails
        entities.add(new WayDef() {{ key="highway"; color="road"; strokeWidth=0.3; }});

        entities.add(new WayDef() {{ key="highway"; value="track"; color="road"; strokeWidth=0.45; }});
        entities.add(new WayDef() {{ key="highway"; value="service"; color="road"; strokeWidth=0.6; }});
        entities.add(new WayDef() {{ key="highway"; rvalue="living_street|pedestrian"; color="road"; strokeWidth=0.65; }});
        entities.add(new WayDef() {{ key="highway"; value="residential"; color="road"; strokeWidth=0.8; }});

        entities.add(new WayDef() {{ key="railway"; color="railway"; strokeWidth=0.2; }});
        entities.add(new WayDef() {{ key="railway"; value="rail"; color="railway"; strokeWidth=0.8; }});

        entities.add(new WayDef() {{ key="highway"; value="unclassified"; color="road"; strokeWidth=1.0; }});
        entities.add(new WayDef() {{ key="highway"; rvalue="_link"; color="road"; strokeWidth=0.9; }});
        entities.add(new WayDef() {{ key="highway"; value="tertiary"; color="road"; strokeWidth=1.2; }});
        entities.add(new WayDef() {{ key="highway"; value="secondary"; color="road"; strokeWidth=1.6; }});
        entities.add(new WayDef() {{ key="highway"; value="primary"; color="road"; strokeWidth=2.0; }});

        entities.add(new WayDef() {{ key="highway"; rvalue="motorway_link|trunk_link"; color="road"; strokeWidth=1.3; }});
        entities.add(new WayDef() {{ key="highway"; value="trunk"; color="road"; strokeWidth=2.5; }});
        entities.add(new WayDef() {{ key="highway"; value="motorway"; color="road"; strokeWidth=3.0; }});

        // modificators (additional line in different color)
        entities.add(new NodeDef() {{ key="barrier"; rvalue="block|bollard|lift_gate"; color="barrier"; radius=1.0; }});
        entities.add(new WayDef() {{ key="bridge"; value="yes"; color="bridge"; strokeWidth=0.6; }});
        entities.add(new WayDef() {{ key="oneway"; value="yes"; color="oneway"; strokeWidth=0.5; }});

        return entities;
    }

}
