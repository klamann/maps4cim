package de.nx42.maps4cim.update;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import de.nx42.maps4cim.TestHelper;
import de.nx42.maps4cim.update.Update.Branch;
import de.nx42.maps4cim.util.DateUtils;
import de.nx42.maps4cim.util.Serializer;

public class UpdateTest {

    protected static final File schema = new File(TestHelper.getTestFolder(), "update-schema.xml");
    protected static final File serialize = new File(TestHelper.getTestFolder(), "update-serialize.xml");

    @Test
    public void schemaTest() {
        try {
            Serializer.generateSchema(schema, Update.class);
        } catch (Exception e) {
            fail("generating schema failed: " + e);
        }
    }

    @Test
    public void serializeTest() {
        try {
            Serializer.serialize(Update.class, generateUpdate(), serialize);
        } catch (JAXBException e) {
            fail("serialization failed: " + e);
        }
    }

    @Test
    public void deserializeTest() {
        try {
            Update u = Serializer.deserialize(Update.class, serialize, schema);
            assertNotNull(u.getStable().version);
        } catch (Exception e) {
            fail("deserialization failed: " + e);
        }
    }

    @Test
    public void deserializeTest2() {
        try {
            Update u = Serializer.deserialize(Update.class, serialize, true);
            assertNotNull(u.getStable().version);
        } catch (Exception e) {
            fail("deserialization failed: " + e);
        }
    }

    // use this function to generate a clean update.xml
//    @Test
//    public void printUpdateXML() {
//        try {
//            String updateXml = Serializer.serializeToString(Update.class, generateUpdate());
//            System.out.println(updateXml);
//        } catch (Exception e) {
//            fail("serialization failed: " + e);
//        }
//    }

    /**
     * @return generates the currently used update.xml
     */
    public static Update generateUpdate() {
        return new Update() {{
            releases = Arrays.asList(new Release[]{
                new Release() {{
                    branch = Branch.stable.name();
                    version = "1.0.0";
                    releaseDate = DateUtils.getDateUTC(2014, 4, 14, 12, 00, 00);
                    description = "plz update, there are new features and bugfixes and stuff!";
                    downloadUrl = "http://www.cimexchange.com/files/file/694-maps4cim/";
                    infoUrl = "https://nx42.de/projects/maps4cim/";
                }},
                new Release() {{
                    branch = Branch.testing.name();
                    version = "1.1.0-alpha-20140414";
                    releaseDate = DateUtils.getDateUTC(2014, 4, 14, 12, 00, 00);
                    description = "new test version, check it out!";
                    downloadUrl = "https://nx42.de/projects/maps4cim/";
                    infoUrl = "https://nx42.de/projects/maps4cim/";
                }}
            });
            notifications = Arrays.asList(new Notification[]{
                    new Notification() {{
                        id = 1;
                        date = DateUtils.getDateUTC(2014, 4, 14, 12, 00, 00);
                        heading = "Info";
                        content = "Hey, Y U No popups? :P";
                    }}
            });
            lastUpdate = DateUtils.getDateUTC(2014, 4, 14, 12, 00, 00);
        }};
    }

}
