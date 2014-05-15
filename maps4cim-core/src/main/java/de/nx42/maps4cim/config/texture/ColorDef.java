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
package de.nx42.maps4cim.config.texture;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.oval.constraint.Range;

/**
 * A single ground texture "color", that can be combined from all available
 * texture types in the game. Each texture type can have a opacity between 0.0
 * and 1.0, but beware: strange things can happen if the sum of all opacities
 * exceeds 1.0. Usually, the textures become pink or something like that ;)
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlRootElement(name = "color")
public class ColorDef {
    
    protected static final String colorRangeError = "Color values must be in the range 0.0 through 1.0";

	/** the name this color can be referred to as by the different entities */
    @XmlAttribute(name = "name")
    public String name;

    /** the portion of 'grass' in this texture. This is the basic texture
        that is always drawn if nothing is defined */
    @XmlAttribute(name = "grass")
    @Range(min=0.0, max=1.0, message=colorRangeError)
    public Double grass;

    /** the portion of 'rough grass' in this texture. This is a very dark type
        of grass */
    @XmlAttribute(name = "rough-grass")
    @Range(min=0.0, max=1.0, message=colorRangeError)
    public Double roughGrass;

    /** the portion of 'mud' in this texture. Brown and slimy, just as kids
        love it :) */
    @XmlAttribute(name = "mud")
    @Range(min=0.0, max=1.0, message=colorRangeError)
    public Double mud;

    /** the portion of 'dirt' in this texture. Has a bright yellowish tone */
    @XmlAttribute(name = "dirt")
    @Range(min=0.0, max=1.0, message=colorRangeError)
    public Double dirt;

    /** Pavement: A special texture that overlaps all other textures. Does
        not have an alpha-channel, so it is best to avoid  */
    @XmlAttribute(name = "pavement")
    @Range(min=0.0, max=1.0, message=colorRangeError)
    public Double pavement;

    /** Black: This is no texture at all, it just removes any texture and with
        it the ground itself. You'll get a black hole in your map that not even
        Chuck Norris can repair (not to mention the map editor...).
        Just don't use it, all right?  */
    @XmlAttribute(name = "black")
    @Range(min=0.0, max=1.0, message=colorRangeError)
    public Double black;


    /**
     * Safely retrieve the grass value. Defaults to 0 if undefined.
     * @return the user defined value for grass, or 0 if undefined.
     */
    public float getSafeGrass() {
        return getFloatDefaultZero(grass);
    }

    /**
     * Safely retrieve the rough grass value. Defaults to 0 if undefined.
     * @return the user defined value for rough grass, or 0 if undefined.
     */
    public float getSafeRoughGrass() {
        return getFloatDefaultZero(roughGrass);
    }

    /**
     * Safely retrieve the mud value. Defaults to 0 if undefined.
     * @return the user defined value for mud, or 0 if undefined.
     */
    public float getSafeMud() {
        return getFloatDefaultZero(mud);
    }

    /**
     * Safely retrieve the dirt value. Defaults to 0 if undefined.
     * @return the user defined dirt for grass, or 0 if undefined.
     */
    public float getSafeDirt() {
        return getFloatDefaultZero(dirt);
    }

    /**
     * Safely retrieve the pavement value. Defaults to 0 if undefined.
     * @return the user defined value for pavement, or 0 if undefined.
     */
    public float getSafePavement() {
        return getFloatDefaultZero(pavement);
    }

    /**
     * Safely retrieve the black value. Defaults to 0 if undefined.
     * @return the user defined value for black, or 0 if undefined.
     */
    public float getSafeBlack() {
        return getFloatDefaultZero(black);
    }
    
    /**
     * Retrieves the specified Double as float, defaults to 0.0f
     * if input is null
     * @param value the value to convert
     * @return the Double as float, 0.0f if null
     */
    protected static float getFloatDefaultZero(Double value) {
        return value != null ? value.floatValue() : 0.0f;
    }


    /**
     * Retrieves a color by it's name
     * @param colors the list of colors to search in
     * @param name the name of the color to get
     * @return the color, if a color with matching name is found, or null
     */
    public static ColorDef getColorByName(Collection<ColorDef> colors, String name) {
        for (ColorDef colorDef : colors) {
            if(colorDef.name.equals(name)) {
                return colorDef;
            }
        }
        return null;
    }

    /**
     * @return some default color definitions
     */
    public static List<ColorDef> getDefaults() {
        List<ColorDef> colors = new LinkedList<ColorDef>();

        colors.add(new ColorDef() {{ name="wood"; roughGrass=0.4; }});
        colors.add(new ColorDef() {{ name="green"; roughGrass=0.2;  }});
        colors.add(new ColorDef() {{ name="farm"; roughGrass=0.15; dirt=0.3; }});
        colors.add(new ColorDef() {{ name="garden"; roughGrass=0.15; dirt=0.1; }});
        colors.add(new ColorDef() {{ name="leisure"; roughGrass=0.2; mud=0.1; }});
        colors.add(new ColorDef() {{ name="building"; mud=0.6; }});
        colors.add(new ColorDef() {{ name="road"; dirt=0.6; mud=0.4;  }});
        colors.add(new ColorDef() {{ name="town"; dirt=0.1; mud=0.1;  }});
        colors.add(new ColorDef() {{ name="water"; dirt=0.5;  }});
        colors.add(new ColorDef() {{ name="railway"; mud=0.8;  }});

        return colors;
    }

}
