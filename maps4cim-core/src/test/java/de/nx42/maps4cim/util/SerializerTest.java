package de.nx42.maps4cim.util;


import static org.junit.Assert.fail;

import java.io.File;

import javax.xml.bind.UnmarshalException;
import javax.xml.validation.Schema;

import org.junit.Test;
import org.xml.sax.SAXParseException;

import de.nx42.maps4cim.config.Config;

public class SerializerTest {

    protected static final File sampleConfig = new File("target/classes/de/nx42/maps4cim/res/sample-config.xml");
    
    @Test
    public void testGenerateSchemaClass() {
        try {
            Schema s = Serializer.generateSchema(Config.class);
            Config c = Serializer.deserialize(Config.class, sampleConfig, s);
            c.toString();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGenerateSchemaClass2() {
        try {
            File xml = new File("target/test-classes/faulty-config.xml");
            Schema s = Serializer.generateSchema(Config.class);
            Serializer.deserialize(Config.class, xml, s);
        } catch (UnmarshalException e) {
            // well-formed but invalid xml shall throw SAXParseException (wrapped
            // in an UnmarshalException), and nothing else!
            Throwable linked = e.getLinkedException();
            if(!(linked instanceof SAXParseException)) {
                fail("Unexpected exception: " + e.getMessage());
            }
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

}
