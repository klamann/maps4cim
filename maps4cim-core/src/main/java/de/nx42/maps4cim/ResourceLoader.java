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

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.google.common.io.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.map.Cache;

/**
 * Loads static resources from the classpath and transforms serialized
 * objects. Also, handles the local appdata directory.
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class ResourceLoader {

    private static final Logger log = LoggerFactory.getLogger(ResourceLoader.class);

	/** the name of this application's data directory */
    public static final String appdataDirName = "maps4cim";
	/** the current user's application data directory / maps4cim's home
	 *  directory. might vary between different operating systems. */
    public static final File appdata = locateAppDir();

    /** classpath to the cim2-resources */
    protected static final String resourcesBase = "de/nx42/maps4cim/res/";
    /** classpath to the cim2-resources */
    protected static final String classpathCim2 = resourcesBase + "cim2/";
    /** classpath to the srtm-resources */
    protected static final String classpathSrtm = resourcesBase + "srtm/";

    // base path for maps4cim (Preferences-API)
    protected static final String preferencesPath = "/de/nx42/maps4cim";
    // Preferences-object for maps4cim
    protected static final Preferences preferences = Preferences.userRoot().node( preferencesPath );


    // read resource files


    /**
     * Retrieves an open InputStream for the specified resource or null if the
     * resource does not exist or was not found
     * @param res the classpath of the resource to open
     * @return an open InputStream for the resource
     */
    public static InputStream get(String res) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(res);
    }

    /**
     * Adds the Resource-BasePath to the specified name. Resources in this
     * application are not stored in the root of the class path but in a
     * subdirectory that fits the package structure of this application.
     * This prevents collisions and keeps bundles containing all dependencies
     * clean
     * @param res the string where the base path is inserted
     * @return basepath/res
     */
    public static String addBasePath(String res) {
        return resourcesBase + res;
    }

    // scenario-specific getters

    /**
     * Reads the static map header and returns it as byte-array.
     * @return the static map header
     * @throws IOException if the resource cannot be accessed
     */
    public static byte[] getStaticMapHeader() throws IOException {
        return readBase64Resource(classpathCim2 + "prefix.base64");
    }

    /**
     * Reads the static map footer and returns it as byte-array.
     * @return the static map footer
     * @throws IOException if the resource cannot be accessed
     */
    public static byte[] getStaticMapFooter() throws IOException {
        return readBase64Resource(classpathCim2 + "postfix.base64");
    }

    /**
     * Retrieves the SRTM-Mapping (Table<Integer,Integer,DownloadURL>),
     * which has been stored as serialized Java object.
     * Can be opened using {@link ResourceLoader#deserializeObject(InputStream)}
     * @return the stored SRTM-Mapping
     */
    public static InputStream getMappingSRTM() {
        return get(classpathSrtm + "srtm-mapping.obj");
    }

    /**
     * Retrieves the messages.properties file for this application.
     * Automatically selects the correct locale by invoking
     * {@link ResourceBundle#getBundle(String)}
     * @return contents of the messages.properties file
     */
    public static ResourceBundle getMessages() {
        return ResourceBundle.getBundle("de/nx42/maps4cim/res/messages");
    }


    // handling of stored data


    /**
     * Reads a base64-encoded Resource from the classpath and returns it as
     * byte-array
     * @param path the path to the resource to load
     * @return the decoded resource as byte-array
     * @throws IllegalArgumentException if the resource is not found
     * @throws IOException if the resource cannot be read as base64
     */
    public static byte[] readBase64Resource(String path) throws IllegalArgumentException, IOException {
        String base64 = Resources.toString(Resources.getResource(path), Charsets.UTF_8);
        String trimmed = CharMatcher.WHITESPACE.removeFrom(base64);
        return BaseEncoding.base64().decode(trimmed);
    }
    
    /**
     * Retrieves an Image that can be opened by {@link Toolkit} from the
     * specified class path
     * @param res the path to the resource to load
     * @return the specified resource as Image
     * @throws IllegalArgumentException if the resource is not found
     */
    public static Image getImageFromResource(String res) throws IllegalArgumentException {
        URL url = Resources.getResource(res);
        return Toolkit.getDefaultToolkit().getImage(url);
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


    // Preferences API


    /**
     * @return the Preferences-Object (Java Preferences API)
     */
    public static Preferences getPrefs() {
        return preferences;
    }

    /**
     * Checks, if the specified key exists in the Preferences (default Java
     * Preferences API). Might not work as expected if null values are stored
     * (storing null values in the Preferences is generally a bad idea...)
     * @param key the key to check
     * @return true, iff the specified key exists and it's value is not null
     */
    public static boolean existsPref(String key) {
        return preferences.get(key, null) != null;
    }


    // cleanup


    public static void uninstall() {
        log.info("All data stored by maps4cim will be deleted by user request.");

        // clear preferences API
        try {
            Preferences.userRoot().node( preferencesPath ).clear();
            Preferences.userRoot().node( "/de/nx42/maps4cim-gui" ).clear();
        } catch (BackingStoreException e) {
            log.error("Could not remove data from preferences", e);
        }

        // delete maps4cim's home directory
        LoggerConfig.stopLogging();
        Cache.deleteFolder(appdata);

    }

}
