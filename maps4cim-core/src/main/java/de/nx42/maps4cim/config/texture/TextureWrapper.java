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
package de.nx42.maps4cim.config.texture;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import net.sf.oval.constraint.AssertValid;
import de.nx42.maps4cim.config.texture.TextureDef.TextureDefNone;

/**
 * Wrapper for the definition of ground textures to draw
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class TextureWrapper {
    
    @XmlElements(value = {
            @XmlElement(name = "osm-file", type=OsmFileDef.class),
            @XmlElement(name = "osm", type=OsmDef.class),
            @XmlElement(name = "image", type=ImageDef.class),
            @XmlElement(name = "single", type=SingleTextureDef.class),
            @XmlElement(name = "none", type=TextureDefNone.class)
    })
    @AssertValid
    public TextureDef value;

    /**
     * @return the value
     */
    public TextureDef getValue() {
        if(value == null) {
            value = new TextureDefNone();
        }
        return value;
    }

}
