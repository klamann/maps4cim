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
package de.nx42.maps4cim.map.relief;

import static de.nx42.maps4cim.util.math.MathExt.rounds;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.relief.SrtmDef;
import de.nx42.maps4cim.map.ReliefMap;
import de.nx42.maps4cim.map.ex.ReliefProcessingException;
import de.nx42.maps4cim.map.relief.srtm.TileDownload;
import de.nx42.maps4cim.util.Compression;
import de.nx42.maps4cim.util.arr2d.Arrays2D;
import de.nx42.maps4cim.util.arr2d.GapInterpolator;
import de.nx42.maps4cim.util.arr2d.ImageJInterpolation;
import de.nx42.maps4cim.util.gis.Area;
import de.nx42.maps4cim.util.math.MathExt;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class SRTM extends ReliefMap {

    /*
     * SRTM spec:
     * - The names of individual data tiles refer to the longitude and latitude
     *   of the lower-left (southwest) corner of the tile.
     * - To be more exact, these coordinates refer to the geometric center
     *   of the lower left sample, which in the case of SRTM3 data will be about
     *   90 meters in extent.
     * - SRTM3 data are sampled at three arc-seconds and contain 1201 lines with
     *   each 1201 samples
     * - The rows at the north and south edges as well as the columns at the
     *   east and west edges of each cell overlap and are identical to the edge
     *   rows and columns in the adjacent cell.
     *
     * File Format:
     * - The DEM is provided as 16-bit signed integer data in a simple binary
     *   raster. There are no header or trailer bytes embedded in the file.
     * - Byte order is Motorola ("big-endian") standard with the most
     *   significant byte first.
     * - Since they are signed integers elevations can range from -32767 to
     *   32767 meters, encompassing the range of elevation to be found on the
     *   Earth.
     * - Voids are flagged with the value -32768
     *
     * Source: http://dds.cr.usgs.gov/srtm/version2_1/Documentation/SRTM_Topo.pdf
     *
     */

    private static org.slf4j.Logger log = LoggerFactory.getLogger(SRTM.class);

    public static final int srtm1length = 3601;
    public static final int srtm3length = 1201;
    public static final int srtm1maxIndex = srtm1length - 1;
    public static final int srtm3maxIndex = srtm3length - 1;
    public static short gap = Short.MIN_VALUE;

    protected static final GapInterpolator gip = new GapInterpolator(gap);

    protected Area bounds;
    protected boolean heightOffsetAuto = true;
    protected float heightOffset = 0;
    protected boolean heightScaleAuto = true;
    protected float heightScale = 1.0f;
    // currently only srtm3 accepted
    protected int srtmLength = srtm3length;
    protected int srtmMaxIndex = srtm3maxIndex;


    public SRTM(Config conf) {
        this((SrtmDef) conf.getReliefTrans(), Area.of(conf.getBoundsTrans()));
    }

    public SRTM(SrtmDef def, Area bounds) {
        log.debug("Using SRTM as source for the map's relief");

        // read configuration settings
        this.bounds = bounds;

        // height offset
        this.heightOffsetAuto = def.isHeightOffsetAuto();
        if(heightOffsetAuto) {
            this.heightOffset = 32767;  // max value -> reduce later
        } else {
            this.heightOffset = (float) def.getHeightOffset();
        }

        // height scale
        this.heightScaleAuto = def.isHeighScaleAuto();
        if(heightScaleAuto) {
            this.heightScale = (float) (8.0 / bounds.getWidthKm());
        } else {
            this.heightScale = (float) def.getHeightScale();
        }



    }

    @Override
    public float[][] generateRelief() throws ReliefProcessingException {
        try {
            return fromBounds();
        } catch (SocketTimeoutException e) {
            log.error("Could not download the required source data, the connection timed out. Usually, this is a problem with your network connection or firewall configuration.");
            throw new ReliefProcessingException(e);
        } catch (UnknownHostException e) {
            log.error("The URL of the SRTM-Server {} could not be resolved. Are you connected to the internet?", e.getMessage());
            throw new ReliefProcessingException(e);
        } catch (IOException e) {
            log.error("I/O Exception while processing the relief.", e);
            throw new ReliefProcessingException(e);
        }
    }

    protected float[][] fromBounds() throws IOException, SocketTimeoutException, UnknownHostException {

        // get some basic metadata and log a note
        double worldWidthKM = bounds.getWidthKm();
        double worldHeightKM = bounds.getHeightKm();

        log.info("The relief map will be generated for an area of {}x{}km, " +
        		"with the center at ({}). The virtual zero height {} and all " +
        		"elevations are scaled {}.",
        		rounds(worldWidthKM), rounds(worldHeightKM), bounds.getCenter().toString(),
        		heightOffsetAuto ?
        		        "will be set to the highest possible value" :
        		        "is set to " + heightOffset,
        		!MathExt.equalsDouble(heightScale, 1.0, 0.001) ?
        		        "by factor " + rounds(heightScale) :
        		        "1:1");

        // keep ratio: do not stretch the map!
        double widthToHeight = worldWidthKM / worldHeightKM;
        int validMapWidth = widthToHeight >= 1.0 ? edgeLength : (int) Math.round(edgeLength * widthToHeight);
        int validMapHeight = widthToHeight <= 1.0 ? edgeLength : (int) Math.round(edgeLength / widthToHeight);

        // valid map indices
        int minX = (edgeLength - validMapWidth) / 2;
        int maxX = validMapWidth + minX;
        int minY = (edgeLength - validMapHeight) / 2;
        int maxY = validMapHeight + minY;

        // get source data (cache or download)
        log.debug("Retrieving SRTM data.");
        short[][] srtm = retrieveSRTMdata(bounds);
        int srtmHeight = srtm.length;
        int srtmWidth = srtm[0].length;

        // rotate, fill gaps and convert to basic 2d-float-array
        float[][] srtmClean = new float[srtmHeight][srtmWidth];
        for (int y = 0; y < srtm.length; y++) {
            for (int x = 0; x < srtm[y].length; x++) {
                float val = srtm[srtm.length-1-y][x];
                if(val == gap) {
                    val = gip.star(srtm, x, srtm.length-1-y);
                }
                if(val == 0) {
                    // drown the water ;)
                    val = -40;
                }
                srtmClean[y][x] = val;
            }
        }

        // calcualte bounds
        int srtmLatMin = (int) Math.floor(bounds.getMinLat());
        int srtmLatMax = (int) Math.ceil(bounds.getMaxLat());
        int srtmLonMin = (int) Math.floor(bounds.getMinLon());
        int srtmLonMax = (int) Math.ceil(bounds.getMaxLon());
        int srtmLatSize = srtmLatMax - srtmLatMin;
        int srtmLonSize = srtmLonMax - srtmLonMin;

        float minLat = (float) ((Math.abs(bounds.getMinLat() - srtmLatMin) / srtmLatSize) * srtmHeight);
        float maxLat = (float) ((Math.abs(bounds.getMaxLat() - srtmLatMin) / srtmLatSize) * srtmHeight);
        float minLon = (float) ((Math.abs(bounds.getMinLon() - srtmLonMin) / srtmLonSize) * srtmWidth);
        float maxLon = (float) ((Math.abs(bounds.getMaxLon() - srtmLonMin) / srtmLonSize) * srtmWidth);

        // interpolate with ImageJ
        log.debug("SRTM data will be cropped and scaled to correct size using bicubic interpolation");
        float[][] scaled = ImageJInterpolation.cropAndResize(srtmClean, validMapWidth, edgeLength,
                minLon, minLat, maxLon, maxLat);

        log.debug("Final conversion and filtering of scaled SRTM data");
        float[][] heightmap = new float[edgeLength][edgeLength];
        for (int y = 0; y < scaled.length; y++) {
            for (int x = 0; x < scaled.length; x++) {
                if(x >= minX && x < maxX && y >= minY && y < maxY) {
                    float val = scaled[y-minY][x-minX];
                    if(heightOffsetAuto && val>=0 && val < this.heightOffset) {
                        this.heightOffset = val;
                    }
                    heightmap[y][x] = val;
                }
            }
        }

        // apply height offset and scale (in-place)
        if(heightOffsetAuto) {
        	if(heightOffset < 1) {
        		heightOffset = 0;
        	}
            log.debug("The virtual zero height has been set to {}", heightOffset);
        }

        if(heightOffset != 0.0f || heightScale != 1.0f) {
        	for (int y = 0; y < heightmap.length; y++) {
                for (int x = 0; x < heightmap.length; x++) {
                    if(heightmap[y][x] > 0) {
                        heightmap[y][x] = (heightmap[y][x] - heightOffset) * heightScale;
                    }
                }
            }
        }


        return heightmap;
    }


    // helpers

    protected float getValue(short[][] srtm, int x, int y) {
        if(srtm[srtmMaxIndex-y][x] < -50)
            return 0;
        return (srtm[srtmMaxIndex-y][x] - heightOffset) * heightScale;
    }


	protected short[][] retrieveSRTMdata(Area ar) throws SocketTimeoutException, IOException, UnknownHostException {
		// get source
		TileDownload td = new TileDownload();
		File[][] files = td.getTiles(ar);

		// transform to single short array
		if(files.length == 1 && files[0].length == 1) {
			// just one tile -> return
			byte[] rawTile = readArchiveSRTM(files[0][0]);
			return getNativeSRTM(rawTile);
		} else {
			// multiple tiles, need to be combined
		    log.debug("combining {} tiles.", files.length * files[0].length);
			short[][][][] source = unpackSRTMTiles(files);

			// validate only, if within SRTM bounds
			boolean validate = ar.getMaxLat() < 61 && ar.getMinLat() >= -60;
			return Arrays2D.combine(source, 1, validate);
		}
	}

	protected short[][][][] unpackSRTMTiles(File[][] files) throws IOException {

		short[][][][] unpacked = new short[files.length][files[0].length][srtmLength][srtmLength];

		for (int y = 0; y < files.length; y++) {
			for (int x = 0; x < files[y].length; x++) {
				byte[] rawTile = readArchiveSRTM(files[files.length - 1 - y][x]);
				short[][] srtm = getNativeSRTM(rawTile);
				unpacked[y][x] = srtm;
			}
		}

		return unpacked;
	}


    protected short[][] getNativeSRTM(File input) throws IOException {
        byte[] raw = Files.toByteArray(input);
        return getNativeSRTM(raw);
    }

    protected short[][] getNativeSRTM(byte[] input) throws IOException {
        if(input == null || input.length == 0) {
            return getEmptySRTMTile();
        }

        ByteArrayDataInput badi = ByteStreams.newDataInput(input);

        // write native srtm values in 16bit signed integer array
        short[][] srtm = new short[srtmLength][srtmLength];
        for (int y = 0; y < srtmLength; y++) {
            for (int x = 0; x < srtmLength; x++) {
                // convert the floats (meter) to integers (millimeter)
                srtm[y][x] = badi.readShort();
            }
        }

        return srtm;
    }

    protected static byte[] readArchiveSRTM(File zipFile) throws IOException {
        if(zipFile == null || !zipFile.exists()) {
            return null;
        }
        return Compression.readFirstZipEntry(zipFile);
    }

    protected short[][] getEmptySRTMTile() {
        short[][] srtm = new short[srtmLength][srtmLength];
        for (int y = 0; y < srtmLength; y++) {
            for (int x = 0; x < srtmLength; x++) {
                srtm[y][x] = 1;
            }
        }
        return srtm;
    }

}
