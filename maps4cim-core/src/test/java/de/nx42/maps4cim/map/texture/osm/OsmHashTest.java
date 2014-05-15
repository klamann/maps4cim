package de.nx42.maps4cim.map.texture.osm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;

import org.junit.Test;

import de.nx42.maps4cim.util.gis.Area;

public class OsmHashTest {
    
    @Test
    public void testOsmHash() throws Exception {
        Area ar = new Area(48, 11, 49, 12);
        OsmHash h = new OsmHash(ar);
        
        String expected = "IcAInQB7wCHA-all";
        String actual = h.getQueryHash();
        assertEquals(expected, actual);
    }

    @Test
    public void testLocationHash() throws Exception {
        locationHashRoundtrip(new Area(48, 11, 49, 12));
        locationHashRoundtrip(new Area(48.1, 11.2, 49.3, 12.4));
        
        locationHashRoundtrip(new Area(170,60,171,61));
        locationHashRoundtrip(new Area(-130,50,-131,51));
        locationHashRoundtrip(new Area(-179.8,-89.8,-179.9,-89.9));
    }
    
    @Test
    public void testLocationHash2() throws Exception {
        
        locationHashTarget(
                new Area(48, 11, 49, 12),
                new Area(48, 11, 49, 12),
                true
         );
        
        locationHashTarget(
                new Area(48.1223, 11.2222, 49.3110, 12.4110),
                new Area(48.1224, 11.2223, 49.3110, 12.4111),
                true
         );
        
        locationHashTarget(
                new Area(48, 11, 49, 12),
                new Area(47, 11, 49, 12),
                false
         );
        
        locationHashTarget(
                new Area(48.122, 11.222, 49.311, 12.411),
                new Area(48.123, 11.222, 49.312, 12.412),
                false
         );
        
    }
    
    protected static void locationHashRoundtrip(Area ar) throws IOException {
        String hash = OsmHash.getQueryHashLocation(ar);
        Area parsed = OsmHash.parseLocationHash(hash);
        assertEquals(ar, parsed);
    }
    
    protected static void locationHashTarget(Area a1, Area a2, boolean same) throws IOException {
        String h1 = OsmHash.getQueryHashLocation(a1);
        String h2 = OsmHash.getQueryHashLocation(a2);
        if(same) {
            assertEquals(h1, h2);
        } else {
            assertNotEquals(h1, h2);
        }
    }
    
}