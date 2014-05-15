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
package de.nx42.maps4cim.config.bounds;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotNull;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlRootElement(name = "bounds")
public class BoundsWrapper {

    public static final String nullError = "Boundaries are not defined!";
    
    /**
     * the boundaries of the map (either explicit bounds in latitude and
     * longitude or just a center with extent)
     */
    @XmlElements({
        @XmlElement(name="bbox", type=BBoxDef.class, required=true),
        @XmlElement(name="center", type=CenterDef.class, required=true)
    })
    @NotNull(message = nullError)
    @AssertValid
    public BoundsDef value;

}
