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
package de.nx42.maps4cim.map;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.common.io.LittleEndianDataOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.map.ex.MapGeneratorException;
import de.nx42.maps4cim.map.ex.ReliefProcessingException;

/**
 * The ReliefMap describes the elevations of the resulting map.
 * 
 * Each map has an ingame size of 8x8 km or a quadratic size of 64 km^2.
 * The ReliefMap contains a two-dimensional matrix with values ranging from
 * -1048.575 to +1048.576 meters, which describe the height of each single
 * control point of the map.
 * 
 * Each map holds a raster of 2048x2048 equally sized fields. The ReliefMap
 * contains 2049x2049 entries, where each entry defines a control point
 * located in the south-west corner of a field (that's why there is one more
 * row and column for the north and the east edge).
 * 
 * Values are represented in the ReliefMap as floats, which describe the metric
 * ingame elevations. In the final map, they are converted to 32bit signed
 * integers (little Endian), rounded to full millimeters (this is the CiM 2
 * map format).
 * 
 * The design of the elevations in CiM 2 has some serious drawbacks that cannot
 * be circumvented:
 * 1. no water above 0m. This means no rivers and no lakes above sea level
 * 2. only one height per square. This means no caves or cliffs with extreme angles
 * 
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public abstract class ReliefMap {

    private static Logger log = LoggerFactory.getLogger(ReliefMap.class);

    protected static final int edgeLength = 2049;
    protected static final int byteAmount = 2049 * 2049 * 4;

    /** minimum allowed value (internal, millimeters) */
    protected static final int minVal = -1048575;
    /** maximum allowed value (internal, millimeters) */
    protected static final int maxVal =  1048576;
    /** minimum allowed value (meters) */
    protected static final float minValF = -1048.575f;
    /** maximum allowed value (meters) */
    protected static final float maxValF =  1048.576f;

    /*
     * Factsheet:
     * - 2049x2049 float array
     * - allowed values: [- 1048.575; + 1048.576]
     * - left to right // west to east // x achsis
     * - bottom to top // south to north // y achsis
     * - float[y][x]
     * - float[0][0] = bottom left = southwest
     * - float[2048][2048] = top right = northeast
     */
    
    /**
     * Generates the full relief map as 2d-array, each data point representing
     * a control point of the map, in meters.
     * 
     * Only this function must be overriden by implementations.
     * 
     * @return the relief map, as 2d float array
     * @throws ReliefProcessingException if anything goes wrong (please wrap
     * other exceptions in this one)
     */
    public abstract float[][] generateRelief() throws ReliefProcessingException;

    /**
     * Generates the relief map by calling {@link ReliefMap#generateRelief()},
     * converts it to the CiM 2 map format and writes it to the specified output
     * stream
     * @param out the output stream to write the relief map into
     * @throws MapGeneratorException when the relief cannot be generated
     * @throws IOException when the resulting map can't be written
     */
    public void writeTo(OutputStream out) throws MapGeneratorException, IOException {
        float[][] reliefMap = generateRelief();

        log.debug("Storing relief in native CiM2-Map format");
        storeByteStream(out, reliefMap);
        log.debug("Relief was written to file");
    }

    /**
     * Stores the relief map in the specified output stream, transforming
     * the floats to little endian 32 bit integers.
     * @param out the output stream to write the relief map into
     * @param reliefMap the relief map to write
     * @throws MapGeneratorException when the size of the relief map is invalid
     * @throws IOException when the resulting map can't be written
     */
    protected void storeByteStream(OutputStream out, float[][] reliefMap)
            throws MapGeneratorException, IOException {

        if (isValidSize(reliefMap)) {
            // byte outputput stream, wrapped by a little endian integer processor
            ByteArrayOutputStream bos = new ByteArrayOutputStream(byteAmount);
            LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(bos);

            // pass the resulting integers to the little endian byte output stream
            for (int y = 0; y < edgeLength; y++) {
                for (int x = 0; x < edgeLength; x++) {
                    // convert the floats (meter) to integers (millimeter)
                    dos.writeInt((int) (reliefMap[y][x] * 1000));
                }
            }

            // write to the user defined output stream
            bos.writeTo(out);
            
            // close streams
            dos.close();
            bos.close();
        } else {
            throw new MapGeneratorException("The size of the relief map is invalid. "
                    + "Only Maps with 2049 * 2049 control points are allowed");
        }

    }

    /**
     * Checks, if all arrays in the relief map have the correct size
     * @param reliefMap the relief map to check
     * @return true, iff the wrapping array's and all contained array's length
     * equals the predefined edge length
     */
    protected static boolean isValidSize(float[][] reliefMap) {
        int lenY = reliefMap.length;
        int lenX = reliefMap[0].length;
        if (lenX == edgeLength && lenY == edgeLength) {
            for (float[] fs : reliefMap) {
                if (fs.length != edgeLength) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
}
