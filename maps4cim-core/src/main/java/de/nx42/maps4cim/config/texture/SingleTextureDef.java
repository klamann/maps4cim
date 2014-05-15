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
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.oval.constraint.AssertValid;


@XmlRootElement(name = "single")
public class SingleTextureDef extends TextureDef {

    /**
     * The single ground texture to use for the entire map
     */
    @XmlElement(name = "ground")
    @AssertValid
    public ColorDef ground;     // defaults to grass

    /**
     * @return the ground texture color
     */
    public ColorDef getGround() {
        return ground == null ? new ColorDef() : ground;
    }
    
}
