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
package de.nx42.maps4cim.gui;

import java.io.File;

import javax.swing.event.MouseInputListener;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.OSMTileFactoryInfo;
import org.jdesktop.swingx.input.CenterMapListener;
import org.jdesktop.swingx.input.PanKeyListener;
import org.jdesktop.swingx.input.PanMouseInputListener;
import org.jdesktop.swingx.input.ZoomMouseWheelListenerCursor;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.LocalResponseCache;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

import de.nx42.maps4cim.gui.action.SelectionAdapter;
import de.nx42.maps4cim.gui.action.SelectionPainter;
import de.nx42.maps4cim.map.Cache;

public class MapViewerFactory {


	private JXMapViewer jxm;
	private SelectionAdapter selection;

	public MapViewerFactory() {
		TileFactory tf = getCachedTileFactory();
		this.jxm = getMapViewer(tf);
		addActionListeners(jxm);
		addSelectionPainters(jxm);
	}

	public JXMapViewer getMapViewer() {
		return this.jxm;
	}

	public SelectionAdapter getSelectionAdapter() {
		return this.selection;
	}

	public void setLocation(GeoPosition gps) {
		this.jxm.setAddressLocation(gps);
	}

	public static JXMapViewer build() {
		MapViewerFactory mvf = new MapViewerFactory();
		return mvf.getMapViewer();
	}

	public static JXMapViewer build(GeoPosition gps) {
		MapViewerFactory mvf = new MapViewerFactory();
		mvf.setLocation(gps);
		return mvf.getMapViewer();
	}


	protected TileFactory getCachedTileFactory() {
		// Create a TileFactoryInfo for OpenStreetMap
		TileFactoryInfo info = new OSMTileFactoryInfo();
		DefaultTileFactory tileFactory = new DefaultTileFactory(info);
		tileFactory.setThreadPoolSize(8);

		// Setup local file cache
		File cache = new Cache().getCacheDir();
		LocalResponseCache.installResponseCache(info.getBaseURL(), cache, false);

		return tileFactory;
	}

	protected JXMapViewer getMapViewer(TileFactory tf) {
		GeoPosition freising = new GeoPosition(48.401, 11.744);
		return getMapViewer(tf, freising);
	}

	protected JXMapViewer getMapViewer(TileFactory tf, GeoPosition gps) {
		JXMapViewer jxm = new JXMapViewer();
		jxm.setTileFactory(tf);
		jxm.setZoom(10);
		jxm.setAddressLocation(gps);
		return jxm;
	}

	protected void addActionListeners(JXMapViewer jxm) {
		MouseInputListener mia = new PanMouseInputListener(jxm);
		jxm.addMouseListener(mia);
		jxm.addMouseMotionListener(mia);

		jxm.addMouseListener(new CenterMapListener(jxm));

		jxm.addMouseWheelListener(new ZoomMouseWheelListenerCursor(jxm));

		jxm.addKeyListener(new PanKeyListener(jxm));
	}

	protected void addSelectionPainters(JXMapViewer jxm) {
		SelectionAdapter sa = new SelectionAdapter(jxm);
		SelectionPainter sp = new SelectionPainter(sa);
		jxm.addMouseListener(sa);
		jxm.addMouseMotionListener(sa);
		jxm.setOverlayPainter(sp);
		this.selection = sa;
	}

}
