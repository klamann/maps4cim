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
package de.nx42.maps4cim.map.texture.osm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.nx42.maps4cim.map.texture.osm.primitives.Point;
import de.nx42.maps4cim.map.texture.osm.primitives.Polygon;
import de.nx42.maps4cim.map.texture.osm.primitives.Polyline;
import de.nx42.maps4cim.util.gis.Area;
import de.nx42.maps4cim.util.gis.Coordinate;
import de.nx42.maps4cim.util.java2d.Polygon2D;

public class TileRenderer {

    public static final int CiM2MapSize = 2048;

    protected final int width;
    protected final int height;
    protected Area area;

    protected BufferedImage bi;
    protected Graphics2D g2;

    public TileRenderer(Area area) {
        this(CiM2MapSize, CiM2MapSize, area);
    }

    public TileRenderer(int width, int height, Area area) {
        this.width = width;
        this.height = height;
        this.area = area;

        this.bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.g2 = bi.createGraphics();
        setHighQuality(g2);
    }


    public void draw(Polyline way) {
        List<Coordinate> coords = way.getNodes();
        if(coords.size() < 2)
            return;

        Iterator<Coordinate> it = coords.iterator();
        Coordinate c1 = it.next();
        Coordinate.RelativeCoord rc1 = c1.relativeWithinArea(area);

        Path2D path = new Path2D.Double();
        path.moveTo(rc1.x * width, rc1.y * height);

        while (it.hasNext()) {
            Coordinate c = it.next();
            Coordinate.RelativeCoord rc = c.relativeWithinArea(area);
            path.lineTo(rc.x * width, rc.y * height);
        }

        g2.setPaint(new Color(way.getColor(), false));
        g2.setStroke(new BasicStroke((float) way.getStrokeWidth()));
        g2.draw(path);
    }

    public void draw(Polygon polygon) {
        List<Coordinate> coords = polygon.getNodes();
        if(coords.size() < 2)
            return;

        Polygon2D poly = new Polygon2D();
        for (Coordinate c : coords) {
            Coordinate.RelativeCoord rc = c.relativeWithinArea(area);
            poly.addPoint((float) (rc.x * width), (float) (rc.y * height));
        }

        g2.setPaint(new Color(polygon.getColor(), false));
        g2.fill(poly);
        g2.draw(poly);
    }

    public void draw(Point point) {
    	Coordinate coord = point.getCoord();
    	Coordinate.RelativeCoord rc = coord.relativeWithinArea(area);

    	double centerX = rc.x * width;
    	double centerY = rc.y * height;
    	double cornerX = centerX - point.getRadius();
    	double cornerY = centerY - point.getRadius();

    	Ellipse2D circle = new Ellipse2D.Double();
    	circle.setFrameFromCenter(centerX, centerY, cornerX, cornerY);

        g2.setPaint(new Color(point.getColor(), false));
        g2.fill(circle);
    }

    @SuppressWarnings("unchecked")
	public void draw(RenderContainer rc) {
        switch (rc.type) {
            case POINT:
            	Collection<Point> points = (Collection<Point>) rc.getPrimitives();
                for (Point p : points) {
                    draw(p);
                }
                break;
            case POLYGON:
                Collection<Polygon> polys = (Collection<Polygon>) rc.getPrimitives();
                for (Polygon poly : polys) {
                    draw(poly);
                }
                break;
            case POLYLINE:
                Collection<Polyline> ways = (Collection<Polyline>) rc.getPrimitives();
                for (Polyline way : ways) {
                    draw(way);
                }
                break;
            default:
                throw new RuntimeException(String.format("Type %s not recognized", rc.type));
        }
    }

    // stores the resulting map (for debugging purposes)
//    public void printResult() {
//        try {
//            ImageIO.write(bi, "PNG", new File("target/image.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public Raster getRaster() {
        return bi.getRaster();
    }

    /**
     * Sets the rendering hints for the given Graphics2D object to the
     * highest quality that Java2D offers. Includes alpha interpolation,
     * antialiasing, color mixing accuracy as well as interpolation and
     * rendering quality in general.
     *
     * Not recommended for real time use, but for ahead of time rendering
     * these settings give a reasonable quality in a fair amount of time.
     *
     * @param g2 the graphics object to apply these settings on
     */
    public static void setHighQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
    }

}