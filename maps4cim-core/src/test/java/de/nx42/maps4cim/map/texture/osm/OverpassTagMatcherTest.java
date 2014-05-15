package de.nx42.maps4cim.map.texture.osm;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import de.nx42.maps4cim.config.texture.osm.EntityDef;
import de.nx42.maps4cim.config.texture.osm.NodeDef;
import de.nx42.maps4cim.config.texture.osm.PolygonDef;
import de.nx42.maps4cim.config.texture.osm.WayDef;
import de.nx42.maps4cim.map.texture.osm.OverpassTagMatcher.Value;

public class OverpassTagMatcherTest {

	protected static final EntityDef defAny =
			new WayDef() {{ key="highway"; color="asphalt"; }};

	protected static final EntityDef defExact =
			new NodeDef() {{ key="amenity"; value="biergarten"; color="yellow"; }};

	protected static final EntityDef defPattern =
			new PolygonDef() {{ key="landuse"; rvalue="farm|forest"; color="green"; }};


	@Test
	public void testXapiTagFilterEntityDef() {
		OverpassTagMatcher otm = new OverpassTagMatcher(defAny);

		assertEquals(EntityType.Way, otm.type);
		assertEquals("highway", otm.key);
		assertEquals(new Value.Any(), otm.value);
		assertNull(otm.getValue());

		CommonEntityData data = buildEntityData("highway", "any");
		assertTrue(otm.matches(new Way(data)));
		assertFalse(otm.matches(new Node(data, 1, 1)));
		assertFalse(otm.matches(new Way(buildEntityData("amenity", "any"))));
	}

	@Test
	public void testXapiTagFilterEntityDef2() {
		OverpassTagMatcher otm = new OverpassTagMatcher(defExact);

		assertEquals(EntityType.Node, otm.type);
		assertEquals("amenity", otm.key);
		assertEquals(new Value.Exact("biergarten"), otm.value);
		assertEquals("biergarten", otm.getValue());

		CommonEntityData data = buildEntityData("amenity", "biergarten");
		assertTrue(otm.matches(new Node(data, 1, 1)));
		assertFalse(otm.matches(new Way(data)));
		assertFalse(otm.matches(new Way(buildEntityData("amenity", "not_a_biergarten"))));
	}

	@Test
	public void testXapiTagFilterEntityDef3() {
		OverpassTagMatcher otm = new OverpassTagMatcher(defPattern);

		assertEquals(EntityType.Way, otm.type);
		assertEquals("landuse", otm.key);
		assertEquals(new Value.Regex(Pattern.compile("farm|forest").matcher("")), otm.value);
		assertEquals("farm|forest", otm.getValue());

		assertTrue(otm.matches(new Way(buildEntityData("landuse", "farm"))));
		assertTrue(otm.matches(new Way(buildEntityData("landuse", "farmland"))));
		assertTrue(otm.matches(new Way(buildEntityData("landuse", "forest"))));
		assertTrue(otm.matches(new Way(buildEntityData("landuse", "deep_forest <- wtf?"))));
		assertTrue(otm.matches(new Way(buildEntityData("landuse", "farm|forest"))));

		assertFalse(otm.matches(new Way(buildEntityData("landuse", "any value"))));
		assertFalse(otm.matches(new Way(buildEntityData("landuse", "frest"))));
	}


	protected CommonEntityData buildEntityData(String key, String value) {
		List<Tag> tags = new ArrayList<Tag>(1);
		tags.add(new Tag(key, value));
		return new CommonEntityData(1, 2, new Date(), new OsmUser(3, "test"), 4, tags);
	}


}
