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

import de.nx42.maps4cim.ResourceLoader;

public class Cache {

	protected static final File cacheDir = new File(ResourceLoader.appdata, "cache");
	protected static final File tempDir = new File(ResourceLoader.appdata, "temp");

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
	 * the cache right now.
	 * @param entry the name of the file to cache
	 * @return an empty file reference for the file to cache
	 * @throws RuntimeException if the specified entry does already exist
	 * (check for existence with {@link Cache#has(String)})
	 */
	public File allocate(String entry) {
		File f = new File(cacheDir, entry);
		if(f.exists()) {
			throw new RuntimeException(String.format("The file %s does already " +
					"exist in the cache!", entry));
		} else {
			return f;
		}
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
		return new File(tempDir, entry);
	}

	public static void clearTemp() {
		deleteFolder(tempDir);
	}

	public static void deleteFolder(File folder) {
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
		folder.delete();
	}

}
