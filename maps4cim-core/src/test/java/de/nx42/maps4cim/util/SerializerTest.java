package de.nx42.maps4cim.util;


import static org.junit.Assert.fail;

import java.io.File;

import javax.xml.bind.UnmarshalException;
import javax.xml.validation.Schema;

import org.junit.Test;
import org.xml.sax.SAXParseException;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.util.Serializer;

public class SerializerTest {

    @Test
    public void testGenerateSchemaClass() {
        try {
            File xml = new File("target/classes/sample-config.xml");
            Schema s = Serializer.generateSchema(Config.class);
            Config c = Serializer.deserialize(Config.class, xml, s);
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
