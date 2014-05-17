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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import de.nx42.maps4cim.util.math.Statistics;

public class PanelBoxPlot extends JPanel {

    private static final long serialVersionUID = 2651638879424578755L;

    protected static final int scaleLeftMargin = 55;
    protected static final int scaleRightMargin = 25;
    protected static final int scaleYMargin = 35;
    protected static final int plotLeftMargin = scaleLeftMargin + 30;
    protected static final int plotRightMargin = scaleRightMargin + 30;
    protected static final int plotYMargin = scaleYMargin + 20;
    protected static final int numScales = 5;
    protected static final int ubyteMax = 256;
    protected static final int ushortMax = 65536;

    protected static Font font = new Font("SansSerif", Font.PLAIN, 10);

    protected Statistics stats;
    protected Measurements m = new Measurements();
    protected boolean calculating = false;

    public PanelBoxPlot() {
        super();
    }

    public PanelBoxPlot(Statistics stats) {
        super();
        this.stats = stats;
    }

    /**
     * @param stats the stats to set
     */
    public void setStats(Statistics stats) {
        this.stats = stats;
        repaint();
    }

    public void resetStats() {
        this.stats = null;
        repaint();
    }

    public void setCalculating(boolean calculating) {
        this.calculating = calculating;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // update measurements
        Dimension d = this.getSize();
        Graphics2D g2d = (Graphics2D) g;
        m.update(g, d);

        // set defaults
        g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // paint background
        paintBackground(g2d);

        // draw the plot
        drawPlot(g);
    }

