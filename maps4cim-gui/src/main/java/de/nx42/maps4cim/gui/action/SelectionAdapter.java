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

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.nx42.maps4cim.util.gis.Area;
import de.nx42.maps4cim.util.gis.Coordinate;
import de.nx42.maps4cim.util.gis.UnitOfLength;

/**
 * Updates the current selection view on the map. Draws a rectangle around the
 * specified area
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class SelectionAdapter extends MouseAdapter {

	protected JXMapViewer jxm;

	/** the coordinate of the last click */
	Point2D cursor = null;
	/** the current extent (edge length) of the map */
	protected double extent = 8;
	/** the area that is defined by center (click) and extent */
	Area ar = null;


	public SelectionAdapter(JXMapViewer jxm) {
		this.jxm = jxm;
	}

	/**
	 * Updates the current extent value. As this class does not watch
	 * it's caller, the extent has to be updated from outside on change
	 * @param extent the extent of the map in km
	 */
	public void updateExtent(double extent) {
		if(extent != this.extent) {
			this.extent = extent;
			if(cursor != null && extent > 0 && extent < 2000) {
				updateArea(cursor.getX(), cursor.getY());
			}
		}
	}

	/**
	 * Updates the current center to the specified value.
	 * @param lat the latitude
	 * @param lon the longitude
	 */
	public void updateCenter(double lat, double lon) {
		double fixLat = lat > 90 ? 90 : lat < -90 ? -90 : lat;
		double fixLon = lon > 180 ? 180 : lon < -180 ? -180 : lon;
		Point2D p = geoToPoint(fixLat, fixLon);
		updateArea(p.getX(), p.getY());
	}


	@Override
	public void mousePressed(MouseEvent evt) {
		if (!SwingUtilities.isRightMouseButton(evt))
			return;
		updateArea(evt.getX(), evt.getY());
	}

	@Override
	public void mouseDragged(MouseEvent evt) {
		if (!SwingUtilities.isRightMouseButton(evt))
			return;
		updateArea(evt.getX(), evt.getY());
	}

	/**
	 * Updates the area definition with the new center at the specified screen
	 * coordinates
	 * @param x the x coordiante (relative to current screen)
	 * @param y the y coordinate (relative to current screen)
	 */
	protected void updateArea(double x, double y) {
		// get current position
		this.cursor = new Point2D.Double(x, y);

		// calculate area
		GeoPosition gps = jxm.convertPointToGeoPosition(cursor);
		ar = new Area(new Coordinate(gps.getLatitude(), gps.getLongitude()),
				extent, UnitOfLength.KILOMETER);

		// repaint
		jxm.repaint();
	}

	/**
	 * @return the selection rectangle
	 */
	public Rectangle getRectangle() {
		if(ar != null) {
			Point2D p1 = geoToPoint(ar.getMinLat(), ar.getMinLon());
			Point2D p2 = geoToPoint(ar.getMaxLat(), ar.getMaxLon());

			int x1 = (int) Math.min(p1.getX(), p2.getX());
			int y1 = (int) Math.min(p1.getY(), p2.getY());
			int x2 = (int) Math.max(p1.getX(), p2.getX());
			int y2 = (int) Math.max(p1.getY(), p2.getY());

			return new Rectangle(x1, y1, x2 - x1, y2 - y1);
		}
		return null;
	}

	/**
	 * @return the lines that define the center of the rectangle
	 */
	public Collection<Line2D> getLines() {
		if(ar != null) {
			Point2D p1 = geoToPoint(ar.getMinLat(), ar.getMinLon());
			Point2D p2 = geoToPoint(ar.getMaxLat(), ar.getMaxLon());

			int x1 = (int) Math.min(p1.getX(), p2.getX());
			int y1 = (int) Math.min(p1.getY(), p2.getY());
			int x2 = (int) Math.max(p1.getX(), p2.getX());
			int y2 = (int) Math.max(p1.getY(), p2.getY());

			List<Line2D> lines = new LinkedList<Line2D>();
			lines.add(new Line2D.Double(x1, y1, x2, y2));
			lines.add(new Line2D.Double(x1, y2, x2, y1));

			return lines;
		}
		return null;
	}

	/**
	 * Transforms a geoposition to a point on the current screen
	 * @param lat the latitude
	 * @param lon the longitude
	 * @return the Point of these values relative to the current screen
	 */
	protected Point2D geoToPoint(double lat, double lon) {
		return jxm.convertGeoPositionToPoint(new GeoPosition(lat, lon));
	}

}
