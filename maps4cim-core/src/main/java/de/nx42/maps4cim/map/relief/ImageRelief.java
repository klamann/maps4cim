/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 - 2014 Sebastian Straub
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

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.relief.HeightmapDef;
import de.nx42.maps4cim.map.ReliefMap;
import de.nx42.maps4cim.map.ex.ReliefProcessingException;
import de.nx42.maps4cim.util.ImageJUtils;
import de.nx42.maps4cim.util.math.Statistics;



public class ImageRelief extends ReliefMap {

    private static Logger log = LoggerFactory.getLogger(ImageRelief.class);

    protected static final double defaultMin = 0;
    protected static final double defaultMax = 300;

    protected ImagePlus img;
    protected double minHeight;
    protected double maxHeight;


    public ImageRelief(Config conf) throws IOException {
        this((HeightmapDef) conf.getReliefTrans());
    }

    public ImageRelief(HeightmapDef def) throws IOException {
        this(def.heightMapPath, def.heightMapMinimum, def.heightMapMaximum);
    }

    public ImageRelief(String imagePath) throws IOException {
        this(new File(imagePath));
    }

    public ImageRelief(String imagePath, Double minHeight, Double maxHeight) throws IOException {
        this(new File(imagePath), minHeight, maxHeight);
    }

    public ImageRelief(File imagePath) throws IOException {
        this(imagePath, defaultMin, defaultMax);
    }

    public ImageRelief(File imageFile, Double minHeight, Double maxHeight) throws IOException {

        this.minHeight = minHeight == null ? defaultMin : minHeight;
        this.maxHeight = maxHeight == null ? defaultMax : maxHeight;
        if(this.minHeight > this.maxHeight) {
            double tmp = this.minHeight;
            this.minHeight = this.maxHeight;
            this.maxHeight = tmp;
        }

        this.img = ImageJUtils.safeOpenImage(imageFile);
    }


    /*
     * Howto?
     * - Read any 16-bit grayscale (in fact, just read any image...)
     * - reduce colors (if greyscale saved as RGB)
     * - scale to 2049x2049
     * - map from scale to height
     * - return
     *
     */

    @Override
    public float[][] generateRelief() throws ReliefProcessingException {
        ImageProcessor ip = img.getProcessor();

        // convert colored images to grayscale
        if(ImageJUtils.hasColorChannels(img)) {
            /*
             * convert any input to grayscale
             * If the image is a grayscale image which has only been saved as RGB,
             * it remains the same. If it's a colored image, the average lightness
             * of each channel is stored.
             * Only 8 bit results are supported; ImageJ cannot convert 16bit RGBs
             * to 16bit grayscale withoout reducing them to 8 bit, so no distinction
             * is required here...
             */
            ip = ip.convertToByte(true);
        }

        // resize to map size
        if(ip.getWidth() != edgeLength || ip.getHeight() != edgeLength) {
            ip = ip.resize(edgeLength, edgeLength);
            if(img.getWidth() * img.getHeight() < edgeLength * edgeLength) {
                ip.smooth();
            }
        }

        // gather statistical data, notify the user
        double[] data = Statistics.getDoubleArray(ip.getPixels(), false);
        Statistics stats = new Statistics(data);

        log.debug("Generating the relief map from an image with a data range " +
                "of {} (min: {}, max: {}), a median value of {} and {} " +
                "unique data points.", stats.getRange(), stats.getMin(),
                stats.getMax(), stats.getMedian(), stats.getUniques());

        String report = getImageQualityReport(stats);
        if(!Strings.isNullOrEmpty(report)) {
            log.warn(report);
        }

        // transform to 2d float-array
        float[][] relief = ip.getFloatArray();

        // scale and rotate
        int maxIdx = edgeLength - 1;
        float srcOffset = (float) stats.getMin();
        float destOffset = (float) minHeight;
        float scale = (float) ((maxHeight - minHeight) / stats.getRange());

        float[][] adjusted = new float[edgeLength][edgeLength];
        for (int y = 0; y < relief.length; y++) {
            for (int x = 0; x < relief[y].length; x++) {
                adjusted[y][x] = ((relief[x][maxIdx - y] - srcOffset) * scale) + destOffset;
            }
        }

        return adjusted;
    }

    protected String getImageQualityReport(Statistics stats) {
        StringBuilder sb = new StringBuilder(512);
        ImageProcessor ip = img.getProcessor();

        // not a greyscale
        if(ImageJUtils.hasColorChannels(img) && !ip.isGrayscale()) {
            sb.append("Your heightmap is colored, but only greyscale " +
                    "heightmaps are supported; Your image will be converted " +
                    "to grayscale, all color information will be lost!");
        }

        // small image
        if(img.getWidth() * img.getHeight() < 0.25 * edgeLength * edgeLength) {
            if(sb.length() > 0) {
                sb.append(" Besides, the");
            } else {
                sb.append("The");
            }
            sb.append(String.format(" heightmap is very small (%s×%s pixel); " +
                    "CiM 2 maps have a resolution of %s×%s data points. " +
                    "For best results, use images with a higher resolution!",
                    img.getWidth(), img.getHeight(), edgeLength, edgeLength));
        }

        // low bit depth
        if(ip.getBitDepth() <= 8 || ip.getBitDepth() == 24 || (ip.getBitDepth() > 8 && stats.getUniques() <= 256)) {
            if(sb.length() > 0) {
                sb.append(" Furthermore, you");
            } else {
                sb.append("You");
            }
            if(ip.getBitDepth() <= 8 || ip.getBitDepth() == 24) {
                sb.append(" are using an 8 bit image as heightmap, which might " +
                        "cause poor results (e.g. steps in the landscape). Use " +
                        "16 bit heightmaps for best results!");
            } else {
                sb.append(String.format(" are using a 16 bit image as " +
                        "heightmap, but it does only contain %s different " +
                        "values, which makes it no better than an 8 bit " +
                        "image. Try to get a real 16 bit heightmap for " +
                        "quality results.", stats.getUniques()));
            }
        }

        return sb.toString();
    }

}