    protected void paintBackground(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, new Color(150, 100, 50), m.d.width, m.d.height, Color.white));
        g.fillRect(0, 0, m.d.width, m.d.height);
    }

    public void drawPlot(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        drawAxis(g2d);

        if(stats != null) {
            // stats available, draw plot
            drawScale(g2d);
            drawQuartileBox(g2d);
            drawMedian(g2d);
            drawWhiskers(g2d);
        } else if(calculating) {
            // waiting for data
            drawCenterText(g2d, "calculating...");
        } else {
            // no data, nothing to come
            drawCenterText(g2d, "no data available");
        }
    }

    /**
     * draw the x and y axis (without scales)
     * @param g Graphics object from current context
     * ({@link PanelBoxPlot#paintComponent(Graphics)})
     */
    protected void drawAxis(Graphics2D g) {
        g.setColor(Color.black);

        // draw x and y axis
        g.drawLine(scaleLeftMargin, scaleYMargin, scaleLeftMargin, scaleYMargin + m.scaleHeight);
        g.drawLine(scaleLeftMargin, scaleYMargin + m.scaleHeight, scaleLeftMargin + m.scaleWidth, scaleYMargin + m.scaleHeight);
    }

    /**
     * draw scales on the axis
     * @param g Graphics object from current context
     * ({@link PanelBoxPlot#paintComponent(Graphics)})
     */
    protected void drawScale(Graphics2D g) {
        for (int i = 0; i < numScales; i++) {
            double part = (1 / (numScales - 1.0));
            int y = plotYMargin + (int) (m.plotHeight * part * i);
            String scale = m.df.format(m.plotMax - i * part * m.plotRange);
            g.drawLine(scaleLeftMargin - 5, y, scaleLeftMargin, y);
            g.drawString(scale, scaleLeftMargin - m.fm.stringWidth(scale) - 10, y + 4);
        }
    }

    /**
     * draw rect between lower and upper quartile
     * @param g Graphics object from current context
     * ({@link PanelBoxPlot#paintComponent(Graphics)})
     */
    protected void drawQuartileBox(Graphics2D g) {
        g.drawRect(plotLeftMargin, m.yUpperQuartile, m.plotWidth, m.iqrHeight);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g.setColor(new Color(205, 125, 50));
        g.fillRect(plotLeftMargin + 1, m.yUpperQuartile + 1, m.plotWidth - 1, m.iqrHeight - 1);
    }

    /**
     * draw median line
     * @param g Graphics object from current context
     * ({@link PanelBoxPlot#paintComponent(Graphics)})
     */
    protected void drawMedian(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(Color.black);
        g.drawLine(plotLeftMargin, m.yMedian, plotLeftMargin + m.plotWidth, m.yMedian);
    }

    /**
     * draw center line and whiskers
     * @param g Graphics object from current context
     * ({@link PanelBoxPlot#paintComponent(Graphics)})
     */
    protected void drawWhiskers(Graphics2D g) {
        g.drawLine(m.xCenter, m.yUpperQuartile, m.xCenter, m.yTop);
        g.drawLine(m.xCenter, m.yLowerQuartile, m.xCenter, m.yBottom);
        g.drawLine(plotLeftMargin, m.yTop, plotLeftMargin + m.plotWidth, m.yTop);
        g.drawLine(plotLeftMargin, m.yBottom, plotLeftMargin + m.plotWidth, m.yBottom);
    }

    /**
     * Draws a line of text in the center of the coordinate system
     * @param g Graphics object from current context
     * ({@link PanelBoxPlot#paintComponent(Graphics)})
     * @param s the String to draw
     */
    protected void drawCenterText(Graphics2D g, String s) {
        g.drawString(s,
                scaleLeftMargin + m.scaleWidth / 2 - m.fm.stringWidth(s) / 2,
                scaleYMargin + m.scaleHeight / 2 + 4);
    }

    /**
     * Convert from data value to y coordinate
     * @param y the value used as input
     * @return the y coordinate of this value
     */
    public int getYCoord(double y) {
        int plotHeight = getSize().height - 2 * plotYMargin;
        return (int) (((m.plotMax - y) / (m.plotRange)) * plotHeight + plotYMargin + 0.5);
    }

    /**
     * This class holds some data used to paint the boxplots used in
     * {@link PanelBoxPlot}. Not what you think ;)
     */
    protected class Measurements {

        protected final DecimalFormat dfFloat = new DecimalFormat("0.####");
        protected final DecimalFormat dfInt = new DecimalFormat("0");
        protected final DecimalFormat dfElse = new DecimalFormat("0.###E0");

        Dimension d;
        FontMetrics fm;
        DecimalFormat df = dfElse;

        double plotMax;
        double plotMin;
        double plotRange;
        int scaleWidth;
        int scaleHeight;
        int plotWidth;
        int plotHeight;

        double upperWhisker;
        double lowerWhisker;

        int yTop;
        int yBottom;
        int xCenter;

        int yUpperQuartile;
        int yLowerQuartile;
        int iqrHeight;
        int yMedian;


        public void update(Graphics g, Dimension d) {
            this.d = d;
            fm = g.getFontMetrics(font);
            g.setFont(font);

            // sizes
            scaleWidth = d.width - scaleLeftMargin - scaleRightMargin;
            scaleHeight = d.height - 2 * scaleYMargin;
            plotWidth = d.width - plotLeftMargin - plotRightMargin;
            plotHeight = d.height - 2 * plotYMargin;

            // data depending on stats
            if(stats != null) {
                // get min and max value (for chart)
                if(stats.getMax() <= 1.0 && stats.getMin() >= -1.0) {
                    df = dfFloat;
                    plotMax = 1;
                    plotMin = stats.getMin() >= 0.0 ? 0 : -1;
                } else if(stats.getMax() <= ubyteMax && stats.getMin() >= -ubyteMax) {
                    df = dfInt;
                    plotMax = 256;
                    plotMin = stats.getMin() >= 0.0 ? 0 : -256;
                } else if(stats.getMax() <= ushortMax && stats.getMin() >= -ushortMax) {
                    df = dfInt;
                    plotMax = ushortMax;
                    plotMin = stats.getMin() >= 0.0 ? 0 : -ushortMax;
                } else {
                    df = dfElse;
                    plotMax = stats.getMax() + stats.getRange() * 0.1;
                    plotMin = stats.getMin() - stats.getRange() * 0.1;
                }
                plotRange = plotMax - plotMin;

                updateStatsData();
            }
        }

        public void setCustomRange(double max, double min) {
            plotMax = max;
            plotMin = min;
            plotRange = max - min;
            if(stats != null) {
                updateStatsData();
            }
        }

        protected void updateStatsData() {
            // calculate box plot values
            upperWhisker = Math.min(stats.getMax(), 1.5 * stats.getIqr() + stats.getUpperQuartile());
            lowerWhisker = Math.max(stats.getMin(), stats.getLowerQuartile() - 1.5 * stats.getIqr());

            yTop = getYCoord(upperWhisker);
            yBottom = getYCoord(lowerWhisker);
            xCenter = plotLeftMargin + plotWidth / 2;

            yUpperQuartile = getYCoord(stats.getUpperQuartile());
            yLowerQuartile = getYCoord(stats.getLowerQuartile());
            iqrHeight = yLowerQuartile - yUpperQuartile;
            yMedian = getYCoord(stats.getMedian());
        }

    }

}