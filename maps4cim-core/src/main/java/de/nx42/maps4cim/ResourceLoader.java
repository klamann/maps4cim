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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.apache.log4j.Appender;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Resources;

/**
 * Loads static resources from the classpath and transforms serialized
 * objects. Also, handles the local appdata directory.
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class ResourceLoader {

	private static final Logger log = LoggerFactory.getLogger(ResourceLoader.class);

	/** the current user's application data directory. might vary between
	    different operating systems */
    public static final File appdata = locateAppDir();
    /** the name of this application's data directory */
    public static final String appdataDirName = "maps4cim";

    /** classpath to the cim2-resources */
    protected static String classpath_cim2 = "cim2/";
    /** classpath to the srtm-resources */
    protected static String classpath_srtm = "srtm/";


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


    // read resource files

    /**
     * Retrieves an open InputStream for the specified resource.
     * @param res the classpath of the resource to open
     * @return an open InputStream for the resource
     */
    public static InputStream get(String res) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(res);
    }

    // scenario-specific getters

    /**
     * Reads the static map header and returns it as byte-array.
     * @return the static map header
     * @throws IOException if the resource cannot be accessed
     */
    public static byte[] getStaticMapHeader() throws IOException {
        return readBase64Resource(classpath_cim2 + "prefix.base64");
    }

    /**
     * Reads the static map footer and returns it as byte-array.
     * @return the static map footer
     * @throws IOException if the resource cannot be accessed
     */
    public static byte[] getStaticMapFooter() throws IOException {
        return readBase64Resource(classpath_cim2 + "postfix.base64");
    }

    /**
     * Retrieves the SRTM-Mapping (Table<Integer,Integer,DownloadURL>),
     * which has been stored as serialized Java object.
     * Can be opened using {@link ResourceLoader#deserializeObject(InputStream)}
     * @return the stored SRTM-Mapping
     */
    public static InputStream getMappingSRTM() {
        return get(classpath_srtm + "srtm-mapping.obj");
    }

    // handling of stored data

    /**
     * Reads a base64-encoded Resource from the classpath and returns it as
     * byte-array
     * @param path the path to the resource to load
     * @return the decoded resource as byte-array
     * @throws IOException if the resource cannot be accessed
     */
    public static byte[] readBase64Resource(String path) throws IOException {
        String base64 = Resources.toString(Resources.getResource(path), Charsets.UTF_8);
        String trimmed = CharMatcher.WHITESPACE.removeFrom(base64);
        return BaseEncoding.base64().decode(trimmed);
    }

    /**
     * Deserializes the contents of the specified file and casts them
     * to the inferred Java object
     * @param file the file to read the serialized object from
     * @return the object representation of the serialized file
     * @throws FileNotFoundException if the specified file does not exist
     * @throws IOException if the specified file cannot be read
     * @throws ClassNotFoundException if the class of the serialized object
     *                                cannot be found
     */
	public static <T> T deserializeObject(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		return deserializeObject(new FileInputStream(file));
    }

	/**
	 * Deserializes the contents of the specified input stream and casts them
	 * to the inferred Java object
	 * @param is the stream to read the serialized object from
	 * @return the object representation of the inputstream
	 * @throws ClassNotFoundException if the class of the serialized object
	 *                                cannot be found
	 * @throws IOException if the inputstream cannot be accessed
	 */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(InputStream is) throws ClassNotFoundException, IOException {
        ObjectInputStream ois = new ObjectInputStream(is);
        T object = (T) ois.readObject();
        ois.close();
        return object;
    }

    // application data directory

    /**
     * Retrieves the current appdata directory and creates it on the fly, if
     * it does not exist yet.
     * @return the appdata directory for this application
     */
    public static File getAppDir() {
    	if(!appdata.exists()) {
    		appdata.mkdirs();
    	}
    	return appdata;
    }

    /**
     * Creates a reference to the application directory.
     * This will be located in the user directory.
     * For windows under %APPDATA%, for all other OS under user.home (but with
     * a leading '.' in the folder name)
     * @return reference to the app's home directory (might need to be created
     *         first!)
     */
    private static final File locateAppDir() {
        File appdir;
        if(System.getProperty("os.name").startsWith("Windows")) {
            String apps = System.getenv("APPDATA");
            appdir = new File(apps, appdataDirName);
        } else {
            String home = System.getProperty("user.home");
            appdir = new File(home, "." + appdataDirName);
        }
        return appdir;
    }


}
