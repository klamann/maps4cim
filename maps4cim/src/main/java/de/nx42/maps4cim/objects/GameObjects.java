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
package de.nx42.maps4cim.objects;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract definition of the binary footer to use for the current map.
 * 
 * Contains all objects that are placed on the current map, like roads,
 * buildings, trees, etc.
 * As with the header, it is also possible to just insert a static footer from
 * an empty map with no objects at all. This is implemented in
 * {@link StaticGameObjects}..
 * 
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public abstract class GameObjects {

    private static Logger log = LoggerFactory.getLogger(GameObjects.class);

    /**
     * Generates the bytes that shall be written in the file footer.
     * @return the footer bytes
     * @throws IOException if some stored binaries can't be accessed
     */
    public abstract byte[] generateGameObjects() throws IOException;

    /**
     * Calls the generateGameObjects function and writes the result to the
     * specified stream
     * @param out the OutputStream to write
     * @throws IOException see {@link GameObjects#generateGameObjects()}
     */
    public void writeTo(OutputStream out) throws IOException {
        byte[] objects = generateGameObjects();

        log.info("Writing game objects (postfix)...");
        out.write(objects);
    }

}
