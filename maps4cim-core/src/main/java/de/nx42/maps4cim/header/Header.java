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
package de.nx42.maps4cim.header;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract definition of the binary header to use for the current map.
 *
 * A definition of the contents can be found in the MapFileFormat.md in the
 * docs folder of this source repository or under
 * https://github.com/Klamann/maps4cim/blob/master/docs/MapFileFormat.md
 *
 * It is possible to just insert a static header from an empty map, as used in
 * the {@link StaticHeader}-Implementation, or to generate a custom header
 * as in {@link CustomHeader}.
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public abstract class Header {

    private static Logger log = LoggerFactory.getLogger(Header.class);

    /**
     * Generates the bytes that shall be written in the file header.
     * @return the header bytes
     * @throws IOException if some stored binaries can't be accessed
     */
    public abstract byte[] generateHeader() throws IOException;

    /**
     * Calls the generateHeader function and writes the result to the
     * specified stream
     * @param out the OutputStream to write
     * @throws IOException see {@link Header#generateHeader()}
     */
    public void writeTo(OutputStream out) throws IOException {
        byte[] header = generateHeader();

        log.info("Writing file header...");
        out.write(header);
    }

}
