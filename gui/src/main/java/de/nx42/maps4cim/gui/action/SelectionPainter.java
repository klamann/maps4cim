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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Collection;

import org.jdesktop.swingx.painter.Painter;

/**
 * Actually draws the elements defined by the SelectionAdapter
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class SelectionPainter implements Painter<Object>
{
	private Color fillColor = new Color(128, 192, 255, 48);
	private Color frameColor = new Color(0, 0, 255, 80);

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
			g.setColor(frameColor);
			g.draw(rc);
			g.setColor(fillColor);
			g.fill(rc);
		}

		// draw the crossed lines inside the rectangle
		Collection<Line2D> lines = adapter.getLines();
		if (lines != null) {
			g.setColor(frameColor);
			for (Line2D line : lines) {
				g.draw(line);
			}
		}

	}
}