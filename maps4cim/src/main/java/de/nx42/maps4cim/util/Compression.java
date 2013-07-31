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
package de.nx42.maps4cim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.io.ByteStreams;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/**
 * Provides static functions to handle compression and decompression of files,
 * using the standard ZIP file format.
 * 
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Compression {

    /**
     * Reads the first file entry in a zip file and returns it's contents
     * as uncompressed byte-array
     * @param zipFile the zip file to read from
     * @return the first file entry (uncompressed)
     * @throws IOException if there is an error accessing the zip file
     */
    public static byte[] readFirstZipEntry(File zipFile) throws IOException {
        // open zip
        ZipFile zf = new ZipFile(zipFile);
        Enumeration<ZipArchiveEntry> entries = zf.getEntries();

        // read first entry to byte[]
        ZipArchiveEntry entry = entries.nextElement();
        InputStream is = zf.getInputStream(entry);
        byte[] raw = ByteStreams.toByteArray(is);

        // close all streams and return byte[]
        is.close();
        zf.close();
        return raw;
    }

    /**
     * Reads the first file entry in a zip file and writes it in uncompressed
     * form to the desired file.
     * @param zipFile the zip file to read from
     * @param dest the file to write the first zip file entry to
     * @return same as destination
     * @throws IOException if there is an error accessing the zip file or the
     * destination file
     */
    public static File readFirstZipEntry(File zipFile, File dest) throws IOException {
        // open zip and get first entry
        ZipFile zf = new ZipFile(zipFile);
        Enumeration<ZipArchiveEntry> entries = zf.getEntries();
        ZipArchiveEntry entry = entries.nextElement();

        // write to file
        InputStream in = zf.getInputStream(entry);
        OutputStream out = new FileOutputStream(dest);
        ByteStreams.copy(in, out);

        // close all streams and return the new file
        in.close();
        out.close();
        zf.close();
        return dest;
    }

    /**
     * Compresses the input file using the zip file format and stores the
     * resulting zip file in the desired location
     * @param input the file to compress
     * @param zipOutput the resulting zip file
     * @return the resulting zip file
     * @throws IOException if there is an error accessing the input file or
     * writing the output zip file
     */
    public static File storeAsZip(File input, File zipOutput) throws IOException {
    	// create new zip output stream
    	ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipOutput));
    	ZipEntry ze = new ZipEntry(input.getName());
    	zos.putNextEntry(ze);

    	// use file as input stream and copy bytes
    	InputStream in = new FileInputStream(input);
    	ByteStreams.copy(in, zos);

    	// close current zip entry and all streams
    	zos.closeEntry();
    	in.close();
    	zos.close();
    	return zipOutput;
    }

}
