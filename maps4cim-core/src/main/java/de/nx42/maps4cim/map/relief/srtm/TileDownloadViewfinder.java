package de.nx42.maps4cim.map.relief.srtm;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Table;

public class TileDownloadViewfinder extends TileDownload {
    
    private static final Logger log = LoggerFactory.getLogger(TileDownloadViewfinder.class);
    
    protected static final String siteUrl = "http://www.viewfinderpanoramas.org";
    protected static final String packageIndex1 = siteUrl + "/dem1list.txt";
    protected static final String packageIndex3 = siteUrl + "/dem3list.txt";
    
    /**
     * Maps from <Lat,Lon> to the download URL relative to the site URL.
     * Nonexisting tiles are not contained in this data structure.
     */
    //protected final Table<Integer,Integer,String> downloadMapping;

    /*
     * Tile package URL retrieval
     * 
     * OK, this is kinda messed up, as there is no proper tile package index
     * available and the naming is rather inconsistent.
     * This however is not the case for the tiles themselves, these are named
     * consistently r"[n|s]\d\d[e|w]\d\d\d.hgt", e.g. "n51e002.hgt"
     * 
     * A possible solution would be parsing the available overview maps
     * - http://www.viewfinderpanoramas.org/Coverage%20map%20viewfinderpanoramas_org3.htm
     * - http://www.imagico.de/files/ferranti.php
     * - http://www.imagico.de/files/ferranti.php?list=dem1list.txt
     * now we could deduce the locations from the pixels of the overview maps
     * 
     * Better approach: read the package lists
     * - http://www.viewfinderpanoramas.org/dem3list.txt
     * - http://www.viewfinderpanoramas.org/dem1list.txt
     * 
     * These follow a specific pattern:
     * id:path[:info], e.g. U22:/dem3/U22.zip or AN2:/ANTDEM3/16-30.zip:-90:-90:-60:-0
     * 
     * id (tolowercase) can be:
     * - "[n|s]\d\d[e|w]\d\d\d", e.g. n47e006: parse directly
     * - "s?[a-z]\d\d", e.g. SM18: convert using algorithm below
     * - otherwise: use coordinates from comment, see algorithm below
     * 
     * short notation to available tiles:
     * west-east in steps of 6°:
     * 01 = 180..175°W, 02 = 174..169°W, 30 = 6..1°W, 31 = 0..5°E, 60 = 175..180°E
     * north-south in steps of 4°:
     * U = 80..83°N, A = 0..3°N, SA = 1..4°S, SN = 53..56°S
     * 
     * comment to coordinates (e.g. "63:-25:67:-13")
     * n63w25 to n66w14, last coordinates are exclusive!
     * 
     * downloaded files can contain multiple .hgt files in several subfolders
     * as well as unrelated files (readmes, etc.).
     * To get all relevant data, list all files and filter with
     * r"[n|s]\d\d[e|w]\d\d\d.hgt"
     * 
     */
    
    protected static Table<Integer,Integer,String> loadMapping() {
        /*
         * - look for index in cache
         * - download index if necessary
         * - parse 3° index, then substitute 1° index
         * 
         * issues:
         * how to separate and combine 1/3° files?
         */
        
        
        return null;
    }

    @Override
    public boolean exists(int lat, int lon) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public File getTile(int lat, int lon) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
