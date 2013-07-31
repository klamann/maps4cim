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
import de.nx42.maps4cim.map.ex.TextureProcessingException;
import de.nx42.maps4cim.map.texture.OsmTexture;

/**
 * The TextureMap describes the ground textures of the resulting maps
 * 
 * The TextureMap contains a two-dimensional matrix with 2048x2048 data points,
 * where each point stands for a type of ground texture and it's saturation.
 * 
 * Each data point consists of a 32 bit integer (4 bytes), with the first
 * 3 bytes standing for the saturation (0-255) for 3 different texture types
 * (dirt, mud and rough grass). The fourth byte describes 3 different textures:
 * black, grass and pavement. Also, different textures can be mixed. For more
 * details, see {@link OsmTexture}.
 * 
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public abstract class TextureMap {

    private static Logger log = LoggerFactory.getLogger(TextureMap.class);

    protected static final int edgeLength = 2048;
    protected static final int byteAmount = 2048 * 2048 * 4;

    /**
     * Generates the fill texture map as 2d-array, with each data point
     * representing the color of a 4x4 m square.
     * 
     * Only this function must be overriden by implementations.
     * 
     * @return the texture map, as 2d integer array (each integer representing
     * a texture definition)
     * @throws TextureProcessingException if anything goes wrong (please wrap
     * other exceptions in this one)
     */
    public abstract int[][] generateTexture() throws TextureProcessingException;

    /**
     * Generates the texture map by calling {@link TextureMap#generateTexture()},
     * converts it to the CiM 2 map format and writes it to the specified output
     * stream
     * @param out out the output stream to write the texture map into
     * @throws MapGeneratorException when the texture map cannot be generated
     * @throws IOException when the resulting map can't be written
     */
    public void writeTo(OutputStream out) throws MapGeneratorException, IOException {
        int[][] textureMap = generateTexture();

        log.debug("Storing texture map in native CiM2-Map format");
        storeByteStream(out, textureMap);
        log.debug("Texture map was written to file");
    }

    /**
     * Stores the texture map in the specified output stream, storing the integers
     * in little endian byte order
     * @param out the output stream to write the texture map into
     * @param textureMap the texture map to write
     * @throws MapGeneratorException when the size of the texture map is invalid
     * @throws IOException when the resulting map can't be written
     */
    protected void storeByteStream(OutputStream out, int[][] textureMap)
            throws MapGeneratorException, IOException {

        if (isValidSize(textureMap)) {
            // byte outputput stream, wrapped by a little endian integer processor
            ByteArrayOutputStream bos = new ByteArrayOutputStream(byteAmount);
            LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(bos);

            // pass the resulting integers to the little endian byte output stream
            for (int y = 0; y < edgeLength; y++) {
                for (int x = 0; x < edgeLength; x++) {
                    dos.writeInt(textureMap[x][y]);
                }
            }

            // writeTo to the user defined output streame
            bos.writeTo(out);
            
            // close streams
            dos.close();
            bos.close();
        } else {
            throw new MapGeneratorException("The size of the texture map is invalid. "
                    + "Only Maps with 2048 * 2048 blocks are allowed");
        }

    }

    /**
     * Checks, if all arrays in the texture map have the correct size
     * @param textureMap the texture map to check
     * @return true, iff the wrapping array's and all contained array's length
     * equals the predefined edge length
     */
    protected static boolean isValidSize(int[][] textureMap) {
        int lenY = textureMap.length;
        int lenX = textureMap[0].length;
        if (lenX == edgeLength && lenY == edgeLength) {
            for (int[] fs : textureMap) {
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
