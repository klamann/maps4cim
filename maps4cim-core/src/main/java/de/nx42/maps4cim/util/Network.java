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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import com.google.common.io.ByteStreams;

/**
 * Collection of networking functions
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class Network {

	/**
	 * Downloads the contents of a resource specified by the src URL to
	 * the dest File. The resulting file is a byte-by-byte copy.
	 * gzip transfer encoding is accepted and handled transparently, no further
	 * action required.
	 * @param src the source URL to read from
	 * @param dest the destination file to write to
	 * @param connTimeout seconds until timeout, if no connection can be established
	 * (this does NOT include download time, just a server response)
	 * @param readTimeout seconds until read timeout. This is the time until
	 * the server has to provide a file to transfer. Set this to a reasonably
	 * high value, if you ask for computationally intensive query results from
	 * the server.
	 * @throws IOException if the file can't be downloaded
	 * @throws SocketTimeoutException if the connection times out
	 */
	public static void downloadToFile(URL src, File dest, double connTimeout,
			double readTimeout) throws IOException, SocketTimeoutException {

		URLConnection conn = openConection(src);
		conn.setConnectTimeout((int) (connTimeout * 1000));
		conn.setReadTimeout((int) (readTimeout * 1000));

        storeConnectionAnswer(conn, dest);
	}
	
	public static void downloadToFile(URL src, File dest) throws IOException {
        URLConnection conn = openConection(src);
        storeConnectionAnswer(conn, dest);
    }

	public static boolean exists(URL url) {
        try {
			HttpURLConnection conn = openConection(url);
			conn.setRequestMethod("HEAD");
            int code = conn.getResponseCode();
			return code >= 200 && code < 300;
        } catch (IOException e) {
            return false;
        }
    }

	private static HttpURLConnection openConection(URL src) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) src.openConnection();
        conn.setRequestProperty("Accept-Encoding", "gzip");
		conn.setRequestProperty("User-Agent", "maps4cim ");
        return conn;
	}

	private static void storeConnectionAnswer(URLConnection conn, File dest) throws IOException {
	    // create input stream and upgrade to gzip, if required
	    InputStream in = conn.getInputStream();
        if ("gzip".equals(conn.getContentEncoding())) {
            in = new GZIPInputStream(in);
        }
        
        // create output stream to file and copy data
        OutputStream out = new FileOutputStream(dest);
        ByteStreams.copy(in, out);

        // close all streams
        in.close();
        out.close();
	}

}
