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
package de.nx42.maps4cim.map.texture.data;

import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.util.math.MathExt;

public class TexHexTriplet {

    protected static final int sigDigits = 4;

    public final int roughgrass;
    public final int mud;
    public final int dirt;

    public TexHexTriplet(int roughgrass, int mud, int dirt) {
        this.roughgrass = roughgrass;
        this.mud = mud;
        this.dirt = dirt;
    }

    // getters

    /**
     * @return the roughgrass
     */
    public int getRoughgrass() {
        return roughgrass;
    }

    /**
     * @return the mud
     */
    public int getMud() {
        return mud;
    }

    /**
     * @return the dirt
     */
    public int getDirt() {
        return dirt;
    }

    public String getHexString() {
        return toTwoDigitHex(roughgrass) + toTwoDigitHex(mud) + toTwoDigitHex(dirt);
    }

    public int getTexture() {
        return Texture.draw(roughgrass, mud, dirt);
    }

    public ColorDef getColorDef() {
        final double r = MathExt.round(roughgrass / 255d, sigDigits);
        final double m = MathExt.round(mud / 255d, sigDigits);
        final double d = MathExt.round(dirt / 255d, sigDigits);
        return new ColorDef() {{
            roughGrass = r;
            mud = m;
            dirt = d;
        }};
    }

    // constructors / parsers

    public static TexHexTriplet of(double roughgrass, double mud, double dirt) {
        int r = (int) (minMaxCutoff(roughgrass, 0, 1) * 255);
        int m = (int) (minMaxCutoff(mud, 0, 1) * 255);
        int d = (int) (minMaxCutoff(dirt, 0, 1) * 255);
        return new TexHexTriplet(r, m, d);
    }

    public static TexHexTriplet of(int roughgrass, int mud, int dirt) {
        return new TexHexTriplet(roughgrass, mud, dirt);
    }
    
    public static TexHexTriplet of(ColorDef c) {
        return TexHexTriplet.of(c.getSafeRoughGrass(), c.getSafeMud(), c.getSafeDirt());
    }

    public static TexHexTriplet parse(String s) throws NumberFormatException {
        String hex = s.startsWith("#") ? s.substring(1) : s;
        if(hex.length() == 6) {
            return parse(Integer.parseInt(hex, 16));
        } else {
            throw new NumberFormatException(hex + " is not a valid hex triplet");
        }
    }

    public static TexHexTriplet parse(int hex) {
        int r = (hex >>> 16) & 0xFF;
        int m = (hex >>>  8) & 0xFF;
        int d = (hex       ) & 0xFF;
        return new TexHexTriplet(r, m, d);
    }

    // static helpers

    public static String convertToHexString(int roughgrass, int mud, int dirt) {
        return toTwoDigitHex(roughgrass) + toTwoDigitHex(mud) + toTwoDigitHex(dirt);
    }


    protected static double minMaxCutoff(double input, double min, double max) {
        return input > max ? max : input < min ? min : input;
    }

    protected static String toTwoDigitHex(int input) {
        int normalized = (int) minMaxCutoff(input, 0, 255);
        String hex = Integer.toHexString(normalized);
        return normalized < 16 ? "0" + hex : hex;
    }

    // Object overrides

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dirt;
        result = prime * result + mud;
        result = prime * result + roughgrass;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TexHexTriplet other = (TexHexTriplet) obj;
        if (dirt != other.dirt)
            return false;
        if (mud != other.mud)
            return false;
        if (roughgrass != other.roughgrass)
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#" + getHexString();
    }

}
