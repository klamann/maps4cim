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
package de.nx42.maps4cim.gui.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.HashSet;
import java.util.Set;


public class Fonts {

    private static final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

    public static final String[] logicalFonts = {
        Font.DIALOG, Font.DIALOG_INPUT, Font.MONOSPACED, Font.SERIF, Font.SANS_SERIF
    };

    public static final int[] fontStyles = {
        Font.PLAIN, Font.BOLD, Font.ITALIC
    };

    public static final Set<String> physicalFonts = getPhysicalFonts();

    protected static Set<String> getPhysicalFonts() {
        Font[] fonts = ge.getAllFonts();
        HashSet<String> fontSet = new HashSet<String>(fonts.length / 3);
        for (Font font : fonts) {
            fontSet.add(font.getFamily());
        }
        return fontSet;
    }

    /**
     * Checks, if the JVM has access to the specified font.
     * Use font family names only, e.g. "Arial", not "Arial Bold"
     * @param font the name of the font (as returned by {@link Font#getFamily()}
     * @return true, iff the JVM has access to this font
     */
    public static boolean exists(String font) {
        return physicalFonts.contains(font);
    }

    /**
     * Selects the first font from the specified list of names that is available
     * on the system. If no valid font is found, the logical "Dialog"-Font is
     * used as a fallback.
     * For a detailed description of the parameters, see
     * {@link Font#Font(String, int, int)}
     * @param style the style constant of the font
     * @param size the point size of the Font
     * @param names the font names to look for
     * @return the first available font
     */
    public static Font select(int style, int size, String... names) {
        for (String name : names) {
            if(exists(name)) {
                return new Font(name, style, size);
            }
        }
        return new Font(Font.DIALOG, style, size);
    }

    /**
     * Selects the first font from the specified list of names that is available
     * on the system. If no valid font is found, the logical "Dialog"-Font is
     * used as a fallback.
     * Font size and style are derived from the specified ancestor Font object
     * @param ancestor the Font to copy relevant attributes from
     * @param names the font names to look for
     * @return the first available font with attributes from the ancestor-font
     */
    public static Font select(Font ancestor, String... names) {
        return select(ancestor.getStyle(), ancestor.getSize(), names);
    }

}
