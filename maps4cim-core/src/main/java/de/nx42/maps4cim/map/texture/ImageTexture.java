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
package de.nx42.maps4cim.map.texture;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.ImageDef;
import de.nx42.maps4cim.map.TextureMap;
import de.nx42.maps4cim.map.ex.TextureProcessingException;
import de.nx42.maps4cim.map.texture.data.Texture;
import de.nx42.maps4cim.util.ImageJUtils;

public class ImageTexture extends TextureMap {

    private static Logger log = LoggerFactory.getLogger(ImageTexture.class);

    protected ImagePlus img;

    protected ColorDef black;
    protected ColorDef white;
    protected ColorDef red;
    protected ColorDef green;
    protected ColorDef blue;


    public ImageTexture(Config conf) throws IOException {
        this((ImageDef) conf.getTextureTrans());
    }

    public ImageTexture(ImageDef def) throws IOException {
        def.fillMissingColors();
        construct(new File(def.imageFilePath),
                def.blackTranslation,
                def.whiteTranslation,
                def.redTranslation,
                def.greenTranslation,
                def.blueTranslation);
    }

    public ImageTexture(String imagePath, ColorDef black, ColorDef white, ColorDef red, ColorDef green, ColorDef blue) throws IOException {
        construct(new File(imagePath), black, white, red, green, blue);
    }

    public ImageTexture(File imageFile, ColorDef black, ColorDef white, ColorDef red, ColorDef green, ColorDef blue) throws IOException {
        construct(imageFile, black, white, red, green, blue);
    }

    /**
     * Constructs this object. This function is necessary to circumvent Javas
     * totally useless constructor restrictions. Forcing you to make this() or
     * super() the first call does not prevent you from screwing up the
     * constructor, so what's the point?
     * @param imageFile the image to load
     * @param black translation of the lightness (darker half)
     * @param white translation of the lightness (lighter half)
     * @param red translation of the red color channel
     * @param green translation of the green color channel
     * @param blue translation of the blue color channel
     * @throws IOExceptionif the image can't be loaded
     */
    private void construct(File imageFile, ColorDef black, ColorDef white, ColorDef red, ColorDef green, ColorDef blue) throws IOException {
        // get image
        this.img = ImageJUtils.safeOpenImage(imageFile);

        // get colors
        this.black = black;
        this.white = white;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }


	@Override
	public int[][] generateTexture() throws TextureProcessingException {
        log.debug("Generating texture map from a {} image with {}×{} pixels.",
                ImageJUtils.hasColorChannels(img) ? "colored" : "grayscale",
                img.getWidth(), img.getHeight());

	    ImageProcessor ip = img.getProcessor();

        // resize to map size
        if(ip.getWidth() != edgeLength || ip.getHeight() != edgeLength) {
            ip = ip.resize(edgeLength, edgeLength);
            if(img.getWidth() * img.getHeight() < edgeLength * edgeLength) {
                ip.smooth();
            }
        }

        // transform to 2d float-array
        int[][] imgArr2D = ip.getIntArray();

        // transform colors and rotate
        int[][] texture = null;
        if(ImageJUtils.hasColorChannels(img)) {
            texture = transformColorTexture(imgArr2D);
        } else {
            texture = transformGrayscaleTexture(imgArr2D);
        }

        return texture;
	}

	protected int[][] transformColorTexture(int[][] image) {
        int maxIdx = edgeLength - 1;
	    int[][] adjusted = new int[edgeLength][edgeLength];

        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[y].length; x++) {

                // retrieve RGB components
                int argb = image[y][maxIdx- x];

                int r = (argb) & 0xFF;
                int g = (argb>>8) & 0xFF;
                int b = (argb>>16) & 0xFF;
                int a = (argb>>24) & 0xFF;

                // relative color distribution in this pixel
                float combined = r + g + b;
                float relR = r / combined;
                float relG = g / combined;
                float relB = b / combined;
                float relA = a / 255f;

                /*
                 * Properties of CiM Texture/Color System
                 * - Base color (green)
                 * - Overlay colors (darkgreen, red, white)
                 * - combination of overlay colors must not exceed 1
                 * - every overlay < 1 lets background (green) shine through
                 *
                 * Use properties from HSL colorspace to separate black, white
                 * and colored components (as we need to actually draw black
                 * and white, because a green background is not quite helpful
                 * for that).
                 */

                // calculate the lightness (as defined in HSL color space)
                float min = Math.min(r, Math.min(g, b)) / 255f;
                float max = Math.max(r, Math.max(g, b)) / 255f;
                float lig = (max + min) / 2;

                // calculate the relative weight of black, white and color in this pixel
                float relBlack = lig < 0.5f ? 1 - (lig * 2) : 0;      // black below 0.5 lightness
                float relWhite = lig > 0.5f ? (lig - 0.5f) * 2 : 0;   // white above 0.5 lightness
                float relColor = 1 - relBlack - relWhite;             // fill with color what is neither black nor white

                // generate the actual colors
                int texBlack = Texture.draw(black, relBlack * relA);
                int texWhite = Texture.draw(white, relWhite * relA);

                int texRed = Texture.draw(red, relColor * relR * relA);
                int texGreen = Texture.draw(green, relColor * relG * relA);
                int texBlue = Texture.draw(blue, relColor * relB * relA);

                // combine and save
                int texColor = Texture.mixAddBase(texRed, texGreen, texBlue);
                adjusted[y][x] = Texture.mixAddBase(texBlack, texWhite, texColor);
            }
        }

	    return adjusted;
	}

	protected int[][] transformGrayscaleTexture(int[][] image) {
	    int maxIdx = edgeLength - 1;
	    float maxValue = (float) Math.pow(2, img.getBitDepth());
        int[][] adjusted = new int[edgeLength][edgeLength];

        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[y].length; x++) {

                // retrieve grayscale value
                int value = image[y][maxIdx- x];
                float relValue = value / maxValue;

                // generate the actual colors
                int texBlack = Texture.draw(black, (1 - relValue));
                int texWhite = Texture.draw(white, relValue);

                // combine and save
                adjusted[y][x] = Texture.mixAddBase(texBlack, texWhite);
            }
        }

        return adjusted;
    }

}
