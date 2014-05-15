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
package de.nx42.maps4cim.gui.comp;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class PanelImage extends JPanel {

    private static final long serialVersionUID = -6178967533972454775L;

    protected Image image;
    protected boolean tiling;

    public PanelImage(Image image, boolean tiling) {
        this.image = image;
        this.tiling = tiling;
    };

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (tiling) {
            int iw = image.getWidth(this);
            int ih = image.getHeight(this);
            if (iw > 0 && ih > 0) {
                for (int x = 0; x < getWidth(); x += iw) {
                    for (int y = 0; y < getHeight(); y += ih) {
                        g.drawImage(image, x, y, iw, ih, this);
                    }
                }
            }
        } else {
            int x = (this.getWidth() - image.getWidth(this)) / 2;
            int y = (this.getHeight() - image.getHeight(this)) / 2;
            g.drawImage(image, x, y, getWidth(), getHeight(), this);
        }
    }

    /**
     * @return the image
     */
    public Image getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(Image image) {
        this.image = image;
        this.repaint();
    }

    /**
     * @return the tiling
     */
    public boolean isTiling() {
        return tiling;
    }

    /**
     * @param tiling the tiling to set
     */
    public void setTiling(boolean tiling) {
        this.tiling = tiling;
        this.repaint();
    }



}
