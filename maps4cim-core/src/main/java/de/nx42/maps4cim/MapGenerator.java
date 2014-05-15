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
package de.nx42.maps4cim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.io.Files;

import net.sf.oval.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.header.HeaderDef;
import de.nx42.maps4cim.config.relief.HeightmapDef;
import de.nx42.maps4cim.config.relief.PlanarReliefDef;
import de.nx42.maps4cim.config.relief.ReliefDef;
import de.nx42.maps4cim.config.relief.ReliefDef.ReliefDefNone;
import de.nx42.maps4cim.config.relief.SrtmDef;
import de.nx42.maps4cim.config.texture.ImageDef;
import de.nx42.maps4cim.config.texture.OsmDef;
import de.nx42.maps4cim.config.texture.SingleTextureDef;
import de.nx42.maps4cim.config.texture.TextureDef;
import de.nx42.maps4cim.config.texture.TextureDef.TextureDefNone;
import de.nx42.maps4cim.header.CustomHeader;
import de.nx42.maps4cim.header.Header;
import de.nx42.maps4cim.map.Cache;
import de.nx42.maps4cim.map.ReliefMap;
import de.nx42.maps4cim.map.TextureMap;
import de.nx42.maps4cim.map.ex.ConfigValidationException;
import de.nx42.maps4cim.map.ex.MapGeneratorException;
import de.nx42.maps4cim.map.relief.ImageRelief;
import de.nx42.maps4cim.map.relief.PlanarRelief;
import de.nx42.maps4cim.map.relief.SRTM;
import de.nx42.maps4cim.map.texture.ImageTexture;
import de.nx42.maps4cim.map.texture.OsmTexture;
import de.nx42.maps4cim.map.texture.SingleTexture;
import de.nx42.maps4cim.objects.GameObjects;
import de.nx42.maps4cim.objects.StaticGameObjects;
import de.nx42.maps4cim.update.ProgramVersion;
import de.nx42.maps4cim.update.Update.Branch;
import de.nx42.maps4cim.util.Result;
import de.nx42.maps4cim.util.ValidatorUtils;

