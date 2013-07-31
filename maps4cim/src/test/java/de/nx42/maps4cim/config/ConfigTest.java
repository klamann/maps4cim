package de.nx42.maps4cim.config;

import static org.junit.Assert.fail;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import de.nx42.maps4cim.TestHelper;
import de.nx42.maps4cim.config.ReliefDef.ReliefSource;
import de.nx42.maps4cim.config.TextureDef.TextureSource;
import de.nx42.maps4cim.config.bounds.CenterDef;
import de.nx42.maps4cim.config.bounds.CenterDef.Unit;
import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.EntityDef;
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
            cfg.texture.entities.toArray();
        } catch (Exception e) {
            fail("deserialization failed: " + e);
        }
    }

    @Test
    public void deserializeTest2() {
        try {
            Config cfg = Serializer.deserialize(Config.class, serialize, true);
            cfg.texture.entities.toArray();
        } catch (Exception e) {
            fail("deserialization failed: " + e);
        }
    }


    public static Config generateConfig() {
        return new Config() {{
            bounds = new CenterDef() {{
                centerLat = 48.401;
                centerLon = 11.738;
                extent = 8.0;
                unit = Unit.KM;
            }};
            relief = new ReliefDef() {{
                source = ReliefSource.srtm;
                heightScale = 1.5;
            }};
            texture = new TextureDef() {{
                source = TextureSource.osm;
                colors = ColorDef.getDefaults();
                entities = EntityDef.getDefaults();
            }};
        }};
    }
}
