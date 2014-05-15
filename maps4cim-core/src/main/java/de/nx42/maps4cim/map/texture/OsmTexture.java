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

import static de.nx42.maps4cim.util.math.MathExt.rounds;

import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.File;
import java.util.List;

import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.OsmFileDef;
import de.nx42.maps4cim.map.TextureMap;
import de.nx42.maps4cim.map.ex.TextureProcessingException;
import de.nx42.maps4cim.map.texture.data.Texture;
import de.nx42.maps4cim.map.texture.osm.EntityConverter;
import de.nx42.maps4cim.map.texture.osm.OverpassBridge;
import de.nx42.maps4cim.map.texture.osm.RenderContainer;
import de.nx42.maps4cim.map.texture.osm.SimpleOsmDump;
import de.nx42.maps4cim.map.texture.osm.TileRenderer;
import de.nx42.maps4cim.util.gis.Area;


public class OsmTexture extends TextureMap {

    private static final Logger log = LoggerFactory.getLogger(TextureMap.class);

    protected OsmDef osm;
    protected Area bounds;

    protected boolean sourceOsmFile;
    protected OsmFileDef osmFile;


    public OsmTexture(Config config) {
        log.debug("Using OpenStreetMap as source for the map's texture");
        this.bounds = Area.of(config.getBoundsTrans());

        if(config.getTextureTrans() instanceof OsmDef) {
            this.osm = (OsmDef) config.getTextureTrans();
            if(osm instanceof OsmFileDef) {
                this.sourceOsmFile = true;
                this.osmFile = (OsmFileDef) osm;
            }
        } else {
            throw new IllegalArgumentException("OsmTexture cannot be created: Configuration invalid!");
        }
    }


    @Override
    public int[][] generateTexture() throws TextureProcessingException {

        // just draw some grass, if no texture is defined
        try {
            if(osm.entities.size() < 1) {
                return fallBackToGrass();
            }
        } catch(Exception e) {
            return fallBackToGrass();
        }

    	log.info("The ground textures will be generated for an area of {}x{}km, " +
                "with the center at ({}). Data source: OpenStreetMap via {}.",
                rounds(bounds.getWidthKm()),
                rounds(bounds.getHeightKm()),
                bounds.getCenter().toString(),
                sourceOsmFile ? "custom OSM XML File" : "Overpass API");

    	// get source data (download or file)
    	SimpleOsmDump osmDump = null;
    	if(sourceOsmFile) {
    	    File osmXml = new File(osmFile.osmXmlFilePath);
    	    if(osmXml.isFile() && osmXml.canRead()) {
    	        osmDump = retrieveOsmData(osmXml);
    	    } else {
    	        throw new TextureProcessingException(
    	                "The File denoted by \"" + osmFile.osmXmlFilePath +
    	                "\" cannot be read or is not a valid File.");
    	    }
    	} else {
    	    osmDump = retrieveOsmData();
    	}

    	Raster ras = drawImage(osmDump);

    	log.debug("Converting rendered image to native CiM2-Texture data");
        return convertImage(ras);
    }

    protected int[][] fallBackToGrass() {
        log.info("No data has been defined for the ground texture, I'll just draw some grass...");
        SingleTexture st = new SingleTexture();
        return st.generateTexture();
    }

    /**
     * Downloads the OSM data for the current configuration from the Overpass
     * servers and creates an object representation of the retrieved data
     * @return an object representation of the retrieved data
     * @throws TextureProcessingException if something goes wrong while getting
     * the data (either from cache or the overpass servers)
     */
    protected SimpleOsmDump retrieveOsmData() throws TextureProcessingException {
    	// osm.xml: download / get from cache
        OverpassBridge ob = new OverpassBridge(bounds, osm);
        File osmXml = ob.getResult();
        return retrieveOsmData(osmXml);
    }

    /**
     * Creates an object representation of the specified OSM XML File
     * @param osmXml the OSM XML file to read from
     * @return an object representation of the specified file
     * @throws TextureProcessingException if parsing of OSM XML fails
     */
    protected SimpleOsmDump retrieveOsmData(File osmXml) throws TextureProcessingException {
        log.debug("Parsing OSM XML with a little help from Osmosis API");

        try {
            return SimpleOsmDump.readOsmXml(osmXml);
        } catch(OsmosisRuntimeException e) {
            throw new TextureProcessingException(e);
        }
    }

    protected Raster drawImage(SimpleOsmDump sink) throws TextureProcessingException {

        // prepare for rendering
        log.debug("Preparing OSM data for rendering");
        EntityConverter ec = new EntityConverter(osm, sink);
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
                int texture = Texture.draw((int) (r*alpha), (int) (g*alpha), (int) (b*alpha));

                result[y][x] = texture;
            }
        }

        return result;
    }

}
