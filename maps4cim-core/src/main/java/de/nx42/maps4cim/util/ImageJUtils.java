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
package de.nx42.maps4cim.util;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;

import de.nx42.maps4cim.util.math.Statistics;

public class ImageJUtils {

    /*
     * Supported file types:
     * http://imagejdocu.tudor.lu/doku.php?id=faq:general:which_file_formats_are_supported_by_imagej
     */
    public static final String[] supportedFiles = {
        "tif", "tiff", "jpg", "jpeg", "bmp", "pgm", "ppm", "pbm", "gif", "png", "psd", "tga", "xbm", "xpm"
    };


    public static ImagePlus safeOpenImage(String imagePath) throws IOException {
        return safeOpenImage(new File(imagePath));
    }

    public static ImagePlus safeOpenImage(File imageFile) throws IOException {
        if (imageFile != null && imageFile.exists() && imageFile.canRead()) {
            try {
                return IJ.openImage(imageFile.getAbsolutePath());
            } catch(Exception e) {
                throw new IOException(String.format("Image %s cannot be opened", imageFile.getName()), e);
            }
        } else {
            throw new IOException(String.format("File %s cannot be accessed", imageFile));
        }
    }

    public static ImagePlus openFromResource(String res) {
        URL url = Resources.getResource(res);
        Image img = Toolkit.getDefaultToolkit().getImage(url);
        return new ImagePlus(null, img);
    }


    /*
     * Supported Image Types:
     *
     * image, channel, bit, grey, type
     * gray8:  1,  8, true, bit
     * gray16: 1, 16, true, short
     * gray24: -
     * gray32: 1, 32, true, float (0-1)
     *
     * rgb8:   1, 24, false, bit (per channel)      // why only 1 channel?
     * rgb16:  3, 16, true, short (per channel)     // why gray true? not for PNG...
     * rgb24:  -
     * rgb32:  -
     *
     * g_rgb8:  1, 24, true     // expected, still 1 channel
     * g_rgb16: 3, 16, true     // overlap with rgb16 -.-
     *
     * mapping:
     * 1 channel && (8 | 16 | 32 bit) --> gray
     * 24 bit || 3 channel & 16 bit   --> color
     *
     */


    public static boolean hasColorChannels(ImagePlus img) {
        return img.getNChannels() > 1 || img.getBitDepth() == 24;
    }

    public static Statistics calculateStats(ImagePlus img) {
        ImageProcessor ip = img.getProcessor();
        if(hasColorChannels(img)) {
            ip = ip.convertToByte(true);
        }
        double[] data = Statistics.getDoubleArray(ip.getPixels(), false);
        return new Statistics(data);
    }

}
