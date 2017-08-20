package de.nx42.maps4cim.map.relief.srtm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Test;

import de.nx42.maps4cim.map.relief.srtm.TileDownload.CoordinateInt;
import de.nx42.maps4cim.util.gis.Area;

public class TileDownloadTest {

	@Test
	public void testGetNumAfterIndex() throws Exception {
		String input = "N47E011.hgt.zip";
		try {
			int n = TileDownload.getNumAfterIndex(input, 1);
			int e = TileDownload.getNumAfterIndex(input, 4);
			assertEquals(47, n);
			assertEquals(11, e);
		} catch(NumberFormatException ex) {
			fail(ex.getMessage());
		} catch(ParseException ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	public void testParseCoordinate() throws Exception {
		helpTestParseCoordinate("N47E011.hgt.zip", 47, 11);
	}

	@Test
	public void testParseCoordinate2() throws Exception {
		helpTestParseCoordinate("S25E022.hgt.zip", -25, 22);
	}

	@Test
	public void testParseCoordinate3() throws Exception {
		helpTestParseCoordinate("S11W162.hgt.zip", -11, -162);
	}

	@Test
	public void testParseCoordinate4() throws Exception {
		helpTestParseCoordinate("N16W093.hgt.zip", 16, -93);
	}

	protected static void helpTestParseCoordinate(String parse, int expLat, int expLon) {
		try {
			CoordinateInt sc = TileDownload.parseCoordinate(parse);
			assertEquals(expLat, sc.lat);
			assertEquals(expLon, sc.lon);
		} catch (ParseException e) {
			fail(String.format("Error parsing file \"%s\": %s", parse, e.getMessage()));
		}

	}

    @Test
    public void testGetCoordinates() throws Exception {
        Area ar = new Area(48,11,50,13);
        CoordinateInt[][] coords = TileDownload.getCoordinates(ar);
        assertEquals(2, coords.length);
        assertEquals(2, coords[0].length);
        assertEquals(new CoordinateInt(48,11), coords[0][0]);
        assertEquals(new CoordinateInt(48,12), coords[0][1]);
        assertEquals(new CoordinateInt(49,11), coords[1][0]);
        assertEquals(new CoordinateInt(49,12), coords[1][1]);
    }
    
    @Test
    public void testGetCoordinates2() throws Exception {
        Area ar = new Area(48,175,50,-178);
        CoordinateInt[][] coords = TileDownload.getCoordinates(ar);
        assertEquals(2, coords.length);
        assertEquals(7, coords[0].length);
        assertEquals(new CoordinateInt(48,175), coords[0][0]);
        assertEquals(new CoordinateInt(48,179), coords[0][4]);
        assertEquals(new CoordinateInt(48,-180), coords[0][5]);
        assertEquals(new CoordinateInt(48,-179), coords[0][6]);
        assertEquals(new CoordinateInt(49,175), coords[1][0]);
        assertEquals(new CoordinateInt(49,-179), coords[1][6]);
    }

}
