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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.ResourceLoader;

public class Components {

    private static final Logger log = LoggerFactory.getLogger(Components.class);
    
    protected static final String resIcoFolder = ResourceLoader.addBasePath("ico/");
    protected static final String resIco16 = resIcoFolder + "maps4cim-16.png";
    protected static final String resIco32 = resIcoFolder + "maps4cim-32.png";
    protected static final String resIco48 = resIcoFolder + "maps4cim-48.png";
    protected static final String resIco64 = resIcoFolder + "maps4cim-64.png";
    protected static final String resIco128 = resIcoFolder + "maps4cim-128.png";
    
    protected static final List<Image> icons = null;


    public static void setPreferredWidth(JComponent c, int width) {
        c.setPreferredSize(new Dimension(width, c.getPreferredSize().height));
    }

    public static void setPreferredHeight(JComponent c, int height) {
        c.setPreferredSize(new Dimension(c.getPreferredSize().width, height));
    }
    
    public static void setIconImages(Window w) {
        // load image icon list (16, 32, 48, 64, 128px)
        w.setIconImages(getIconImages());
    }
    
    protected static List<Image> getIconImages() {
        if(icons != null) {
            return icons;
        } else {
            try {
                List<Image> icons = new ArrayList<Image>(5);
                icons.add(ResourceLoader.getImageFromResource(resIco16));
                icons.add(ResourceLoader.getImageFromResource(resIco32));
                icons.add(ResourceLoader.getImageFromResource(resIco48));
                icons.add(ResourceLoader.getImageFromResource(resIco64));
                icons.add(ResourceLoader.getImageFromResource(resIco128));
                return icons;
            } catch (IllegalArgumentException e) {
                log.warn("Could not load icon images", e);
                return null;
            }
        }
    }

}
