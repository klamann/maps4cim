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
package de.nx42.maps4cim.map;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nx42.maps4cim.ResourceLoader;
import de.nx42.maps4cim.util.DateUtils;

public class Cache {

    private static final Logger log = LoggerFactory.getLogger(Cache.class);

    public static final File cacheDir = new File(ResourceLoader.appdata, "cache");
    public static final File tempDir = new File(ResourceLoader.appdata, "temp");

    protected static final Pattern osmXmlPattern = Pattern.compile("osm.+xml\\.zip$");
    protected static final Pattern srtmPattern = Pattern.compile("(N|S).+hgt\\.zip$");
    protected static final File osmTileDir = new File(cacheDir, "tile.openstreetmap.org");

    public Cache() {
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if(!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    /**
     * Checks if the specified entry is cached
     * @param entry the name of the cached file
     * @return true, if the file exists in the cache
     */
    public boolean has(String entry) {
        File f = new File(cacheDir, entry);
        return f.isFile();
    }

    /**
     * Creates a file reference to the specified entry in the cache.
     * @param entry the name of the cached file
     * @return a reference to the cached file
     * @throws RuntimeException if the specified entry does not exist
     * (check for existence with {@link Cache#has(String)})
     */
    public File get(String entry) {
        File f = new File(cacheDir, entry);
        if(f.isFile()) {
            return f;
        } else {
            throw new RuntimeException(String.format("The file %s could not " +
                    "be found in the cache", entry));
        }
    }

    public File getUnchecked(String entry) {
        return new File(cacheDir, entry);
    }

    /**
     * Creates a reference to the specified new entry that must not exist in
     * the cache right now. Returns a reference to a new file in the temp
     * folder which must later be moved to the cache with {@link #moveToCache(File)}
     * @param entry the name of the file to cache
     * @return an empty file reference to the file to cache
     * @throws RuntimeException if the specified entry does already exist
     * (check for existence with {@link Cache#has(String)})
     */
    public File allocate(String entry) {
        File cacheFile = new File(cacheDir, entry);
        File tempFile = new File(tempDir, entry);
        if(cacheFile.exists()) {
            throw new RuntimeException(String.format("The file %s does already " +
                    "exist in the cache!", entry));
        } else if(tempFile.exists()) {
            throw new RuntimeException(String.format("The file %s does already " +
                    "exist in the temp folder!", entry));
        } else {
            return tempFile;
        }
    }

    public File moveToCache(File toCache, boolean overwrite) throws IOException {
        File dest = new File(cacheDir, toCache.getName());
        if(dest.exists()) {
            if(overwrite) {
                dest.delete();
            } else {
                throw new IOException(String.format("The file %s does " +
                        "already exist in the cache!", dest));
            }
        }

        Files.move(toCache, dest);
        return dest;
    }

    public File moveToCache(File toCache) throws IOException {
        return moveToCache(toCache, false);
    }

    public File getCacheDir() {
        return cacheDir;
    }

    /**
     * Creates a reference to the specified new entry in the temp directory.
     * The file will be overwritten, if it already exists
     * @param entry the name of the file to write in the temp dir
     * @return a reference to the temporary file
     */
    public static File temporaray(String entry) {
        if(!tempDir.exists()) {
            tempDir.mkdirs();
        }
        return new File(tempDir, entry);
    }

    public static void clearTemp() {
        deleteFolder(tempDir);
    }

    /**
     * Deletes the specified folder and all of it's contents.
     * Beware of evil Hardlink-loops ;)
     * @param folder the folder to delete
     */
    public static void deleteFolder(File folder) {
        deleteAllFiles(folder);
        folder.delete();
    }

    /**
     * Deletes all files and folders in the specified folder
     * @param folder the folder that will be swiped clean
     */
    public static void deleteAllFiles(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    public static void clearCache() {
        log.debug("Cache directory will be cleared!");
        deleteAllFiles(cacheDir);
    }

    public static void clearCache(boolean mapTiles, boolean srtm, boolean osmXml) {
        // delete map tiles (for openstreetmap view on main screen)
        if(mapTiles) {
            log.debug("Removing cached OpenStreetMap-tiles");
            deleteFolder(osmTileDir);
        }

        // delete SRTM tiles
        if(srtm) {
            log.debug("Removing cached SRTM-tiles");
            clearFilesMatchingPattern(cacheDir, srtmPattern);
        }

        // delete cached OSM XML files
        if(osmXml) {
            log.debug("Removing cached OpenStreetMap XML files");
            clearFilesMatchingPattern(cacheDir, osmXmlPattern);
        }
    }

    protected static void clearFilesMatchingPattern(File folder, Pattern p) {
        for (File f : getFilesMatchingPattern(folder, p)) {
            f.delete();
            log.debug("File {} deleted", f.getName());
        }
    }

    protected static List<File> getFilesMatchingPattern(File folder, Pattern p) {
        File[] files = folder.listFiles();
        List<File> matches = new LinkedList<File>();
        for (File f : files) {
            if(f.isFile() && p.matcher(f.getName()).matches()) {
                matches.add(f);
            }
        }
        return matches;
    }

    /**
     * The cache janitor removes old cache entries from time to time
     */
    public static class Janitor {

        /** number of days to keep map tiles */
        protected double keepMapTiles = 60;
        /** number of days to keep downloaded OSM XML files */
        protected double keepOsmXml = 30;
        /** number of days to keep SRTM-tiles */
        protected double keepSRTM = 365;

        /** number of milliseconds to pause between file operations (reduces load)
         *  set to 0 to run at full speed (might cause high disk load! */
        protected long fileAccessPause = 50;
        
        public Janitor() {}

        public Janitor(double keepMapTiles, double keepOsmXml, double keepSRTM, long fileAccessPause) {
            this.keepMapTiles = keepMapTiles;
            this.keepOsmXml = keepOsmXml;
            this.keepSRTM = keepSRTM;
            this.fileAccessPause = fileAccessPause;
        }

        /**
         * Main task of the Janitor: Searches for deprecated files in the cache
         * and deletes them, in accordance with the specified settings.
         */
        public void wipeCache() {
            // OSM XML data
            List<File> osmXmlFiles = getFilesMatchingPattern(cacheDir, osmXmlPattern);
            deleteFilesOlderThan(osmXmlFiles, DateUtils.getDaysBeforeToday(keepOsmXml), fileAccessPause);

            // SRTM data
            List<File> srtmFiles = getFilesMatchingPattern(cacheDir, srtmPattern);
            deleteFilesOlderThan(srtmFiles, DateUtils.getDaysBeforeToday(keepSRTM), fileAccessPause);

            // OSM Tiles
            deleteFilesOlderThanRecursively(osmTileDir, DateUtils.getDaysBeforeToday(keepMapTiles), fileAccessPause);
        }

        protected void deleteFilesOlderThan(List<File> files, Date before, long pause) {
            long beforeMs = before.getTime();
            for (File f : files) {
                if(f.lastModified() < beforeMs) {
                    f.delete();
                    //log.debug("File {} deleted by cache janitor", f);

                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException e) {
                        log.warn("error while running cache janitor", e);
                    }
                }
            }
        }

        /**
         * Deletes all files in the specified folder and all of it's subfolders
         * recursively, if their last modification was before the specified date.
         * @param folder the directory to start the search from
         * @param before only files that were last modified before this date are affected
         * @param pause number of milliseconds to pause between each file access
         */
        protected void deleteFilesOlderThanRecursively(File folder, Date before, long pause) {
            long beforeMs = before.getTime();

            // go slow..
            if(pause > 0) {
                try {
                    Thread.sleep(pause);
                } catch (InterruptedException e) {
                    log.warn("error while running cache janitor", e);
                }
            }
            
            File[] files = folder.listFiles();
            if (files != null) { // some JVMs return null for empty dirs
                for (File f : files) {
                    // recurse or compare dates
                    if (f.isDirectory()) {
                        deleteFilesOlderThanRecursively(f, before, pause);
                    } else {
                        if (f.lastModified() < beforeMs) {
                            f.delete();
                            //log.debug("File {} deleted by cache janitor", f);
                        }
                    }
                }
            }

            // delete folder, if empty
            if (folder.listFiles().length == 0) {
                folder.delete();
            }
        }

    }

}
