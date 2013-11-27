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
package de.nx42.maps4cim;

import java.io.File;
import java.io.FileOutputStream;

import com.google.common.base.Stopwatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.ReliefDef.ReliefSource;
import de.nx42.maps4cim.config.TextureDef.TextureSource;
import de.nx42.maps4cim.header.CustomHeader;
import de.nx42.maps4cim.header.Header;
import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.map.ReliefMap;
import de.nx42.maps4cim.map.TextureMap;
import de.nx42.maps4cim.map.ex.MapGeneratorException;
import de.nx42.maps4cim.map.relief.MandelbrotRelief;
import de.nx42.maps4cim.map.relief.PlanarRelief;
import de.nx42.maps4cim.map.relief.SRTM;
import de.nx42.maps4cim.map.texture.OsmTexture;
import de.nx42.maps4cim.map.texture.SingleTexture;
import de.nx42.maps4cim.objects.GameObjects;
import de.nx42.maps4cim.objects.StaticGameObjects;
import de.nx42.maps4cim.util.Result;

/**
 * The MapGenerator transforms header, reliefmap, texturemap & game objects
 * into a valid map file
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class MapGenerator {

    private static final Logger log = LoggerFactory.getLogger(MapGenerator.class);

    protected Config config;
    protected Header he;
    protected ReliefMap rm;
    protected TextureMap tm;
    protected GameObjects go;

    /**
     * Creates a new map generator instance with the specified config, using the
     * given ReliefMap and TextureMap implementation
     * @param config the configuration object
     * @param rm the ReliefMap implementation to use
     * @param tm the TextureMap implementation to use
     */
    public MapGenerator(Config config, ReliefMap rm, TextureMap tm) {
        log.debug("Initializing the Map Generator...");

        this.config = config;
        this.he = new CustomHeader();
        this.rm = rm;
        this.tm = tm;
        this.go = new StaticGameObjects();
    }

    /**
     * Creates a new map generator instance with the specified config. The
     * ReliefMap and TextureMap implementations are derived from the config,
     * so no need to specify those
     * @param config the configuration object
     */
    public MapGenerator(Config config) {
        this(config, getReliefMap(config), getTextureMap(config));
    }

    /**
     * Generates a new map based on the data from the configuration that was
     * used to create this object.
     * Returns a result object which specifies, if the operation was successful
     * @param output the file to write the resulting map to
     * @return a result object, containing the success message or failure with
     *         a detailed report
     * @throws MapGeneratorException any exception that cannot be fixed by
     *                               the program
     */
	public Result generateMap(File output) throws MapGeneratorException {
	    Result res = new Result("generating map", true);
		try {
			// TODO write temporary file first, then move
			// TODO allow writing of relief diff, based on texture data

			FileOutputStream fos = new FileOutputStream(output);
			log.info("Writing resulting map to file {}", output.toString());

			// header
			he.writeTo(fos);

			// relief
			try {
			    rm.writeTo(fos);
			} catch(MapGeneratorException e) {
			    log.error("Error while processing the relief map, falling back to a simple planar relief.", e);
			    res.failure("the intended relief map could not be generated");
			    ReliefMap planar = new PlanarRelief();
			    planar.writeTo(fos);
			}

			// texture
			try {
			    tm.writeTo(fos);
            } catch(MapGeneratorException e) {
                log.error("Error while processing the texture map, falling back to a simple grass texture.");
                res.failure("the intended texture map could not be generated");
                TextureMap simple = new SingleTexture();
                simple.writeTo(fos);
            }

			// game objects
			go.writeTo(fos);

			// close the streams, return result
			fos.flush();
			fos.close();
			return res;
		} catch(Exception e) {
		    log.error("Error generating map", e);
		    throw new MapGeneratorException(e);
		} finally {
			Cache.clearTemp();
		}
	}

	// static launcher methods, including a stopwatch

    /**
     * Launches the map generator using the specified configuration
     * and writes the resulting map in a new file in the current context
     *
     * Returns true, iff the map was generated without errors.
     * @param conf the map is generated based on the information provided in
     *             this config
     * @return true, iff the map was generated without errors
     */
    public static boolean execute(Config conf) {
    	return execute(conf, getDefaultOutput());
    }

    /**
     * Launches the map generator using the specified configuration
     * and writes the resulting map to the specified file.
     *
     * Returns true, iff the map was generated without errors.
     *
     * @param conf the map is generated based on the information provided in
     *             this config
     * @param dest the file where the resulting map is written to
     * @return true, iff the map was generated without errors
     */
    public static boolean execute(Config conf, File dest) {

    	try {
    	    final Stopwatch stopwatch = Stopwatch.createStarted();

            log.info("Map Generator has been started.");

            MapGenerator mg = new MapGenerator(conf);

            try {
                log.debug("Ressources initialized. The map will now be written...");
                Result res = mg.generateMap(dest);

                stopwatch.stop();
                if(res.isSuccess()) {
                    log.info("The map has been successfully generated. Total Time: {}", stopwatch.toString());
                    log.info("If you plan to publish this map, please attribute correctly. "
                            + "You can copy the following text to do so:\n"
                            + "This map was created using maps4cim, with data "
                            + "from the OpenStreetMap (© OpenStreetMap contributors).");
                    return true;
                } else {
                    log.warn(res.getReport());
                    log.warn("Something went wrong, so your map has been generated "
                            + "in fallback mode (probably it's just empty).\n"
                            + "Please review the errors and post this log in the "
                            + "forums if you don't know how to fix them.");
                    return false;
                }
            } catch (MapGeneratorException e) {
                // errors are already logged
                return false;
            }
    	} catch(Exception e) {
    		log.error("Unexpected Exception", e);
    		return false;
    	}

    }

    // Helpers

    /**
     * @return a new map file in the current context
     */
    protected static File getDefaultOutput() {
    	return new File("maps4cim-generated.map");
    }

    /**
     * Retrieves the correct {@link TextureMap}-Implementation for the specified
     * configuration
     * @param conf the configuration object
     * @return a {@link TextureMap}-Implementation
     */
    protected static TextureMap getTextureMap(Config conf) {
        TextureSource ts = conf.texture.getSource();
        switch (ts) {
            case osm:   return new OsmTexture(conf);
            case none:  return new SingleTexture();
            default:    return new SingleTexture();
        }
    }

    /**
     * Retrieves the correct {@link ReliefMap}-Implementation for the specified
     * configuration
     * @param conf the configuration object
     * @return a {@link ReliefMap}-Implementation
     */
    protected static ReliefMap getReliefMap(Config conf) {
        ReliefSource rs = conf.relief.getSource();
        switch(rs) {
            case srtm:  return new SRTM(conf);
            case mandelbrot: return new MandelbrotRelief();
            case none:  return new PlanarRelief();
            default:    return new PlanarRelief();
        }
    }


    /**
     * removes any character that isn't a number, letter or underscore
     * -> creates strings that are safe for use in file systems
     * One way function only, cannot restore cleaned names...
     * @param name the filename to clean
     * @return filename with valid characters only
     */
    public static String cleanFileName(String name) {
        return name.replaceAll("\\W+", "");
    }

}
