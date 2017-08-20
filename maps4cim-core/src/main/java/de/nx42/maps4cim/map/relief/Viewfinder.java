package de.nx42.maps4cim.map.relief;

/**
 * source: http://www.viewfinderpanoramas.org/dem3.html
 * 
 * 
 */
public class Viewfinder {

  /*
   * HGT File Format
   * (source: http://www.viewfinderpanoramas.org/dem3.html#hgt)
   * 
   * An HGT file covers an area of 1°x1°. Its south western corner can be
   * deduced from its file name: for example, n51e002.hgt covers the area
   * between N 51° E 2° and N 52° E 3°, and s14w077.hgt covers S 14° W 77° to
   * S 13° W 76°. The file size depends on the resolution. If this is 1",
   * there are 3601 rows of 3601 cells each; if it is 3", there are 1201 rows
   * of 1201 cells each. The rows are laid out like text on a page, starting
   * with the northernmost row, with each row reading from west to east. Each
   * cell has two bytes, and the elevation at that cell is
   * 256*(1st byte) + (2nd byte). It follows that a 3" HGT file has a file
   * length of 2 x 1201 x 1201. SRTM 3" cells are calculated by calculating
   * the mean of 1" cells and their eight neighbors.
   * It follows that the highest local point is likely to be higher than the
   * highest SRTM 3" cell. The difference should vary with the steepness of
   * the local relief.
   */
  
  /*
   * Tile package URL retrieval
   * 
   * OK, this is kinda messed up, as there is no proper tile package index
   * available and the naming is totally inconsistent.
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
   * or get download links from this site:
   * - http://www.imagico.de/map/demsearch.php
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
   * 
   * 
   * 
   */

}
