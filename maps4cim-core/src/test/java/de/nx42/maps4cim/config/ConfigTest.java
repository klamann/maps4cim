package de.nx42.maps4cim.config;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import de.nx42.maps4cim.TestHelper;
import de.nx42.maps4cim.config.bounds.BoundsWrapper;
import de.nx42.maps4cim.config.bounds.CenterDef;
import de.nx42.maps4cim.config.header.HeaderDef;
import de.nx42.maps4cim.config.header.HeaderDef.BuildingSet;
import de.nx42.maps4cim.config.relief.ReliefWrapper;
import de.nx42.maps4cim.config.relief.SrtmDef;
import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.TextureWrapper;
import de.nx42.maps4cim.config.texture.osm.EntityDef;
import de.nx42.maps4cim.util.DateUtils;
import de.nx42.maps4cim.util.Serializer;

public class ConfigTest {

	protected static final File schema = new File(TestHelper.getTestFolder(), "schema.xml");
	protected static final File serialize = new File(TestHelper.getTestFolder(), "serialize.xml");

    @Test
    public void schemaTest() {
        try {
            Serializer.generateSchema(schema, Config.class);
        } catch (Exception e) {
            fail("generating schema failed: " + e);
        }
    }

    @Test
    public void serializeTest() {
        try {
            Serializer.serialize(Config.class, generateConfig(), serialize);
        } catch (JAXBException e) {
            fail("serialization failed: " + e);
        }
    }

    @Test
    public void deserializeTest() {
        try {
            Config cfg = Serializer.deserialize(Config.class, serialize, schema);
            OsmDef def = (OsmDef) cfg.getTextureTrans();
            def.entities.toArray();
        } catch (Exception e) {
            fail("deserialization failed: " + e);
        }
    }

    @Test
    public void deserializeTest2() {
        try {
            Config cfg = Serializer.deserialize(Config.class, serialize, true);
            OsmDef def = (OsmDef) cfg.getTextureTrans();
            def.entities.toArray();
        } catch (Exception e) {
            fail("deserialization failed: " + e);
        }
    }


    public static Config generateConfig() {
        return new Config() {{
            bounds = new BoundsWrapper() {{
                value = new CenterDef() {{
                    centerLat = 48.401;
                    centerLon = 11.738;
                    extent = 8.0;
                    unit = Unit.KM;
                }};
            }};
            relief = new ReliefWrapper() {{
                value = new SrtmDef() {{
                    heightOffset = "auto";
                    heightScale = "0.5";
                }};
            }};
            texture = new TextureWrapper() {{
                value = new OsmDef() {{
                    colors = ColorDef.getDefaults();
                    entities = EntityDef.getDefaults();
                }};
            }};
            header = new HeaderDef() {{
                name = "mymap";
                created = DateUtils.getDateUTC(2013, 12, 6, 12, 0, 0);
                modified = new Date();
                buildingSet = BuildingSet.AMERICAN;
            }};
        }};
    }
}
