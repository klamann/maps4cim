package de.nx42.maps4cim.map.relief.srtm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.Test;

import de.nx42.maps4cim.map.relief.srtm.TileDownload.SimpleCoord;

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
			SimpleCoord sc = TileDownload.parseCoordinate(parse);
			assertEquals(expLat, sc.lat);
			assertEquals(expLon, sc.lon);
		} catch (ParseException e) {
			fail(String.format("Error parsing file \"%s\": %s", parse, e.getMessage()));
		}

	}

	// The following tests depend on the availablitity of external servers
	// and disk write permissions, disabled by default...

//	@Test
//	public void testGetTileIntInt() throws Exception {
//		TileDownload td = new TileDownload();
//		File f = td.getTile(47, 11);
//		Cache c = new Cache();
//
//		assertTrue(c.has(f.getName()));
//		assertEquals(new File(Cache.cacheDir, "N47E011.hgt.zip").toString(), f.toString());
//	}

//	@Test
//	public void testGetTiles() throws Exception {
//		Area ar = new Area(new Coordinate(48.4, 11.7), 8, UnitOfLength.KILOMETER);
//		Cache c = new Cache();
//		File[][] expected = new File[][]{ { c.getUnchecked("N48E011.hgt.zip")  } };
//
//		runTestGetTiles(ar, expected);
//	}

//	@Test
//	public void testGetTiles2() throws Exception {
//		Area ar = new Area(new Coordinate(48.4, 12.0), 8, UnitOfLength.KILOMETER);
//		Cache c = new Cache();
//		File[][] expected = new File[][] {
//			{ c.getUnchecked("N48E011.hgt.zip"), c.getUnchecked("N48E012.hgt.zip") }
//		};
//
//		runTestGetTiles(ar, expected);
//	}

//	@Test
//	public void testGetTiles3() throws Exception {
//		Area ar = new Area(new Coordinate(48.0, 12.0), 8, UnitOfLength.KILOMETER);
//		Cache c = new Cache();
//		File[][] expected = new File[][] {
//			{ c.getUnchecked("N47E011.hgt.zip"), c.getUnchecked("N47E012.hgt.zip") },
//			{ c.getUnchecked("N48E011.hgt.zip"), c.getUnchecked("N48E012.hgt.zip") }
//		};
//
//		runTestGetTiles(ar, expected);
//	}

//	protected static void runTestGetTiles(Area ar, File[][] expected) {
//		System.out.println("tile download test for area " + ar.toString());
//
//		try {
//			TileDownload td = new TileDownload();
//			File[][] actual = td.getTiles(ar);
//			assertArray2dEquals(expected, actual);
//		} catch(Exception e) {
//			fail(e.getMessage());
//		}
//	}


	public static void assertArray2dEquals(Object[][] expected, Object[][] actual) {
		if (expected.length != actual.length || expected[0].length != actual[0].length) {
			fail("Arrays are of different size!");
		}
		for (int i = 0; i < actual.length; i++) {
			assertArrayEquals(expected[i], actual[i]);
		}
	}

	// the following tests can be used to update the srtm download mapping

//  @Test
//  public void testStoreMapping() {
//      try {
//
//          // store
//          File store = new File(TestHelper.getTestFolder(), "mapping.obj");
//          TileDownload.storeMapping(store);
//
//          // read
//          ObjectInputStream ois = new ObjectInputStream(
//                  new FileInputStream(store));
//          @SuppressWarnings("unchecked")
//          Table<Integer, Integer, DownloadURL> mapping =
//                          (Table<Integer, Integer, DownloadURL>) ois.readObject();
//          ois.close();
//
//          // assert
//          assertEquals(mapping.hashCode(), mapping.hashCode());
//          assertEquals(mapping.toString(), mapping.toString());
//          assertTrue(mapping.equals(mapping));
//
//      } catch(Exception e) {
//          fail(e.getMessage());
//      }
//  }
//
//    @Test
//    public void testGenerateMapping() {
//        try {
//            TileDownload.generateMapping();
//        } catch (Exception e) {
//          e.printStackTrace();
//            fail(e.getMessage());
//        }
//    }

}
