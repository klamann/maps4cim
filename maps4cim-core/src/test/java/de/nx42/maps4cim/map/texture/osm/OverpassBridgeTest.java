package de.nx42.maps4cim.map.texture.osm;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.bounds.BoundsWrapper;
import de.nx42.maps4cim.config.bounds.CenterDef;
import de.nx42.maps4cim.config.relief.ReliefWrapper;
import de.nx42.maps4cim.config.relief.SrtmDef;
import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.TextureWrapper;
import de.nx42.maps4cim.config.texture.osm.EntityDef;
import de.nx42.maps4cim.config.texture.osm.PolygonDef;
import de.nx42.maps4cim.config.texture.osm.WayDef;

public class OverpassBridgeTest {

	protected static final Config c = new Config() {{
	    bounds = new BoundsWrapper() {{
            value = new CenterDef() {{
                centerLat = 48.401;
                centerLon = 11.744;
                extent = 1.0;
                unit = Unit.KM;
            }};
        }};
        relief = new ReliefWrapper() {{
            value = new SrtmDef() {{
                heightScale = "1.5";
            }};
        }};
        texture = new TextureWrapper() {{
            value = new OsmDef() {{
                colors = ColorDef.getDefaults();
                entities = new ImmutableList.Builder<EntityDef>()
                        .add(new PolygonDef() {{ key="landuse"; rvalue="forest|wood"; color="wood"; }})
                        .add(new PolygonDef() {{ key="waterway"; value="riverbank"; color="water"; }})
                        .add(new WayDef() {{ key="bridge"; color="road"; strokeWidth=0.3; }})
                        .add(new WayDef() {{ key="highway"; rvalue="_link"; color="road"; strokeWidth=0.9; }})
                        .build();
            }};
        }};
    }};

	@Test
    	public void testBuildOverpassQuery() {
    		OverpassBridge ob = new OverpassBridge(c);
    
    		String expected = "(way[\"landuse\"~\"forest|wood\"](48.39650842,11.73723469,48.40549158,11.75076531);way[\"waterway\"=\"riverbank\"](48.39650842,11.73723469,48.40549158,11.75076531);way[\"bridge\"](48.39650842,11.73723469,48.40549158,11.75076531);way[\"highway\"~\"_link\"](48.39650842,11.73723469,48.40549158,11.75076531););(._;>;);out meta;";
    		String actual = ob.buildOverpassQuery();
    		assertEquals(expected, actual);
    	}

	@Test
    	public void testBuildSingleEntityQueryPart() {
    		OverpassBridge ob = new OverpassBridge(c);
    		EntityDef entity = ((OsmDef) c.getTextureTrans()).entities.get(0);
    		StringBuilder sb = new StringBuilder();
    		ob.buildSingleEntityQueryPart(entity, sb);
    
    		String expected = "way[\"landuse\"~\"forest|wood\"](48.39650842,11.73723469,48.40549158,11.75076531);";
    		String actual = sb.toString();
    		assertEquals(expected, actual);
    	}

	@Test
	public void testBuildOverpassQueryURL() {
		OverpassBridge ob = new OverpassBridge(c);

		try {
			String server = OverpassBridge.servers[0];
			String expected = server + URLEncoder.encode("(way[\"landuse\"~\"forest|wood\"](48.39650842,11.73723469,48.40549158,11.75076531);way[\"waterway\"=\"riverbank\"](48.39650842,11.73723469,48.40549158,11.75076531);way[\"bridge\"](48.39650842,11.73723469,48.40549158,11.75076531);way[\"highway\"~\"_link\"](48.39650842,11.73723469,48.40549158,11.75076531););(._;>;);out meta;", "UTF-8");
			String actual = ob.buildQueryURL(server).toString();
			assertEquals(expected, actual);
		} catch (UnsupportedEncodingException e) {
			fail("URL encoding failure!");
		}

	}

	@Test
	public void testEscapeOverpassQuery() throws Exception {
		String input = "test\tquery\\and\nescape'chars'";
		String expected = "test\\tquery\\\\and\\nescape\\'chars\\'";
		String actual = OverpassBridge.escapeOverpassQuery(input);

		assertEquals(expected, actual);
	}

}
