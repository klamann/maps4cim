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

import de.nx42.maps4cim.ResourceLoader;

/**
 * Reads the static map header from the integrated resources and returns it as
 * byte-array
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class StaticHeader extends Header {

    @Override
    public byte[] generateHeader() throws IOException {
        return  ResourceLoader.getStaticMapHeader();
    }

}
