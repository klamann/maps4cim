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
package de.nx42.maps4cim.map.texture;

import static de.nx42.maps4cim.util.math.MathExt.roundf;

import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.map.TextureMap;
import de.nx42.maps4cim.map.ex.TextureProcessingException;
import de.nx42.maps4cim.map.texture.osm.EntityConverter;
import de.nx42.maps4cim.map.texture.osm.OverpassBridge;
import de.nx42.maps4cim.map.texture.osm.RenderContainer;
import de.nx42.maps4cim.map.texture.osm.SimpleOsmDump;
import de.nx42.maps4cim.map.texture.osm.TileRenderer;
import de.nx42.maps4cim.util.gis.Area;


public class OsmTexture extends TextureMap {

    private static final Logger log = LoggerFactory.getLogger(TextureMap.class);


    protected Config config;
    protected Area bounds;


    public OsmTexture(Config config) {
        log.debug("Using OpenStreetMap as source for the map's texture");
        this.config = config;
        this.bounds = Area.of(config.bounds);
    }


    @Override
    public int[][] generateTexture() throws TextureProcessingException {

        // just draw some grass, if no texture is defined
        try {
            if(config.texture.entities.size() < 1) {
                return fallBackToGrass();
            }
        } catch(Exception e) {
            return fallBackToGrass();
        }

    	log.info("The ground textures will be generated for an area of {}x{}km, " +
                "with the center at ({}). Data source: OpenStreetMap via Overpass API.",
                roundf(bounds.getWidthKm()), roundf(bounds.getHeightKm()),
                bounds.getCenter().toString());

    	SimpleOsmDump osm = retrieveOsmData();
    	Raster ras = drawImage(osm);

    	log.debug("Converting rendered image to native CiM2-Texture data");
        return convertImage(ras);
    }

    protected int[][] fallBackToGrass() {
        log.info("No data has been defined for the ground texture, I'll just draw some grass...");
        SingleTexture st = new SingleTexture();
        return st.generateTexture();
    }

    protected SimpleOsmDump retrieveOsmData() throws TextureProcessingException {
    	// osm.xml: download / get from cache
        OverpassBridge ob = new OverpassBridge(this.config);
        File osmxml = ob.getResult();

        // osmosis / parse
        log.debug("Parsing OSM XML with a little help from Osmosis API");
        return SimpleOsmDump.readOsmXml(osmxml);
    }

    protected Raster drawImage(SimpleOsmDump osm) throws TextureProcessingException {

        // prepare for rendering
        log.debug("Preparing OSM data for rendering");
        EntityConverter ec = new EntityConverter(config, osm);
        List<RenderContainer> rcs = ec.buildRenderContainers();

        // render image
        log.debug("Rendering {} layers of OSM data", rcs.size());
        TileRenderer ir = new TileRenderer(bounds);
        for (RenderContainer rc : rcs) {
            ir.draw(rc);
        }

        // print resulting image (debugging)
        //ir.printResult();

        // return image raster
        return ir.getRaster();

    }

    protected static int[][] convertImage(Raster ras) {

        final int[] pixels = ((DataBufferInt) ras.getDataBuffer()).getData();
        final int width = ras.getWidth();
        final int height = ras.getHeight();

        int[][] result = new int[height][width];
        for (int y = 0; y < edgeLength; y++) {
            for (int x = 0; x < edgeLength; x++) {

                int argb = pixels[(edgeLength - 1 - x)*edgeLength + y];

                int r = (argb) & 0xFF;
                int g = (argb>>8) & 0xFF;
                int b = (argb>>16) & 0xFF;
                int a = (argb>>24) & 0xFF;

                float alpha = a / 255f;
                int texture = CiMTexture.draw((int) (r*alpha), (int) (g*alpha), (int) (b*alpha));

                result[y][x] = texture;
            }
        }

        return result;
    }

}
