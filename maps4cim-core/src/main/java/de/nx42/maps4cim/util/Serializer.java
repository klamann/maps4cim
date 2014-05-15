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
package de.nx42.maps4cim.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * Serializes java objects and deserializes XML documents using the JAXB library
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Serializer {

    protected final static SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    // -------------------- Serialize java objects --------------------

    /**
     * Serialize an object to XML using JAXB
     * @param clazz the class of the object to serialize (wtf is wrong with reflection?!)
     * @param instance the actual object instance to serialize
     * @param schemaLocation schemalocation tag that is added to the xml
     * @param output the file to write the xml into
     * @throws JAXBException when serialization fails
     */
    @SuppressWarnings("rawtypes")
	public static void serialize(Class clazz, Object instance,
            String schemaLocation, File output) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(clazz);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
        m.marshal(instance, output);
    }

    /**
     * Serialize an object to XML using JAXB, writing it to the desired file
     * @param clazz the class of the object to serialize (wtf is wrong with reflection?!)
     * @param instance the actual object instance to serialize
     * @param output the file to write the xml into
     * @throws JAXBException when serialization fails
     */
    @SuppressWarnings("rawtypes")
	public static void serialize(Class clazz, Object instance, File output)
            throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(clazz);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(instance, output);

    }

    /**
     * Serialize an object to XML using JAXB, returning the serialized object
     * as String
     * @param clazz the class of the object to serialize (wtf is wrong with reflection?!)
     * @param instance the actual object instance to serialize
     * @return the serialized XML as String
     * @throws JAXBException when serialization fails
     * @throws IOException when the resulting String cannot be written
     */
    @SuppressWarnings("rawtypes")
    public static String serializeToString(Class clazz, Object instance)
            throws JAXBException, IOException {

        JAXBContext context = JAXBContext.newInstance(clazz);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter(2048);
        m.marshal(instance, sw);
        sw.flush();
        String res = sw.toString();
        sw.close();
        return res;
    }

    // -------------------- deserialize XML documents --------------------

    /**
     * Deserializes an XML document to a java object using JAXB
     * @param <S> the type of the java object to create
     * @param clazz the class of the object to create
     * @param xml the xml file to deserialize
     * @param schema the corresponding schema file, to check validity
     * @return the deserialized java object
     * @throws SAXException when the xml is not valid agains the schema
     * @throws JAXBException when deserialization fails
     */
    public static <S> S deserialize(Class<S> clazz, File xml, File schema)
            throws SAXException, JAXBException {

        Schema s = sf.newSchema(schema);
        return deserialize(clazz, xml, s);
    }

    /**
     * Deserializes an XML document to a java object using JAXB
     * @param <S> the type of the java object to create
     * @param clazz the class of the object to create
     * @param xml the xml file to deserialize
     * @param schema the corresponding schema file, to check validity
     * @return the deserialized java object
     * @throws SAXException when the xml is not valid agains the schema
     * @throws JAXBException when deserialization fails
     */
    public static <S> S deserialize(Class<S> clazz, File xml, URL schema)
            throws SAXException, JAXBException {

        Schema s = sf.newSchema(schema);
        return deserialize(clazz, xml, s);
    }

    /**
     * Deserializes an XML document to a java object using JAXB
     * @param <S> the type of the java object to create
     * @param clazz the class of the object to create
     * @param xml the xml file to deserialize
     * @param schema the corresponding schema file as Schema object
     * @return the deserialized java object
     * @throws JAXBException when deserialization fails
     */
    @SuppressWarnings("unchecked")
    public static <S> S deserialize(Class<S> clazz, File xml, Schema schema) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);
        S deserialized = (S) unmarshaller.unmarshal(xml);
        return deserialized;
    }

    /**
     * Deserializes an XML document to a java object using JAXB and validates
     * it against a schema generated on the fly from the current implementation
     * of the specified class
     * @param <S> the type of the java object to create
     * @param clazz the class of the object to create
     * @param xml the xml file to deserialize
     * @return the deserialized java object
     * @throws JAXBException when deserialization fails
     * @throws IOException when the XML file can't be accessed
     * @throws SAXException when the XML can't be parsed
     */
    public static <S> S deserialize(Class<S> clazz, File xml) throws JAXBException, IOException, SAXException {
        return deserialize(clazz, xml, true);
    }

    /**
     * Deserializes an XML document to a java object using JAXB.
     * Warning: Using this method without specifying a schema is insecure!
     * Invalid xml documents can generate corrupt java objects.
     * @param <S> the type of the java object to create
     * @param clazz the class of the object to create
     * @param xml the xml file to deserialize
     * @param validate when set to true, validates the XML document against a
     * schema generated from the specified java class
     * @return the deserialized java object
     * @throws JAXBException when deserialization fails
     * @throws IOException when the XML file can't be accessed
     * @throws SAXException when the XML can't be parsed
     */
    @SuppressWarnings("unchecked")
	public static <S> S deserialize(Class<S> clazz, File xml, boolean validate) throws JAXBException, IOException, SAXException {
        if(validate) {
            Schema schema = generateSchema(clazz);
            return deserialize(clazz, xml, schema);
        } else {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            S deserialized = (S) unmarshaller.unmarshal(xml);
            return deserialized;
        }
    }

    /**
     * Deserializes an XML document to a java object using JAXB.
     * Warning: Using this method without specifying a schema is insecure!
     * Invalid xml documents can generate corrupt java objects.
     * @param <S> the type of the java object to create
     * @param clazz the class of the object to create
     * @param xml the xml string to deserialize
     * @return the deserialized java object
     * @throws JAXBException when deserialization fails
     */
    @SuppressWarnings("unchecked")
	public static <S> S deserializeFromString(Class<S> clazz, String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader read = new StringReader(xml);
        S deserialized = (S) unmarshaller.unmarshal(read);
        read.close();
        return deserialized;
    }

    // -------------------- XML schema --------------------

    /**
     * Generates a schema object for the specified class
     * @param clazz the class to generate the schema for
     * @return the generated schema object
     * @throws JAXBException
     * @throws IOException
     * @throws SAXException
     */
    @SuppressWarnings("rawtypes")
	public static Schema generateSchema(Class clazz) throws JAXBException, IOException, SAXException {
        // get context to write schema for
        JAXBContext context = JAXBContext.newInstance(clazz);

        // write schema in DOMResult using SchemaOutputResolver
        final List<DOMResult> results = new ArrayList<DOMResult>();
        context.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String ns, String file) throws IOException {
                DOMResult result = new DOMResult();
                result.setSystemId(file);
                results.add(result);
                return result;
            }
        });

        // transform domresult in source
        DOMResult res = results.get(0);
        DOMSource src = new DOMSource(res.getNode());

        // write schema object from source
        return sf.newSchema(src);
    }

    /**
     * Generates an xml schema for the specified class and writes it to file
     * @param dest the file where the schema shall be written to
     * @param clazz the class to generate the schema for
     * @throws JAXBException when the schema can't be created
     * @throws IOException when writing to file fails
     */
    @SuppressWarnings("rawtypes")
	public static void generateSchema(final File dest, Class clazz) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(clazz);
        context.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                return new StreamResult(dest);
            }
        });
    }

}
