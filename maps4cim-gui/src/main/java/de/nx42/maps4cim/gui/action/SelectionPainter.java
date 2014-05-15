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
package de.nx42.maps4cim.gui.action;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Collection;

import javax.swing.JLabel;

import org.jdesktop.swingx.painter.Painter;

import de.nx42.maps4cim.gui.util.Fonts;

/**
 * Actually draws the elements defined by the SelectionAdapter
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class SelectionPainter implements Painter<Object> {
    
	protected static final Color colorSelectionFill = new Color(128, 192, 255, 48);
	protected static final Color colorSelectionFrame = new Color(0, 0, 255, 80);
	protected static final Color colorAttribBG = new Color(255, 255, 255, 192);
	protected static final Font font = Fonts.select(Font.PLAIN, 11, "Tahoma", "Geneva", "Arial", Font.SANS_SERIF);

	protected static final String attributionString = "<html>© <font color=\"#0000FF\"><u>OpenStreetMap</u></font> contributors</html>";
	protected static final JLabel attributionLabel = new JLabel(attributionString);
	static {
	    attributionLabel.setFont(font);
	    attributionLabel.setSize(attributionLabel.getPreferredSize());
	}

	private SelectionAdapter adapter;

	/**
	 * @param adapter the selection adapter
	 */
	public SelectionPainter(SelectionAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void paint(Graphics2D g, Object t, int width, int height) {
		// draw area rectangle
		Rectangle rc = adapter.getRectangle();
		if (rc != null) {
			g.setColor(colorSelectionFrame);
			g.draw(rc);
			g.setColor(colorSelectionFill);
			g.fill(rc);
		}

		// draw the crossed lines inside the rectangle
		Collection<Line2D> lines = adapter.getLines();
		if (lines != null) {
			g.setColor(colorSelectionFrame);
			for (Line2D line : lines) {
				g.draw(line);
			}
		}
		
		// OSM Attribution
		Dimension d = attributionLabel.getPreferredSize();
		BufferedImage bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);

		g.setPaint(colorAttribBG);
        g.fillRect(width - bi.getWidth() - 12, height - bi.getHeight() - 4, bi.getWidth() + 12, bi.getHeight() + 4);
		
		Graphics gImg = bi.createGraphics();
		gImg.setColor(Color.black);
        attributionLabel.paint(gImg);
        
        g.drawImage(bi, width - bi.getWidth() - 6, height - bi.getHeight() - 2, null);
		
	}
	
	protected static boolean isOsmLinkClicked(Dimension panelSize, Point clicked) {
	    return clicked.y > (panelSize.height - attributionLabel.getHeight() - 4)   // Y
	        && clicked.x > (panelSize.width - attributionLabel.getWidth() - 12);   // X
	}
	
}