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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.nx42.maps4cim.gui.MainWindow;

/**
 * Mouslistener for the map center selection.
 * Sets the center of the map to the position of the cursor when a right mouse
 * button click occurs.
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class CenterOnClickListener extends MouseAdapter {

	private MainWindow main;
	private JXMapViewer jxm;


	public CenterOnClickListener(MainWindow main, JXMapViewer viewer) {
		this.main = main;
		this.jxm = viewer;
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		boolean right = SwingUtilities.isRightMouseButton(evt);
		if (right) {
			setCenter(evt);
		}
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		boolean right = SwingUtilities.isRightMouseButton(evt);
		if (right) {
			setCenter(evt);
		}
	}

	protected void setCenter(MouseEvent evt) {
		Point2D cursor = new Point2D.Double(evt.getX(), evt.getY());
		GeoPosition gps = jxm.convertPointToGeoPosition(cursor);
		main.setCenter(gps.getLatitude(), gps.getLongitude());
	}
}