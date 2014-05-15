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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;

/**
 * An Image file is used as a ground texture. The colors of the image are
 * translated to the limited amount of available ground textures.
 * Not all colors can be translated very well, but the result will be well
 * recognizable.
 * 
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@XmlRootElement(name = "image")
public class ImageDef extends TextureDef {

    @XmlAttribute(name = "file")
    @NotNull(message="Image file path must be defined")
    @NotBlank(message="Image file path must not be empty")
    public String imageFilePath;

    @XmlElement(name = "black")
    @AssertValid
    public ColorDef blackTranslation;

    @XmlElement(name = "white")
    @AssertValid
    public ColorDef whiteTranslation;

    @XmlElement(name = "red")
    @AssertValid
    public ColorDef redTranslation;

    @XmlElement(name = "green")
    @AssertValid
    public ColorDef greenTranslation;

    @XmlElement(name = "blue")
    @AssertValid
    public ColorDef blueTranslation;


    /**
     * Set all undefined colors to the default value
     */
    public void fillMissingColors() {

        // BW

        if(blackTranslation == null) {
            blackTranslation = new ColorDef() {{
                name = "black";
                roughGrass = 0.7;
                mud = 0.3;
            }};
        }
        if(whiteTranslation == null) {
            whiteTranslation = new ColorDef() {{
                name = "white";
                dirt = 1.0;
            }};
        }

        // RGB

        if(redTranslation == null) {
            redTranslation = new ColorDef() {{
                name = "red";
                roughGrass = 0.7;
                mud = 0.3;
            }};
        }
        if(greenTranslation == null) {
            greenTranslation = new ColorDef() {{
                name = "green";
                grass = 0.5;
            }};
        }
        if(blueTranslation == null) {
            blueTranslation = new ColorDef() {{
                name = "blue";
                mud = 1.0;
            }};
        }

    }

}
