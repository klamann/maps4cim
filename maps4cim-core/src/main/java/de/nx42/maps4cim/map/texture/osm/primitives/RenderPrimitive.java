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
package de.nx42.maps4cim.map.texture.osm.primitives;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.EntityDef;
import de.nx42.maps4cim.map.texture.CiMTexture;


public abstract class RenderPrimitive {

	private static final Logger log = LoggerFactory.getLogger(RenderPrimitive.class);

    public final int color;


    public RenderPrimitive() {
        this.color = CiMTexture.GRASS.draw();
    }

    public RenderPrimitive(int color) {
        this.color = color;
    }

    public RenderPrimitive(ColorDef def) {
        this.color = CiMTexture.draw(def);
    }

    public RenderPrimitive(Collection<ColorDef> colors, EntityDef def) {
        ColorDef color = ColorDef.getColorByName(colors, def.color);
        if(color == null) {
        	log.warn("The color with name '{}' was not found. Using the default " +
        			"texture (which will be invisible on plain grass...)", def.color);
        }
        this.color = CiMTexture.draw(color);
    }


    /**
     * @return the color
     */
    public int getColor() {
        return color;
    }

}
