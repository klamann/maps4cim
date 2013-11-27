/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nx42.maps4cim.util.gis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Test;

import de.nx42.maps4cim.util.math.MathExt;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class CoordinateTest {

    // test values: N 47.421217°, E 10.986314°
    protected static final double latitude = 47.421217;
    protected static final double longitude = 10.986314;
    protected static final Coordinate instance = new Coordinate(latitude, longitude);

    @Test
    public void testGetLongitudeWGS84() {
        double expResult = longitude;
        double result = instance.getLongitude();
        assertEquals(expResult, result, 0.000001);
    }

    @Test
    public void testGetLatitudeWGS84() {
        double expResult = latitude;
        double result = instance.getLatitude();
        assertEquals(expResult, result, 0.000001);
    }

    @Test
    public void testToString() {
        String expResult = "47.4212°, 10.9863°";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    @Test
    public void testParse() {
        try {
            String s = "47.421217°, 10.986314°";
            Coordinate expResult = instance;
            Coordinate result = Coordinate.parse(s);
            assertEquals(expResult, result);
        } catch (ParseException ex) {
            fail("Error while parsing: " + ex);
        }
    }

    @Test
    public void testParseEvil() {
        try {
            String s = "    N47.4212170 ° , E 10.986314° ";
            Coordinate expResult = instance;
            Coordinate result = Coordinate.parse(s);
            assertEquals(expResult, result);
        } catch (ParseException ex) {
            fail("Error while parsing: " + ex);
        }
    }

    @Test
    public void testCleanAndParse() {
        String coord = "    N 47.4212170degrees ";
        double expResult = latitude;
        double result = MathExt.parseDoubleAggressive(coord);
        assertEquals(expResult, result, 0.000001);
    }
}