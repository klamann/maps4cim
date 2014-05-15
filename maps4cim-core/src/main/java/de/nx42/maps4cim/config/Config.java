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
package de.nx42.maps4cim.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotNull;
import de.nx42.maps4cim.config.bounds.BoundsDef;
import de.nx42.maps4cim.config.bounds.BoundsWrapper;
import de.nx42.maps4cim.config.bounds.CenterDef;
import de.nx42.maps4cim.config.bounds.CenterDef.Unit;
import de.nx42.maps4cim.config.header.HeaderDef;
import de.nx42.maps4cim.config.relief.ReliefDef;
import de.nx42.maps4cim.config.relief.ReliefWrapper;
import de.nx42.maps4cim.config.relief.SrtmDef;
import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.TextureDef;
import de.nx42.maps4cim.config.texture.TextureWrapper;
import de.nx42.maps4cim.config.texture.osm.EntityDef;

/**
 * The root configuration element. Holds the definition of
 * - the boundaries of the map
 * - the type of relief to use
 * - the type of textures to draw
 * - the map's header
 * 
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlRootElement(name = "maps4cim")
public class Config {

    /**
     * Wrapper for the definition of the map's boundaries
     */
    @XmlElement(name = "bounds")
    @NotNull(message = BoundsWrapper.nullError)
    @AssertValid
    public BoundsWrapper bounds;

    /**
     * Wrapper for the definition of the relief of the map (elevation data)
     */
    @XmlElement(name = "relief")
    @AssertValid
    public ReliefWrapper relief;

    /**
     * Wrapper for the definition of ground textures to draw
     */
    @XmlElement(name = "texture")
    @AssertValid
    public TextureWrapper texture;

    /**
     * the header configuration
     */
    @XmlElement(name = "meta")
    @AssertValid
    public HeaderDef header;

    // ----- getters & setters -----

    /**
     * @return the boundaries of the map
     */
    @XmlTransient
    public BoundsDef getBoundsTrans() {
        return bounds.value;
    }

    /**
     * @param bounds the boundaries of the map
     */
    public void setBoundsTrans(final BoundsDef bounds) {
        this.bounds = new BoundsWrapper() {{
            value = bounds;
        }};
    }

    /**
     * @return the relief configuration
     */
    @XmlTransient
    public ReliefDef getReliefTrans() {
        return relief.value;
    }

    /**
     * @param relief the relief configuration to set
     */
    public void setReliefTrans(final ReliefDef relief) {
        this.relief = new ReliefWrapper() {{
            value = relief;
        }};
    }

    /**
     * @return the texture configuration
     */
    @XmlTransient
    public TextureDef getTextureTrans() {
        return texture.value;
    }

    /**
     * @param texture the texture configuration to set
     */
    public void setTextureTrans(final TextureDef texture) {
        this.texture = new TextureWrapper() {{
            value = texture;
        }};
    }

    /**
     * @return the header configuration
     */
    @XmlTransient
    public HeaderDef getHeader() {
        return header;
    }

    /**
     * @param header the header configuration to set
     */
    public void setHeader(HeaderDef header) {
        this.header = header;
    }
    
    
    public static Config getMinimalConfig() {
        return new Config() {{
            bounds = new BoundsWrapper() {{
                value = new CenterDef() {{
                    centerLat = 51.4778;
                    centerLon = -0.0015;
                    extent = 8.0;
                    unit = Unit.KM;
                }};
            }};
            relief = new ReliefWrapper() {{
                value = new SrtmDef() {{
                    heightOffset = "auto";
                    heightScale = "auto";
                }};
            }};
            texture = new TextureWrapper() {{
                value = new OsmDef() {{
                    colors = ColorDef.getDefaults();
                    entities = EntityDef.getDefaults();
                }};
            }};
        }};
    }

}
