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

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import de.nx42.maps4cim.util.math.Statistics;

public class PanelBoxPlotWorld extends PanelBoxPlot {

    private static final long serialVersionUID = 5922400551252280469L;

    protected static final DecimalFormat df = new DecimalFormat("0");
    protected static final int plotMax = 1000;
    protected static final int plotMin = -1000;


    public PanelBoxPlotWorld() {
        super();
    }

    public PanelBoxPlotWorld(Statistics stats) {
        super(stats);
    }

    /* (non-Javadoc)
     * @see de.nx42.maps4cim.gui.comp.PanelBoxPlot#paintBackground(java.awt.Graphics2D)
     */
    @Override
    protected void paintBackground(Graphics2D g) {
        int yCenter = scaleYMargin + m.scaleHeight / 2;
        int ySnow = yCenter - m.scaleHeight / 4;
        int yGrass = yCenter - m.scaleHeight / 50;
        int ySand = yCenter + m.scaleHeight / 50;

        // sky
        g.setPaint(new GradientPaint(0, 0, new Color(200, 255, 255), m.d.width, scaleYMargin, new Color(233, 255, 255)));
        g.fillRect(0, 0, m.d.width, scaleYMargin);

        // snow
        g.setPaint(new GradientPaint(0, scaleYMargin, new Color(245, 246, 249), m.d.width, ySnow, new Color(221, 225, 237)));
        g.fillRect(0, scaleYMargin, m.d.width, ySnow);

        // grass
        g.setPaint(new GradientPaint(0, ySnow, new Color(102, 93, 40), m.d.width, yGrass, new Color(95, 114, 33)));
        g.fillRect(0, ySnow, m.d.width, yGrass);

        // sand
        g.setPaint(new GradientPaint(0, yGrass, new Color(106, 91, 38), m.d.width, ySand, new Color(103, 67, 39)));
        g.fillRect(0, yGrass, m.d.width, ySand);

        // water
        g.setPaint(new GradientPaint(0, ySand, new Color(108, 150, 175), m.d.width, m.d.height, new Color(44, 62, 70)));
        g.fillRect(0, ySand, m.d.width, m.d.height);

    }

    /* (non-Javadoc)
     * @see de.nx42.maps4cim.gui.comp.PanelBoxPlot#drawPlot(java.awt.Graphics)
     */
    @Override
    public void drawPlot(Graphics g) {
        setFixedPlotSize();
        super.drawPlot(g);
    }

    protected void setFixedPlotSize() {
        m.df = df;
        m.setCustomRange(plotMax, plotMin);
    }



}
