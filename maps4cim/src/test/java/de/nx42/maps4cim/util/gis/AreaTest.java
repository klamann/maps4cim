/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.nx42.maps4cim.util.gis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Test;

import de.nx42.maps4cim.util.gis.Area;
import de.nx42.maps4cim.util.gis.Coordinate;
import de.nx42.maps4cim.util.gis.UnitOfLength;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class AreaTest {


    protected static final double north = 48.0;
    protected static final double west = 10.0;
    protected static final Coordinate nw = new Coordinate(north, west);
    protected static final double south = 47.0;
    protected static final double east = 11.0;
    protected static final Coordinate se = new Coordinate(south, east);

    protected static final double precision = 0.0000001;



    @Test
    public void testConstructor1() {
        Area instance = new Area(south, west, north, east);
        testResult(instance);
    }

    @Test
    public void testConstructor2() {
        Area instance = new Area(nw, se);
        testResult(instance);
    }

    @Test
    public void testConstructorCenter1() {
        Coordinate center = new Coordinate(48.0, 10.0);
        Area result = new Area(center, 1.0);
        Area expResult = new Area(47.5, 9.5, 48.5, 10.5);
        assertEquals(expResult, result);
    }

    @Test
    public void testConstructorCenter2() {
        Coordinate center = new Coordinate(48.0, 10.0);
        Area result = new Area(center, 2.0, 1.0);
        Area expResult = new Area(47.0, 9.5, 49.0, 10.5);
        assertEquals(expResult, result);
    }

    @Test
    public void testConstructorCenter3() {
        Coordinate center = new Coordinate(48.0, 10.0);
        Area result = new Area(center, 100.0, UnitOfLength.KILOMETER);
        Area expResult = new Area(47.5508423579, 9.32874443674, 48.4491576420, 10.67125556325);
        assertEquals(expResult, result);
    }

    @Test
    public void testConstructorCenter4() {
        Coordinate center = new Coordinate(48.0, 10.0);
        Area result = new Area(center, 100.0, 65.0, UnitOfLength.KILOMETER);
        Area expResult = new Area(47.550842357935, 9.5636838838824, 48.449157642011, 10.4363161161166);
        assertEquals(expResult, result);
    }

    @Test
    public void testConstructorCenter5() {
        Coordinate center = new Coordinate(48.0, 10.0);
        Area result = new Area(center, 100000.0, UnitOfLength.METER);
        Area expResult = new Area(47.5508423579, 9.32874443674, 48.4491576420, 10.67125556325);
        assertEquals(expResult, result);
    }

    @Test
    public void testConstructor5() {
        Area instance = new Area(new double[]{south, west, north, east});
        testResult(instance);
    }

    protected void testResult(Area instance) {
        assertEquals(south, instance.getMinLat(), precision);
        assertEquals(west, instance.getMinLon(), precision);
        assertEquals(north, instance.getMaxLat(), precision);
        assertEquals(east, instance.getMaxLon(), precision);
        assertEquals(nw, instance.getBoundNW());
        assertEquals(se, instance.getBoundSE());
    }


    @Test
    public void testParseSimple() {
        String s = "47.0,10.0,48.0,11.0";
        compareParsed(s);
    }

    @Test
    public void testParseSimpleEvil() {
        String s = "    N47.0° , E 10.0, n 48.0000°, 11";
        compareParsed(s);
    }

    @Test
    public void testParseOsm() {
        String s = "minlon=10.0&minlat=47.0&maxlon=11.0&maxlat=48.0";
        compareParsed(s);
    }

    @Test
    public void testParseOsmEvil() {
        String s = "  minLon=10&minLat= 47.0 &  MAXlon= 11.0 & maxLAT= 48.00000 ";
        compareParsed(s);
    }

    @Test
    public void testParseCenter() {
        String s = "10.5,47.5,1.0";
        compareParsedCenter(s, new Area(nw, se));
    }

    @Test
    public void testParseCenter2() {
        String s = "11.5,47.5,1.0,2.0";
        compareParsedCenter(s, new Area(46.5, 11, 48.5, 12));
    }



    protected void compareParsed(String input) {
        try {
            Area expResult = new Area(nw, se);
            Area result = Area.parse(input);
            assertEquals(expResult, result);
        } catch (ParseException ex) {
            fail("Error while parsing: " + ex);
        }
    }

    protected void compareParsedCenter(String input, Area expResult) {
        try {
            Area result = Area.parseCenter(input);
            assertEquals(expResult, result);
        } catch (ParseException ex) {
            fail("Error while parsing: " + ex);
        }
    }


}