/**
 * The MapGenerator transforms header, reliefmap, texturemap & game objects
 * into a valid map file
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class MapGenerator {

    private static final Logger log = LoggerFactory.getLogger(MapGenerator.class);

    public static final ProgramVersion version = new ProgramVersion("1.0.0");
    public static final Branch branch = Branch.stable;

    /** the configuration to work with */
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
    public MapGenerator(Config config, ReliefMap rm, TextureMap tm, Header he, GameObjects go) {
        log.debug("Initializing the Map Generator...");

        this.config = config;
        this.he = he;
        this.rm = rm;
        this.tm = tm;
        this.go = go;
    }

    /**
     * Creates a new map generator instance with the specified config. The
     * ReliefMap and TextureMap implementations are derived from the config,
     * so no need to specify those
     * @param config the configuration object
     */
    public MapGenerator(Config config) {
        this(config, getReliefMap(config), getTextureMap(config), new CustomHeader(config),
                new StaticGameObjects());
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
        // clean up the configuration / avoid nullpointers
        preprocessConfig(output);
        
        Result res = new Result("generating map", true);
        FileOutputStream fos = null;

        try {
            // write to temporary file
            final File tmp = Cache.temporaray(output.getName() + ".tmp");
            fos = new FileOutputStream(tmp);

            // actually write the map to the OutputStream
            log.info("Writing resulting map to file {}", output.toString());
            writeMapToStream(fos, res);

            // close the streams, return result
            fos.flush();
            fos.close();

            // move temporary file
            if (output.exists()) {
                output.delete();
            }
            Files.move(tmp, output);

            return res;
        } catch (Exception e) {
            log.error("Error generating map: " + e.getMessage(), e);
            throw new MapGeneratorException(e);
        } finally {
            // close streams, delete temporary files
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("Error while closing output stream to resulting map", e);
                }
            }
            Cache.clearTemp();
        }
    }

    /**
     * Writes the 4 parts of a map (header, relief, texture, game objects) as
     * defined in this MapGenerator to the specified outputstream.
     * Any errors will be stored in the Result-object.
     * @param out the stream to write the results to
     * @param res the Results-object where error-messages may be stored
     * @throws IOException writeMapToStream
     * @throws MapGeneratorException if anything goes wrong while generating
     * the map's contents
     */
    protected void writeMapToStream(OutputStream out, Result res)
            throws IOException, MapGeneratorException {

        // step 1/4: header
        he.writeTo(out);

        // step 2/4: relief
        try {
            rm.writeTo(out);
        } catch (MapGeneratorException e) {
            log.error("Error while processing the relief map, " + e.print()
                    + "\nFalling back to a simple planar relief.", e);
            res.failure("the intended relief map could not be generated");
            PlanarRelief.write(out);
        } catch (RuntimeException e) {
            log.error("Unexpected Exception while processing the relief map, "
                    + MapGeneratorException.getRootCause(e).toString()
                    + "\nFalling back to a simple planar relief.", e);
            res.failure("the intended relief map could not be generated");
            PlanarRelief.write(out);
        }

        // step 3/4: texture
        try {
            tm.writeTo(out);
        } catch (MapGeneratorException e) {
            log.error("Error while processing the texture map, " + e.print()
                    + "\nFalling back to a simple grass texture.", e);
            res.failure("the intended texture map could not be generated");
            SingleTexture.write(out);
        } catch (RuntimeException e) {
            log.error("Unexpected Exception while processing the texture map, "
                    + MapGeneratorException.getRootCause(e).toString()
                    + "\nFalling back to a simple grass texture.", e);
            res.failure("the intended texture map could not be generated");
            SingleTexture.write(out);
        }

        // step 4/4: game objects
        go.writeTo(out);

    }

    private void preprocessConfig(File output) throws ConfigValidationException {
        // validate
        List<ConstraintViolation> cvs = ValidatorUtils.validateR(config);
        if(!cvs.isEmpty()) {
            log.error("The configuration appears to be invalid:\n" + ValidatorUtils.formatRootCauses(cvs));
            throw new ConfigValidationException(ValidatorUtils.formatCausesRecursively(cvs));
        }
        
        
        // set the relief to none, if nonexistent
        if (config.relief == null || config.relief.value == null) {
            config.relief = ReliefDef.none();
        }

        // set the texture to none, if nonexistent
        if (config.texture == null || config.texture.value == null) {
            config.texture = TextureDef.none();
        }

        // set the internal map name to the filename (without extension), if not specified
        if (config.header == null) {
            config.header = HeaderDef.forFile(output);
        } else if (Strings.isNullOrEmpty(config.header.name)) {
            config.header.name = HeaderDef.getFileNameOnly(output);
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
                if (res.isSuccess()) {
                    log.info("The map has been successfully generated. Total Time: {}",
                             stopwatch.toString());
                    logAttribution(conf);
                    return true;
                } else {
                    log.warn(res.getReport());
                    log.warn("Something went wrong, so your map has been generated "
                            + "in fallback mode (probably it's just empty).\n"
                            + "Please review the errors and post this log in the "
                            + "forums if you are not sure how to fix them.");
                    return false;
                }
            } catch (MapGeneratorException e) {
                // errors are already logged
                return false;
            }
        } catch (Exception e) {
            log.error("Unexpected Exception", e);
            return false;
        }
    }

    /**
     * Logs a message containing information about the required attributions
     * if the user intends to publish the map generated by this application.
     * @param conf the configuration that was used to generate the map
     */
    protected static void logAttribution(Config conf) {
        boolean osm = conf.getTextureTrans() instanceof OsmDef;
        boolean srtm = conf.getReliefTrans() instanceof SrtmDef;
        boolean graphic = conf.getTextureTrans() instanceof ImageDef
                       || conf.getReliefTrans() instanceof HeightmapDef;

        StringBuilder sb = new StringBuilder(256);

        // maps4cim's attribution
        if (osm || srtm) {
            sb.append("This map was built using ");
            if (osm) {
                sb.append("data from the OpenStreetMap (© OpenStreetMap contributors)");
            }
            if (srtm) {
                if (osm) {
                    sb.append(" and ");
                }
                sb.append("relief data from the SRTM-dataset (public domain).");
            }
            sb.append('\n');
        }

        // user required to attribute when sharing?
        if (osm) {
            sb.append("If you plan to publish this map, please attribute correctly. "
                    + "You can copy the following text to do so:\n"
                    + "This map was created using maps4cim, with data "
                    + "from the OpenStreetMap (© OpenStreetMap contributors).");
            if (graphic) {
                sb.append("\nAlso, check the license terms of the images that "
                        + "were used to generate this map!");
            }
        } else if (graphic) {
            sb.append("Your map does not appear to use any data sources that "
                    + "are subject to licensing conditions, but if you plan to "
                    + "share this map, check the license terms of the images "
                    + "you've used to generate this map!");
        } else {
            sb.append("Your map does not use any data which is subject to any "
                    + "license terms, feel free to share it wherever you like :)");
        }

        // log
        log.info(sb.toString());
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
        TextureDef def = conf.getTextureTrans();

        if (def == null || !(def instanceof TextureDef)) {
            log.warn("No valid ground texture definition found, falling back "
                    + "to a simple grass texture.");
            return new SingleTexture();
        } else if (def instanceof OsmDef) {
            // this includes OsmFileDef (subclass)
            return new OsmTexture(conf);
        } else if (def instanceof ImageDef) {
            try {
                return new ImageTexture(conf);
            } catch (IOException e) {
                log.error(
                        "Error creating ground texture from config, falling "
                        + "back to a simple grass texture.", e);
                return new SingleTexture();
            }
        } else if (def instanceof SingleTextureDef) {
            return new SingleTexture(((SingleTextureDef) def).getGround());
        } else if (def instanceof TextureDefNone) {
            return new SingleTexture();
        } else {
            log.warn("Selected texture type not supported, falling back to a "
                    + "simple grass texture.");
            return new SingleTexture();
        }
    }

    /**
     * Retrieves the correct {@link ReliefMap}-Implementation for the specified
     * configuration
     * @param conf the configuration object
     * @return a {@link ReliefMap}-Implementation
     */
    protected static ReliefMap getReliefMap(Config conf) {
        ReliefDef def = conf.getReliefTrans();

        if (def == null || !(def instanceof ReliefDef)) {
            log.warn("No valid relief definition found, falling back to a "
                    + "simple planar relief.");
            return new PlanarRelief();
        } else if (def instanceof SrtmDef) {
            return new SRTM(conf);
        } else if (def instanceof HeightmapDef) {
            try {
                return new ImageRelief(conf);
            } catch (IOException e) {
                log.error(
                        "Error creating image relief from config, falling back "
                        + "to a simple planar relief.", e);
                return new PlanarRelief();
            }
        } else if (def instanceof PlanarReliefDef) {
            return new PlanarRelief(conf);
        } else if (def instanceof ReliefDefNone) {
            return new PlanarRelief();
        } else {
            log.warn("Selected relief type not supported, falling back to a "
                    + "simple planar relief.");
            return new PlanarRelief();
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
