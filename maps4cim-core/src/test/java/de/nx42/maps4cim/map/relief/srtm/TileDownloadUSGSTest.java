package de.nx42.maps4cim.map.relief.srtm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.map.relief.srtm.TileDownload.CoordinateInt;
import de.nx42.maps4cim.map.relief.srtm.TileDownloadUSGS.DownloadURL;
import de.nx42.maps4cim.util.gis.Area;
import de.nx42.maps4cim.util.gis.Coordinate;
import de.nx42.maps4cim.util.gis.UnitOfLength;

public class TileDownloadUSGSTest {

    // The following tests depend on the availablitity of external servers
    // and disk write permissions, disabled by default...

    // @Test
    public void testGetTileIntInt() throws Exception {
        TileDownload td = new TileDownloadUSGS();
        File f = td.getTile(47, 11);
        Cache c = new Cache();

        assertTrue(c.has(f.getName()));
        assertEquals(new File(Cache.cacheDir, "N47E011.hgt.zip").toString(), f.toString());
    }

    // @Test
    public void testGetTiles() throws Exception {
        Area ar = new Area(new Coordinate(48.4, 11.7), 8, UnitOfLength.KILOMETER);
        Cache c = new Cache();
        File[][] expected = new File[][]{ { c.getUnchecked("N48E011.hgt.zip")  } };

        runTestGetTiles(ar, expected);
    }

    // @Test
    public void testGetTiles2() throws Exception {
        Area ar = new Area(new Coordinate(48.4, 12.0), 8, UnitOfLength.KILOMETER);
        Cache c = new Cache();
        File[][] expected = new File[][] {
            { c.getUnchecked("N48E011.hgt.zip"), c.getUnchecked("N48E012.hgt.zip") }
        };

        runTestGetTiles(ar, expected);
    }

    // @Test
    public void testGetTiles3() throws Exception {
        Area ar = new Area(new Coordinate(48.0, 12.0), 8, UnitOfLength.KILOMETER);
        Cache c = new Cache();
        File[][] expected = new File[][] {
            { c.getUnchecked("N47E011.hgt.zip"), c.getUnchecked("N47E012.hgt.zip") },
            { c.getUnchecked("N48E011.hgt.zip"), c.getUnchecked("N48E012.hgt.zip") }
        };

        runTestGetTiles(ar, expected);
    }
    
    // helper functions

    protected static void runTestGetTiles(Area ar, File[][] expected) {
        System.out.println("tile download test for area " + ar.toString());

        try {
            TileDownload td = new TileDownloadUSGS();
            File[][] actual = td.getTiles(ar);
            assertArray2dEquals(expected, actual);
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }

    public static void assertArray2dEquals(Object[][] expected, Object[][] actual) {
        if (expected.length != actual.length || expected[0].length != actual[0].length) {
            fail("Arrays are of different size!");
        }
        for (int i = 0; i < actual.length; i++) {
            assertArrayEquals(expected[i], actual[i]);
        }
    }

}
