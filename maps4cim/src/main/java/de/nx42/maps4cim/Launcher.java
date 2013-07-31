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
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import org.apache.log4j.Appender;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import de.nx42.maps4cim.config.Config;
import de.nx42.maps4cim.config.ReliefDef;
import de.nx42.maps4cim.config.TextureDef;
import de.nx42.maps4cim.config.bounds.CenterDef;
import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.config.texture.EntityDef;
import de.nx42.maps4cim.map.ex.MapGeneratorException;
import de.nx42.maps4cim.util.Result;
import de.nx42.maps4cim.util.Serializer;
import de.nx42.maps4cim.util.gis.Coordinate;


/**
 * Program launcher and Command Line Parser
 * 
 * <pre>
 * Usage: <main class> [options]
 *   Options:
 *     -ce, --center
 *        The center of the map. This parameter requires exactly two values:
 *        latitude and longitude, each as decimal numbers split by a single comma WITHOUT
 *        whitespace in between. A valid input would be -c 48.5,11.5
 *     -c, --config
 *        Path to the config.xml file that specifies all program options. If the
 *        config.xml is used (which is highly recommended), it will override all other options
 *        specified through command line args (except for the output path).
 *     -e, --extent
 *        The extent of the map in kilometers. As the resulting map will be
 *        quadratic, this setting equals the edge length of the map.
 *     -hs, --height-scale
 *        The height-scale to apply on the elevation-data of the current map. The
 *        default value of 1.0 is usually sufficient, but for large maps a lower scale
 *        might be necessary.
 *     -h, -?, --help
 *        Prints these usage instructions.
 *        Default: false
 *     -o, --output
 *        Path to the file where the resulting map shall be written into.
 *        Overwrites any existing file without warning!
 * </pre>
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Launcher {

    private static final Logger log = LoggerFactory.getLogger(Launcher.class);

    @Parameter(names = { "-h", "-?", "--help" },
    		   description = "Prints these usage instructions." )
    private boolean help;

    @Parameter(names = { "-c", "--config" },
    		   converter = FileConverter.class,
    		   validateWith = FileValidator.class,
    		   description = "Path to the config.xml file that specifies all " +
    		   		"program options. If the config.xml is used (which is highly " +
    		   		"recommended), it will override all other options specified " +
    		   		"through command line args (except for the output path)." )
    protected File config;

    @Parameter(names = { "-o", "--output" },
    		   converter = FileConverter.class,
    		   validateWith = FileValidator.class,
    		   description = "Path to the file where the resulting map shall be " +
    		   		"written into. Overwrites any existing file without warning!" )
    protected File output;

    @Parameter(names = { "-ce", "--center" },
    		   converter = CoordinateConverter.class,
    		   validateWith = CoordinateValidator.class,
    		   description = "The center of the map. This parameter requires " +
    		   		"exactly two values: latitude and longitude, each as decimal " +
    		   		"numbers split by a single comma WITHOUT whitespace in " +
    		   		"between. A valid input would be -c 48.5,11.5" )
    protected Coordinate center;

    @Parameter(names = { "-e", "--extent" },
    		   converter = DoubleConverter.class,
    		   validateWith = DoubleValidator.class,
    		   description = "The extent of the map in kilometers. As the " +
    		   		"resulting map will be quadratic, this setting equals the " +
    		   		"edge length of the map." )
    protected Double extent;

    @Parameter(names = { "-hs", "--height-scale" },
 		       converter = DoubleConverter.class,
 		       validateWith = DoubleValidator.class,
 		       description = "The height-scale to apply on the elevation-data " +
 		       		"of the current map. The default value of 1.0 is usually " +
 		       		"sufficient, but for large maps a lower scale might be " +
 		       		"necessary." )
    protected Double heightScale;


    /**
     * Quick run with arg: -c target/classes/sample-config.xml
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		initLogger();

		// parse command line args and launch (if possible)
		try {
			Launcher l = new Launcher();
			l.parse(args);

			if(l.config != null) {
				l.runWithConfig();
			} else {
				l.runWithArgs();
			}
		} catch(ParameterException e) {
			log.error(e.getMessage());
		}

    }

    /**
     * Changes the logger configuration, so the logfile is written to the
     * correct file in the user directory.
     * This method should be called before the first log entry is done, or
     * a new log file will be created in the directory of the executable.
     */
	public static void initLogger() {
		File logFile = new File(ResourceLoader.getAppDir(), "maps4cim.log");
		Appender appender = org.apache.log4j.Logger.getRootLogger().getAppender("FA");
		RollingFileAppender fa = (RollingFileAppender) appender;
		fa.setFile(logFile.getAbsolutePath());
		fa.activateOptions();
		log.debug("---------- NEW SESSION ----------");
		log.debug("Writing log to {}", logFile.getAbsoluteFile());
	}

	/**
	 * If a config XML file was specified by the user, run the application
	 * with the settings specified in the xml.
	 * The only other argument that is considered is the output path of the
	 * resulting map. The output is optional, a default path is available as
	 * fallback.
	 */
    protected void runWithConfig() {

    	// required: config (file)
    	// optional: output (file)

    	try {
            Config conf = Serializer.deserialize(Config.class, config);
            File out = output == null ? getDefaultOutput() : output;
            runMapGenerator(conf, out);
        } catch (Exception e) {
            log.error("reading xml config failed", e);
        }
    }

    /**
     * Runs the program without a user defined config XML. Just the basic
     * information that can be entered over the command line is used.
     * The minimum required information is the center coordinate, all
     * other settings (output file path, map extent, height scale) are
     * optional.
     */
    protected void runWithArgs() {
        
    	// required: center (Coordinate)
    	// optional: ouput (file), extent (double), heightScale (double)

    	// check validity
    	if(center == null) {
    		throw new ParameterException("No valid config.xml or center " +
    				"defined. Can't generate a map based on no information " +
    				"at all...");
    	}

    	// set bounds
		Config c = getDefaultConfig();
		c.bounds = new CenterDef() {{
				centerLat = center.getLatitude();
				centerLon = center.getLongitude();
				extent = extent == null ? 8.0 : Launcher.this.extent;
				unit = Unit.KM;
		}};

		// set scale
		if (heightScale != null) {
			c.relief = new ReliefDef() {{
					heightScale = Launcher.this.heightScale;
			}};
		}

		// run
		File out = output == null ? getDefaultOutput() : output;
		runMapGenerator(c, out);
    }

    /**
     * Creates a new JCommander instance and parses the command line args
     * If no args are specified or the help arg is provided, the help text
     * is printed to stdout.
     * @param args the command line args to parse (String[] as provided by main)
     * @throws ParameterException if invalid args are used
     */
    protected void parse(String[] args) throws ParameterException {
    	JCommander jc = new JCommander(this);
    	jc.parse(args);
		if (this.help || args.length < 1) {
			jc.usage();
		}
    }



    /**
     * Launches the map generator using the specified configuration
     * and writes the resulting map in a new file in the current context
     * 
     * Returns true, iff the map was generated without errors.
     * @param conf the map is generated based on the information provided in
     *             this config
     * @return true, iff the map was generated without errors
     */
    public static boolean runMapGenerator(Config conf) {
    	return runMapGenerator(conf, getDefaultOutput());
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
    public static boolean runMapGenerator(Config conf, File dest) {

    	try {
    		final Stopwatch stopwatch = new Stopwatch();
            stopwatch.start();

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
    
    /**
     * Creates a default configuration, ready to use. Can be adapted easily
     * @return basic configuration object
     */
    protected static Config getDefaultConfig() {
        return new Config() {{
            bounds = new CenterDef() {{
                centerLat = 48.401;
                centerLon = 11.738;
                extent = 8.0;
                unit = Unit.KM;
            }};
            relief = new ReliefDef() {{
                heightScale = 1.0;
            }};
            texture = new TextureDef() {{
                colors = ColorDef.getDefaults();
                entities = EntityDef.getDefaults();
            }};
        }};
    }

    /**
     * @return a new map file in the current context
     */
    protected static File getDefaultOutput() {
    	return new File("maps4cim-generated.map");
    }


    // cmd-line arg converters

    /**
     * Converts a String to a pojo File
     */
	public static class FileConverter implements IStringConverter<File> {
		@Override
		public File convert(String value) {
			return new File(value);
		}
	}

	/**
	 * Converts a String to a Double
	 */
	public static class DoubleConverter implements IStringConverter<Double> {
		@Override
		public Double convert(String value) {
			return Double.parseDouble(value);
		}
	}

	/**
     * Converts two decimal numbers split by a single comma WITHOUT
     * whitespace in between to a world Coordinate object.
     * A valid input would be "48.5,11.5"
     */
	public static class CoordinateConverter implements IStringConverter<Coordinate> {
		@Override
		public Coordinate convert(String value) {
			Iterator<String> args = Splitter.on(',').trimResults().split(value).iterator();
			double lat = Double.parseDouble(args.next());
			double lon = Double.parseDouble(args.next());
			return new Coordinate(lat, lon);
		}
	}

	// cmd-line arg validators

	/**
	 * Checks if a String can be converted to a java File object
	 */
	public static class FileValidator implements IParameterValidator {
		@Override
        public void validate(String name, String value) throws ParameterException {
			try {
				File f = new File(value);
				f.getCanonicalPath();
			} catch (IOException e) {
				throw new ParameterException(String.format(
					"Parameter %s \"%s\" does not seem to represent a valid file path: %s",
					name, value, e.getMessage())
				);
			}
		}
	}

	/**
     * Checks if a String can be converted to a Double
     */
	public static class DoubleValidator implements IParameterValidator {
		@Override
        public void validate(String name, String value) throws ParameterException {
			try {
				Double.parseDouble(value);
			} catch (NumberFormatException e) {
				throw new ParameterException(String.format(
					"Parameter %s \"%s\" could not be interpreted as decimal: %s",
					name, value, e.getMessage())
				);
			}
		}
	}

	/**
     * Checks if a String can be converted to a world Coordinate (lat,lon)
     */
	public static class CoordinateValidator implements IParameterValidator {
		@Override
        public void validate(String name, String value) throws ParameterException {
			try {
				Iterator<String> argsIt = Splitter.on(',')
						.trimResults().omitEmptyStrings().split(value).iterator();
				List<String> args = Lists.newArrayList(argsIt);

				if(args.size() >= 2) {
					try {
						double lat = Double.parseDouble(args.get(0));
						double lon = Double.parseDouble(args.get(1));
						new Coordinate(lat, lon);
					} catch(NumberFormatException e) {
						throw new ParameterException(String.format(
								"One of the values could not be interpreted as " +
								"decimal: %s", name, value, e.getMessage())
							);
					}
				} else {
					throw new ParameterException("You have to specify the center " +
							"with exactly two numers separated by a single comma, " +
							"with no whitespace in between, e.g. 48.5,11.5");
				}
			} catch (Exception e) {
				throw new ParameterException(String.format(
					"Parameter %s \"%s\" could not be interpreted as coordinate: %s",
					name, value, e.getMessage())
				);
			}
		}
	}

}