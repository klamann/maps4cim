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
package de.nx42.maps4cim.config.header;

import java.io.File;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "meta")
public class HeaderDef {

    /** the name of the map (CiM 2 might still prefer the file name over this) */
    @XmlAttribute(name = "name")
    public String name;

    /** date when the map has been created */
    @XmlAttribute(name = "date-created")
    public Date created;

    /** date when the map has been last modified */
    @XmlAttribute(name = "date-modified")
    public Date modified;

    @XmlAttribute(name = "style")
    public BuildingSet buildingSet;

    // not supported by config, use header editor in GUI instead...
    //@XmlAttribute(name = "preview-image-path")
    //public String previewImagePath;

    /**
     * Create a new header definition from the specified file.
     * Currently, this method does only parse the file name and sets everything
     * else to the defaults
     * @param f the file to create a header defintion from
     * @return a new header definition for the specified file
     */
    public static HeaderDef forFile(final File f) {
        return new HeaderDef() {{
            name = getFileNameOnly(f);
            created = new Date();
            modified = created;
        }};
    }

    /**
     * Returns the name of the specified file without extension
     * @param f the file to get the name of
     * @return the file name (without extension)
     */
    public static String getFileNameOnly(File f) {
        return f.getName().replaceFirst("[.][^.]+$", "");
    }

    @XmlType
    @XmlEnum(String.class)
    public enum BuildingSet {
        /** the (default) american building set */
        @XmlEnumValue("america") AMERICAN("American"),
        /** the european building set (available as DLC) */
        @XmlEnumValue("europe")  EUROPEAN("European");

        protected String name;

        private BuildingSet(String name) {
            this.name = name;
        }

        public static BuildingSet getDefault() {
            return AMERICAN;
        }

        @Override
        public String toString() {
            return name;
        }

        public String getName() {
            return name;
        }
    }

}
